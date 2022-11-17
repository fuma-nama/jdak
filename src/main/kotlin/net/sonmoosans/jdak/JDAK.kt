package net.sonmoosans.jdak

import net.dv8tion.jda.api.JDA
import net.sonmoosans.jdak.builder.CommandBuilder
import net.sonmoosans.jdak.listener.CommandListener

object JDAK {
    fun commands(jda: JDA, init: CommandBuilder.() -> Unit) {
        val builder = CommandBuilder().apply(init)
        val listener = CommandListener()

        for (command in builder.slashcommands) {
            command.buildHandler(listener.slash)
        }
        for (command in builder.userCommands) {
            listener.users[command.name] = command.handler
        }
        for (command in builder.messageCommands) {
            listener.messages[command.name] = command.handler
        }

        jda.updateCommands()
            .addCommands(builder.slashcommands.map { it.build() })
            .addCommands(builder.userCommands.map { it.build() })
            .addCommands(builder.messageCommands.map { it.build() })
            .queue()
        jda.addEventListener(listener)
    }
}