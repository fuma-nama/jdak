package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

interface ApplicationCommand {
    fun build(): CommandData
}

data class SlashCommand(
    val name: String,
    val description: String,
): ApplicationCommand, OptionsContainer {
    override val options = arrayListOf<CommandOption>()
    val subcommands = arrayListOf<SubCommand>()

    override fun build(): CommandData {
        return CommandDataImpl(name, description)
            .addOptions(options.map { it.build() })
            .addSubcommands(subcommands.map { it.build() })
    }
}

data class SubCommand(
    val name: String,
    val description: String,
): OptionsContainer {
    override val options = arrayListOf<CommandOption>()

    fun build(): SubcommandData {
        return SubcommandData(name, description)
            .addOptions(options.map { it.build() })
    }
}