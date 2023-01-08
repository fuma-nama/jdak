package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import net.sonmoosans.jdak.listener.CommandListenerBuilder

sealed class ContextCommand(val name: String, val type: Command.Type): ApplicationCommand {
    var nameLocalizations: Map<DiscordLocale, String>? = null
    override var guildOnly: Boolean = false
    override var permissions: DefaultMemberPermissions? = null

    override fun build(): CommandData {
        val command =  CommandDataImpl(type, name)
            .setGuildOnly(guildOnly)

        permissions?.let {
            command.setDefaultPermissions(it)
        }

        nameLocalizations?.let {
            command.setNameLocalizations(it)
        }

        return command
    }
}

class MessageContextCommand(name: String): ContextCommand(name, Command.Type.MESSAGE) {
    var handler: (MessageContextInteractionEvent) -> Unit = {}

    fun onEvent(handler: (event: MessageContextInteractionEvent) -> Unit) {
        this.handler = handler
    }

    override fun listen(builder: CommandListenerBuilder) {
        builder.messagecommand(this.name, handler)
    }
}

class UserContextCommand(name: String): ContextCommand(name, Command.Type.USER) {
    var handler: (event: UserContextInteractionEvent) -> Unit = {}

    fun onEvent(handler: (event: UserContextInteractionEvent) -> Unit) {
        this.handler = handler
    }

    override fun listen(builder: CommandListenerBuilder) {
        builder.usercommand(this.name, handler)
    }
}