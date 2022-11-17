package net.sonmoosans.jdak.builder

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.sonmoosans.jdak.command.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@DslMarker
annotation class CommandDsl

@CommandDsl
class CommandBuilder {
    val slashcommands = arrayListOf<SlashCommand>()
    val messageCommands = arrayListOf<MessageContextCommand>()
    val userCommands = arrayListOf<UserContextCommand>()

    fun slashcommand(name: String, description: String, @CommandDsl init: SlashCommand.() -> Unit): SlashCommand {
        val command = SlashCommand(name, description).apply(init)
        slashcommands += command

        return command
    }

    fun messagecontext(name: String, nameLocalizations: Map<DiscordLocale, String>? = null, handler: (MessageContextInteractionEvent) -> Unit): MessageContextCommand {
        val command = MessageContextCommand(name)
        command.nameLocalizations = nameLocalizations
        command.onEvent(handler)

        messageCommands += command
        return command
    }

    fun usercontext(name: String, nameLocalizations: Map<DiscordLocale, String>? = null, handler: (UserContextInteractionEvent) -> Unit): UserContextCommand {
        val command = UserContextCommand(name)
        command.nameLocalizations = nameLocalizations
        command.onEvent(handler)

        userCommands += command
        return command
    }
}

fun CommandGroup.subcommand(name: String, description: String, @CommandDsl init: SubCommand.() -> Unit): SubCommand {
    val command = SubCommand(name, description).apply(init)

    subcommands += command
    return command
}

fun SlashCommand.group(name: String, description: String, @CommandDsl init: SubCommandGroup.() -> Unit): SubCommandGroup {
    val group = SubCommandGroup(name, description).apply(init)

    subcommandGroups += group
    return group
}
