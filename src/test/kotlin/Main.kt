import net.dv8tion.jda.api.JDABuilder
import net.sonmoosans.jdak.JDAK

fun main() {
    val jda = JDABuilder.createDefault("OTA3OTU1NzgxOTcyOTE4Mjgz.Gp9K9s.aKl7cTbMEyQnM3vWoqfA31rCXPj432gNwsNc1w")
        .build()
        .awaitReady()

    JDAK.commands(jda)
}