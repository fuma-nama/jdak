package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

open class StringCommandOption<T: Any?>(
    name: String, description: String
): TypedCommandOption<T>(name, description, OptionType.STRING) {
    fun len(min: Int? = null, max: Int? = null): StringCommandOption<T> {
        this.minLength = min
        this.maxLength = max

        return this
    }

    fun choice(name: String, v: String, nameLocale: Map<DiscordLocale, String>? = null): T {
        val choice = Choice(name, v)
        nameLocale?.let {
            choice.setNameLocalizations(it)
        }

        choices += choice
        return this as T
    }

    fun choices(vararg choices: Pair<String, String>): StringCommandOption<T> {
        this.choices += choices.map {
            Choice(it.first, it.second)
        }

        return this
    }
}

open class NumberCommandOption<T : Any?>(
    name: String, description: String, type: OptionType
): TypedCommandOption<T>(name, description, type) {
    fun range(min: Number? = null, max: Number? = null): NumberCommandOption<T> {
        this.min = min
        this.max = max

        return this
    }

    fun choices(vararg choices: Pair<String, Number>): NumberCommandOption<T> {
        this.choices += choices.map {(name, value) ->
            when (type) {
                OptionType.NUMBER -> Choice(name, value.toDouble())
                OptionType.INTEGER -> Choice(name, value.toLong())
                else -> error("Invalid option type: $type")
            }
        }

        return this
    }

    fun choice(name: String, v: Number, nameLocale: Map<DiscordLocale, String>? = null): T {
        val choice = when (type) {
            OptionType.NUMBER -> Choice(name, v.toDouble())
            OptionType.INTEGER -> Choice(name, v.toLong())
            else -> error("Invalid option type: $type")
        }

        nameLocale?.let {
            choice.setNameLocalizations(it)
        }

        choices += choice
        return this as T
    }
}

open class ChannelCommandOption<T : Any?>(
    name: String, description: String
): TypedCommandOption<T>(name, description, OptionType.CHANNEL) {
    fun types(vararg types: ChannelType): ChannelCommandOption<T> {
        this.channelTypes = types
        return this
    }
}

open class TypedCommandOption<T : Any?>(
    name: String, description: String, type: OptionType
): CommandOption(name, description, type) {
    fun required(): TypedCommandOption<T & Any> {
        required = true
        return this as TypedCommandOption<T & Any>
    }

    fun optional(): TypedCommandOption<T?> {
        required = false
        return this as TypedCommandOption<T?>
    }

    fun autoComplete(v: Boolean = true): TypedCommandOption<T> {
        autoComplete = v
        return this
    }

    fun autoComplete(handler: (CommandAutoCompleteInteractionEvent) -> Map<String, T>): TypedCommandOption<T> {
        return onAutoComplete {
            val choices = handler(it)

            it.replyChoices(choices.map { (key, v) ->
                when (v) {
                    is Double -> Choice(key, v)
                    is Float -> Choice(key, v.toDouble())
                    is Number -> Choice(key, v.toLong())
                    is String -> Choice(key, v)
                    else -> error("Unexpected auto complete choice: $v")
                }
            }).queue()
        }
    }

    open fun onAutoComplete(handler: (CommandAutoCompleteInteractionEvent) -> Unit): TypedCommandOption<T> {
        autoComplete = true
        onAutoComplete = handler

        return this
    }

    /**
     * Map value, allows null value
     */
    fun<R> map(map: (T?) -> R): TypedCommandOption<R> {
        val prev = this.mapper
        this.mapper = {
            map(prev(it) as T?)
        }

        return this as TypedCommandOption<R>
    }

    /**
     * Map value, throw error if value is null
     */
    fun<R> mapNonnull(map: (T) -> R): TypedCommandOption<R> {
        val prev = this.mapper
        this.mapper = {
            map((prev(it) as T?)!!)
        }

        return this as TypedCommandOption<R>
    }

    fun localeName(vararg localizations: Pair<DiscordLocale, String>): TypedCommandOption<T> {
        nameLocalizations = mapOf(*localizations)
        return this
    }

    fun localeName(localizations: Map<DiscordLocale, String>): TypedCommandOption<T> {
        nameLocalizations = localizations
        return this
    }

    fun localeDescription(vararg localizations: Pair<DiscordLocale, String>): TypedCommandOption<T> {
        descriptionLocalizations = mapOf(*localizations)
        return this
    }

    fun localeDescription(localizations: Map<DiscordLocale, String>): TypedCommandOption<T> {
        descriptionLocalizations = localizations
        return this
    }
}

open class CommandOption(
    val name: String, val description: String, val type: OptionType
) {
    var required: Boolean = true
    var autoComplete: Boolean = false
    var onAutoComplete: ((event: CommandAutoCompleteInteractionEvent) -> Unit)? = null

    var min: Number? = null
    var max: Number? = null
    var minLength: Int? = null
    var maxLength: Int? = null
    val choices = arrayListOf<Choice>()
    var channelTypes: Array<out ChannelType>? = null

    var mapper: (value: Any?) -> Any? = { it }
    var nameLocalizations: Map<DiscordLocale, String>? = null
    var descriptionLocalizations: Map<DiscordLocale, String>? = null

    open fun parse(event: SlashCommandInteractionEvent): Any? {
        val option = event.getOption(name)
        val parsed: Any? = when (option?.type) {
            OptionType.ATTACHMENT -> option.asAttachment
            OptionType.BOOLEAN -> option.asBoolean
            OptionType.CHANNEL -> option.asChannel
            OptionType.INTEGER -> option.asLong
            OptionType.NUMBER -> option.asDouble
            OptionType.MENTIONABLE -> option.asMentionable
            OptionType.STRING -> option.asString
            OptionType.ROLE -> option.asRole
            OptionType.USER -> option.asUser
            else -> null
        }

        return mapper(parsed)
    }

    open fun build(): OptionData {
        var option = OptionData(type, name, description, required, autoComplete)

        if (type == OptionType.NUMBER || type == OptionType.INTEGER) {
            min?.let {
                option = when (it) {
                    is Double -> option.setMinValue(it)
                    is Float -> option.setMinValue(it.toDouble())
                    else -> option.setMinValue(it.toLong())
                }
            }

            max?.let {
                option = when (it) {
                    is Double -> option.setMaxValue(it)
                    is Float -> option.setMaxValue(it.toDouble())
                    else -> option.setMaxValue(it.toLong())
                }
            }
        }

        if (type == OptionType.STRING) {
            minLength?.let {
                option = option.setMinLength(it)
            }

            maxLength?.let {
                option = option.setMaxLength(it)
            }
        }

        if (type.canSupportChoices()) {
            option = option.addChoices(choices)
        }

        if (type == OptionType.CHANNEL) {
            channelTypes?.let {
                option = option.setChannelTypes(*it)
            }
        }

        nameLocalizations?.let {
            option = option.setNameLocalizations(it)
        }

        descriptionLocalizations?.let {
            option = option.setDescriptionLocalizations(it)
        }

        return option
    }
}