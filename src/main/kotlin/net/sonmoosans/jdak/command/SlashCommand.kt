package net.sonmoosans.jdak.command

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import net.sonmoosans.jdak.builder.CommandDsl
import net.sonmoosans.jdak.event.SlashCommandContext
import net.sonmoosans.jdak.listener.*

@CommandDsl
interface CommandGroup {
    val subcommands: MutableList<SubCommand>
}

@CommandDsl
abstract class CommandNode: OptionsContainer {
    var nameLocale: Map<DiscordLocale, String>? = null
    var descriptionLocale: Map<DiscordLocale, String>? = null
    override val options = arrayListOf<CommandOption>()

    var handler: CommandHandler? = null
        private set

    fun listen(key: CommandKey, builder: CommandListenerBuilder) {
        handler?.let { handler ->
            builder.command(key) {
                parseOptions(this@CommandNode)

                if (filter(this)) {
                    handler.invoke(this)
                }
            }
        }

        for (option in options) {
            val handler = option.onAutoComplete?: continue

            builder.autocomplete(AutoCompleteKey(key, option.name), handler)
        }
    }

    /**
     * filter events
     * If false, skip the event
     */
    var filter: SlashCommandContext.() -> Boolean = { true }

    fun filterEvent(filter: (@CommandDsl SlashCommandContext).() -> Boolean) {
        this.filter = filter
    }

    fun onEvent(handler: (@CommandDsl SlashCommandContext).() -> Unit) {
        this.handler = handler
    }
}

data class SlashCommand(
    val name: String,
    val description: String,
): CommandNode(), ApplicationCommand, CommandGroup {
    override val subcommands = arrayListOf<SubCommand>()
    val subcommandGroups = arrayListOf<SubCommandGroup>()
    override var guildOnly: Boolean = false
    override var permissions: DefaultMemberPermissions? = null

    override fun build(): CommandData {
        return CommandDataImpl(name, description)
            .setGuildOnly(guildOnly)
            .setNameLocalizations(nameLocale?: mapOf())
            .setDescriptionLocalizations(descriptionLocale?: mapOf())
            .addOptions(options.map { it.build() })
            .addSubcommands(subcommands.map { it.build() })
            .addSubcommandGroups(subcommandGroups.map { it.build() })
            .also { command ->
                permissions?.let {
                    command.defaultPermissions = it
                }
            }
    }

    fun buildHandler(builder: CommandListenerBuilder) {
        listen(CommandKey(name), builder)

        for (subcommandGroup in subcommandGroups) {
            subcommandGroup.buildHandler(this.name, builder)
        }

        for (subcommand in subcommands) {

            subcommand.listen(
                key = CommandKey(this.name, subcommand = subcommand.name),
                builder = builder
            )
        }
    }
}

data class SubCommandGroup(
    val name: String,
    val description: String
): CommandGroup {
    override val subcommands = arrayListOf<SubCommand>()
    var nameLocale: Map<DiscordLocale, String>? = null
    var descriptionLocale: Map<DiscordLocale, String>? = null

    fun build(): SubcommandGroupData {
        return SubcommandGroupData(name, description)
            .addSubcommands(subcommands.map { it.build() })
            .setNameLocalizations(nameLocale?: mapOf())
            .setDescriptionLocalizations(descriptionLocale?: mapOf())
    }

    fun buildHandler(root: String, builder: CommandListenerBuilder) {
        for (subcommand in subcommands) {
            val key = CommandKey(root, group = this.name, subcommand = subcommand.name)

            subcommand.listen(key, builder)
        }
    }
}

data class SubCommand(
    val name: String,
    val description: String,
): CommandNode() {

    fun build(): SubcommandData {
        return SubcommandData(name, description)
            .setNameLocalizations(nameLocale?: mapOf())
            .setDescriptionLocalizations(descriptionLocale?: mapOf())
            .addOptions(options.map { it.build() })
    }
}