package net.sonmoosans.jdak.event

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.sonmoosans.jdak.command.CommandOption
import net.sonmoosans.jdak.command.OptionsContainer
import net.sonmoosans.jdak.command.TypedCommandOption

class SlashCommandContext(
    val event: SlashCommandInteractionEvent
) {
    val options = hashMapOf<String, Any?>()

    fun parseOptions(command: OptionsContainer) {
        for (option in command.options) {
            this.options[option.name] = option.parse(event)
        }
    }

    val<T : Any?> TypedCommandOption<T>.value: T get() {
        return options[name] as T
    }

    inline fun<reified T> CommandOption.value(): T {
        return options[name] as T
    }
}