package net.sonmoosans.jdak.builder

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.sonmoosans.jdak.listener.*

typealias MiddlewareFn = (event: GenericInteractionCreateEvent, next: () -> Unit) -> Unit

class MiddlewareListenerBuilder(val wrapper: MiddlewareFn, val builder: CommandListenerBuilder) : CommandListenerBuilder by builder {

    override fun command(key: CommandKey, handler: CommandHandler) {
        builder.command(key) {
            wrapper(event) {
                handler(this)
            }
        }
    }

    override fun autocomplete(key: AutoCompleteKey, handler: AutoCompleteHandler) {
        builder.autocomplete(key) {
            wrapper(it) {
                handler(it)
            }
        }
    }

    override fun usercommand(name: String, handler: UserCommandHandler) {
        builder.usercommand(name) {
            wrapper(it) {
                handler(it)
            }
        }
    }

    override fun messagecommand(name: String, handler: MessageCommandHandler) {
        builder.messagecommand(name) {
            wrapper(it) {
                handler(it)
            }
        }
    }
}