package net.sonmoosans.jdak.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.jdak.event.SlashCommandContext

typealias CommandHandler = SlashCommandContext.() -> Unit
typealias CommandHandlerChunk = HashMap<CommandKey, CommandHandler>
data class CommandKey(
    val name: String, val group: String? = null, val subcommand: String? = null
)

class CommandListener: ListenerAdapter() {
    val handlers: CommandHandlerChunk = hashMapOf()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val key = CommandKey(event.name, event.subcommandGroup, event.subcommandName)
        val context = SlashCommandContext(event)

        handlers[key]?.invoke(context)
    }
}