package net.sonmoosans.jdak

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.sonmoosans.jdak.builder.CommandBuilder
import net.sonmoosans.jdak.listener.CommandListener

data class CommandsBuild(
    val slashCommands: List<CommandData>,
    val userCommands: List<CommandData>,
    val messageCommands: List<CommandData>,
    val listener: CommandListener
)

object JDAK {
    fun build(init: CommandBuilder.() -> Unit): CommandsBuild {
        val builder = CommandBuilder().apply(init)
        val listener = CommandListener()

        for (command in builder.slashcommands) {
            command.buildHandler(listener)
        }
        for (command in builder.userCommands) {
            listener.users[command.name] = command.handler
        }
        for (command in builder.messageCommands) {
            listener.messages[command.name] = command.handler
        }

        val slashCommands = builder.slashcommands.map { it.build() }
        val userCommands = builder.userCommands.map { it.build() }
        val messageCommands = builder.messageCommands.map { it.build() }

        return CommandsBuild(slashCommands, userCommands, messageCommands, listener)
    }

    fun guilds(jda: JDA, guilds: List<Guild>, init: CommandBuilder.() -> Unit) {
        val data = build(init)

        for (guild in guilds) {
            guild.updateCommands()
                .addCommands(data.slashCommands)
                .addCommands(data.userCommands)
                .addCommands(data.messageCommands)
                .queue()
        }

        jda.addEventListener(data.listener)
    }

    fun global(jda: JDA, init: CommandBuilder.() -> Unit) {
        val data = build(init)

        jda.updateCommands()
            .addCommands(data.slashCommands)
            .addCommands(data.userCommands)
            .addCommands(data.messageCommands)
            .queue()

        jda.addEventListener(data.listener)
    }
}