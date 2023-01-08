package net.sonmoosans.jdak.builder

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.sonmoosans.jdak.command.*
import net.sonmoosans.jdak.listener.CommandListenerBuilder

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@DslMarker
annotation class CommandDsl

@CommandDsl
class CommandBuilder(val middleware: MiddlewareFn? = null) {
    val commands = arrayListOf<ApplicationCommand>()
    val children = arrayListOf<CommandBuilder>()

    fun middleware(fn: MiddlewareFn, routes: CommandBuilder.() -> Unit): CommandBuilder {
        val builder = CommandBuilder(fn).apply(routes)
        children += builder

        return builder
    }

    fun slashcommand(name: String, description: String, @CommandDsl init: SlashCommand.() -> Unit): SlashCommand {
        val command = SlashCommand(name, description).apply(init)
        commands += command

        return command
    }

    fun messagecommand(
        name: String,
        nameLocalizations: Map<DiscordLocale, String>? = null,
        guildOnly: Boolean = false,
        permissions: DefaultMemberPermissions? = null,
        handler: (MessageContextInteractionEvent) -> Unit
    ): MessageContextCommand {
        val command = MessageContextCommand(name)
        command.nameLocalizations = nameLocalizations
        command.guildOnly = guildOnly
        command.permissions = permissions
        command.onEvent(handler)

        commands += command
        return command
    }

    fun messagecontext(name: String, init: MessageContextCommand.() -> Unit): MessageContextCommand {
        val command = MessageContextCommand(name).apply(init)

        commands += command
        return command
    }

    fun usercontext(name: String, init: UserContextCommand.() -> Unit): UserContextCommand {
        val command = UserContextCommand(name).apply(init)

        commands += command
        return command
    }

    fun usercommand(
        name: String,
        nameLocalizations: Map<DiscordLocale, String>? = null,
        guildOnly: Boolean = false,
        permissions: DefaultMemberPermissions? = null,
        handler: (UserContextInteractionEvent) -> Unit): UserContextCommand {
        val command = UserContextCommand(name)
        command.nameLocalizations = nameLocalizations
        command.guildOnly = guildOnly
        command.permissions = permissions
        command.onEvent(handler)

        commands += command
        return command
    }

    fun buildTo(commands: MutableList<CommandData>, listener: CommandListenerBuilder) {
        val listenerWithMiddleware = middleware?.let {
            MiddlewareListenerBuilder(it, listener)
        }

        for (command in this.commands) {
            command.listen(listenerWithMiddleware?: listener)
            commands += command.build()
        }

        for (child in children) {
            child.buildTo(commands, listenerWithMiddleware?: listener)
        }
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
