import net.dv8tion.jda.api.JDABuilder
import net.sonmoosans.jdak.JDAK
import net.sonmoosans.jdak.builder.group
import net.sonmoosans.jdak.builder.subcommand
import net.sonmoosans.jdak.command.int
import net.sonmoosans.jdak.command.string
import net.sonmoosans.jdak.command.user

fun main() {
    val jda = JDABuilder.createDefault("OTA3OTU1NzgxOTcyOTE4Mjgz.Gp9K9s.aKl7cTbMEyQnM3vWoqfA31rCXPj432gNwsNc1w")
        .build()
        .awaitReady()

    JDAK.commands(jda) {
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
                .required()
                .map {
                    println(it)
                    it
                }
            val size = int("size", "Size of text")
                .optional()
                .map {
                    println(it)
                    it
                }

            onEvent {
                event.reply("${text.value} in size of ${size.value}")
                    .queue()
            }
        }
    }
}