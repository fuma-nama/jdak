package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.OptionType

interface OptionsContainer {
    val options: MutableList<CommandOption>

    fun<T: CommandOption> addOption(command: T): T {
        options += command

        return command
    }
}

fun OptionsContainer.option(type: OptionType, name: String, description: String) = addOption(
    CommandOption(name, description, type)
)

fun OptionsContainer.int(name: String, description: String) = addOption(
    NumberCommandOption<Long>(name, description, OptionType.INTEGER)
)

fun OptionsContainer.int(name: String, description: String, init: NumberCommandOption<Long>.() -> Unit): TypedCommandOption<Long> = addOption(
    NumberCommandOption<Long>(name, description, OptionType.INTEGER).apply(init)
)

fun OptionsContainer.number(name: String, description: String) = addOption(
    NumberCommandOption<Double>(name, description, OptionType.NUMBER)
)

fun OptionsContainer.number(name: String, description: String, init: NumberCommandOption<Double>.() -> Unit): TypedCommandOption<Double> = addOption(
    NumberCommandOption<Double>(name, description, OptionType.NUMBER).apply(init)
)

fun OptionsContainer.user(name: String, description: String) = addOption(
    TypedCommandOption<User>(name, description, OptionType.USER)
)

fun OptionsContainer.string(name: String, description: String) = addOption(
    StringCommandOption<String>(name, description)
)

fun OptionsContainer.string(name: String, description: String, init: StringCommandOption<String>.() -> Unit): TypedCommandOption<String> = addOption(
    StringCommandOption<String>(name, description).apply(init)
)

fun OptionsContainer.role(name: String, description: String) = addOption(
    TypedCommandOption<Role>(name, description, OptionType.ROLE)
)

fun OptionsContainer.attachment(name: String, description: String) = addOption(
    TypedCommandOption<Attachment>(name, description, OptionType.ATTACHMENT)
)

fun OptionsContainer.mentionable(name: String, description: String) = addOption(
    TypedCommandOption<IMentionable>(name, description, OptionType.MENTIONABLE)
)

fun OptionsContainer.channel(name: String, description: String) = addOption(
    ChannelCommandOption<Channel>(name, description)
)

fun OptionsContainer.channel(name: String, description: String, init: ChannelCommandOption<Channel>.() -> Unit): ChannelCommandOption<Channel> = addOption(
    ChannelCommandOption<Channel>(name, description).apply(init)
)

fun OptionsContainer.boolean(name: String, description: String) = addOption(
    TypedCommandOption<Boolean>(name, description, OptionType.BOOLEAN)
)