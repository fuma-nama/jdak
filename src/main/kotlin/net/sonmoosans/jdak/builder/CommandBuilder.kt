package net.sonmoosans.jdak.builder

import net.sonmoosans.jdak.command.CommandGroup
import net.sonmoosans.jdak.command.SlashCommand
import net.sonmoosans.jdak.command.SubCommand
import net.sonmoosans.jdak.command.SubCommandGroup

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@DslMarker
annotation class CommandDsl

@CommandDsl
class CommandBuilder {
    val slashcommands = arrayListOf<SlashCommand>()

    fun slashcommand(name: String, description: String, @CommandDsl init: SlashCommand.() -> Unit): SlashCommand {
        val command = SlashCommand(name, description).apply(init)
        slashcommands += command

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
