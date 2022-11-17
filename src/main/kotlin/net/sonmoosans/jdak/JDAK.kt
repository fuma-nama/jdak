package net.sonmoosans.jdak

import net.dv8tion.jda.api.JDA
import net.sonmoosans.jdak.builder.CommandBuilder
import net.sonmoosans.jdak.listener.CommandListener

object JDAK {
    fun commands(jda: JDA, init: CommandBuilder.() -> Unit) {
        val builder = CommandBuilder().apply(init)
        val listener = CommandListener()

        for (slashcommand in builder.slashcommands) {
            slashcommand.buildHandler(listener.handlers)
        }

        jda.updateCommands()
            .addCommands(builder.slashcommands.map { it.build() })
            .queue()
        jda.addEventListener(listener)
    }
}