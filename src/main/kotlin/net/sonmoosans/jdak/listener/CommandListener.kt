package net.sonmoosans.jdak.listener

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.jdak.event.SlashCommandContext

typealias CommandHandler = SlashCommandContext.() -> Unit
typealias CommandHandlerChunk = HashMap<CommandKey, CommandHandler>
data class CommandKey(
    val name: String, val group: String? = null, val subcommand: String? = null
)

class CommandListener: ListenerAdapter() {
    val slash: CommandHandlerChunk = hashMapOf()
    val users = hashMapOf<String, (UserContextInteractionEvent) -> Unit>()
    val messages = hashMapOf<String, (MessageContextInteractionEvent) -> Unit>()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val key = CommandKey(event.name, event.subcommandGroup, event.subcommandName)
        val context = SlashCommandContext(event)

        slash[key]?.invoke(context)
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        users[event.name]?.invoke(event)
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        messages[event.name]?.invoke(event)
    }
}