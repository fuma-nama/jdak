package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface ApplicationCommand {
    var guildOnly: Boolean
    var permissions: DefaultMemberPermissions?

    fun build(): CommandData
}