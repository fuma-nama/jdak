import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.sonmoosans.jdak.JDAK
import net.sonmoosans.jdak.builder.CommandBuilder
import net.sonmoosans.jdak.builder.group
import net.sonmoosans.jdak.builder.subcommand
import net.sonmoosans.jdak.command.int
import net.sonmoosans.jdak.command.string
import net.sonmoosans.jdak.command.user

fun main() {
    val jda = JDABuilder.createDefault(System.getenv("TOKEN"))
        .build()
        .awaitReady()

    JDAK.commands(jda) {
        usercontext("Test", mapOf(
            DiscordLocale.CHINESE_TAIWAN to "測試"
        )) {
            it.reply("Hello World").queue()
        }

        slashcommand("test", "debug commands") {
            group("beta", "Beta features") {
                subcommand("kill", "Kill someone") {
                    val target = user("target", "The target to kill")

                    onEvent {
                        event.reply("Killed ${target.value.asMention}")
                            .queue()
                    }
                }
            }
        }

        slashcommand("hello", "Say hello") {
            val text = string("text", "text to send")
            val size = int("size", "Size of text") {
                range(min = 0, max = 3)

                choices(
                    "xl" to 2,
                    "lg" to 1,
                )

                mapNullable {
                    it?.toInt()
                }
            }.optional()

            onEvent {
                event.reply("${text.value} in size of ${size.value}")
                    .queue()
            }
        }

        gameCommands()
    }
}

//you can split the builder into multi functions
fun CommandBuilder.gameCommands() = slashcommand("game", "Games commands") {
    val something = int("something", "No description")

    //the event won't be replied if 'something' is 0
    filterEvent {
        something.value != 0L
    }

    onEvent {
        event.reply("result: ${something.value}").queue()
    }
}