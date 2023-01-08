package net.sonmoosans.jdak.listener

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.jdak.event.SlashCommandContext

typealias CommandHandler = SlashCommandContext.() -> Unit
typealias AutoCompleteHandler = (CommandAutoCompleteInteractionEvent) -> Unit
typealias MessageCommandHandler = (MessageContextInteractionEvent) -> Unit
typealias UserCommandHandler = (UserContextInteractionEvent) -> Unit

data class CommandKey(
    val name: String, val group: String? = null, val subcommand: String? = null
)

data class AutoCompleteKey(
    val command: CommandKey, val option: String
)

class CommandListener(
    val slash: Map<CommandKey, CommandHandler>,
    val users: Map<String, UserCommandHandler>,
    val messages: Map<String, MessageCommandHandler>,
    val autoCompletes: Map<AutoCompleteKey, AutoCompleteHandler>
): ListenerAdapter() {

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

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        val key = AutoCompleteKey(
            CommandKey(event.name, event.subcommandGroup, event.subcommandName),
            event.focusedOption.name
        )

        autoCompletes[key]?.invoke(event)
    }
}