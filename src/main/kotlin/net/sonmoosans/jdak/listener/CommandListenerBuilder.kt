package net.sonmoosans.jdak.listener

interface CommandListenerBuilder {
    fun command(key: CommandKey, handler: CommandHandler)
    fun autocomplete(key: AutoCompleteKey, handler: AutoCompleteHandler)

    fun usercommand(name: String, handler: UserCommandHandler)
    fun messagecommand(name: String, handler: MessageCommandHandler)

    fun build(): CommandListener
}

open class CommandListenerBuilderImpl: CommandListenerBuilder {
    val slash: HashMap<CommandKey, CommandHandler> = hashMapOf()
    val users = hashMapOf<String, UserCommandHandler>()
    val messages = hashMapOf<String, MessageCommandHandler>()
    val autoCompletes = hashMapOf<AutoCompleteKey, AutoCompleteHandler>()

    override fun command(key: CommandKey, handler: CommandHandler) {
        slash[key] = handler
    }

    override fun autocomplete(key: AutoCompleteKey, handler: AutoCompleteHandler) {
        autoCompletes[key] = handler
    }

    override fun usercommand(name: String, handler: UserCommandHandler) {
        users[name] = handler
    }

    override fun messagecommand(name: String, handler: MessageCommandHandler) {
        messages[name] = handler
    }

    override fun build(): CommandListener {
        return CommandListener(
            slash, users, messages, autoCompletes
        )
    }
}