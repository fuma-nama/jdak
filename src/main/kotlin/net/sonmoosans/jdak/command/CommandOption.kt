package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class TypedCommandOption<T : Any?>(
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

    fun autoComplete(): TypedCommandOption<T> {
        autoComplete
        return this
    }

    fun<R> map(map: (T) -> R): TypedCommandOption<R> {
        val prev = this.mapper
        this.mapper = {
            map(prev(it) as T)
        }

        return this as TypedCommandOption<R>
    }
}

open class CommandOption(
    val name: String, val description: String, val type: OptionType
) {
    var required: Boolean = true
    var autoComplete: Boolean = false
    var mapper: (value: Any?) -> Any? = { it }

    fun parse(event: SlashCommandInteractionEvent): Any? {
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

    fun build(): OptionData {
        return OptionData(type, name, description, required, autoComplete)
    }
}