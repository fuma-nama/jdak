package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface ApplicationCommand {
    fun build(): CommandData
}