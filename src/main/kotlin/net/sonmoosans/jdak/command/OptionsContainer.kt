package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.OptionType

interface OptionsContainer {
    val options: MutableList<CommandOption>

    fun<T: CommandOption> addOption(command: T): T {
        options += command

        return command
    }
}

fun OptionsContainer.option(type: OptionType, name: String, description: String = "") = addOption(
    CommandOption(name, description, type)
)

fun OptionsContainer.int(name: String, description: String = "") = addOption(
    TypedCommandOption<Long>(name, description, OptionType.INTEGER)
)

fun OptionsContainer.number(name: String, description: String = "") = addOption(
    TypedCommandOption<Double>(name, description, OptionType.NUMBER)
)

fun OptionsContainer.user(name: String, description: String = "") = addOption(
    TypedCommandOption<User>(name, description, OptionType.USER)
)