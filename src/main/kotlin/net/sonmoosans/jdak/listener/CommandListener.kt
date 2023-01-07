package net.sonmoosans.jdak.listener

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.sonmoosans.jdak.event.SlashCommandContext

typealias CommandHandler = SlashCommandContext.() -> Unit
typealias AutoCompleteHandler = (CommandAutoCompleteInteractionEvent) -> Unit
data class CommandKey(
    val name: String, val group: String? = null, val subcommand: String? = null
)

data class AutoCompleteKey(
    val command: CommandKey, val option: String
)

class CommandListener: ListenerAdapter(), CommandListenerBuilder {
    val slash: HashMap<CommandKey, CommandHandler> = hashMapOf()
    val users = hashMapOf<String, (UserContextInteractionEvent) -> Unit>()
    val messages = hashMapOf<String, (MessageContextInteractionEvent) -> Unit>()
    val autoCompletes = hashMapOf<AutoCompleteKey, (CommandAutoCompleteInteractionEvent) -> Unit>()

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

    override fun command(key: CommandKey, handler: CommandHandler) {
        slash[key] = handler
    }

    override fun autocomplete(key: AutoCompleteKey, handler: AutoCompleteHandler) {
        autoCompletes[key] = handler
    }
}