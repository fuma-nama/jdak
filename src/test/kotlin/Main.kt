import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.sonmoosans.jdak.JDAK
import net.sonmoosans.jdak.builder.CommandBuilder
import net.sonmoosans.jdak.builder.group
import net.sonmoosans.jdak.builder.subcommand
import net.sonmoosans.jdak.command.long
import net.sonmoosans.jdak.command.string
import net.sonmoosans.jdak.command.user

fun main() {
    val jda = JDABuilder.createDefault(System.getenv("TOKEN"))
        .build()
        .awaitReady()
    val guilds = arrayOf("684766026776576052")

    JDAK.guilds(jda, guilds = guilds.map { jda.getGuildById(it)!! }) {
        //inline
        messagecommand("Test",
            nameLocalizations = mapOf(DiscordLocale.CHINESE_TAIWAN to "測試"),
            guildOnly = true,
            permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
        ) {
            it.reply("Hello World").queue()
        }

        //full
        messagecontext("Test 2") {
            nameLocalizations = mapOf(DiscordLocale.CHINESE_TAIWAN to "測試 2")
            guildOnly = true
            permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)

            onEvent {
                it.reply("Hello World").queue()
            }
        }

        //Define a middleware
        middleware({ event, next ->
            val member = event.member

            if (member == null) {
                println("Outside of guild")
            } else if (member.permissions.contains(Permission.ADMINISTRATOR)) {
                println("Are you admin!")
            } else {
                println("Ignored event")

                return@middleware
            }

            next()
        }) {
            protectedCommands()
        }

        slashcommand("hello", "Say hello") {
            val text = string("text", "text to send")
                .autoComplete {
                    mapOf(
                        "Hello" to "World",
                        "Money" to "Shark"
                    )
                }
            val size = long("size", "Size of text")
                .range(min = 0, max = 3)
                .choices(
                    "xl" to 2,
                    "lg" to 1,
                )
                .optional()

            onEvent {
                event.reply("${text.value} in size of ${size.value}")
                    .queue()
            }
        }
    }
}

//you can split the builder into multi functions
fun CommandBuilder.protectedCommands() = slashcommand("protected", "protected commands") {
    nameLocale = mapOf(
        DiscordLocale.CHINESE_TAIWAN to "測試"
    )
    guildOnly = true

    group("beta", "Beta features") {
        subcommand("kill", "Kill someone") {
            val target = user("target", "The target to kill")

            onEvent {
                event.reply("Killed ${target.value.asMention}").queue()
            }
        }

        subcommand("hello", "Say hello") {

            onEvent {
                event.reply("hello").queue()
            }
        }
    }
}