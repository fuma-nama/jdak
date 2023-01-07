package net.sonmoosans.jdak.listener

interface CommandListenerBuilder {
    fun command(key: CommandKey, handler: CommandHandler)
    fun autocomplete(key: AutoCompleteKey, handler: AutoCompleteHandler)
}