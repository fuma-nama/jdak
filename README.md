# JDAK - JDA Command Framework in Kotlin
A Light-Weight, Fast, Flexible, Functional Programming Command framework for [JDA](https://github.com/DV8FromTheWorld/JDA) written in **Kotlin**

- High performance
- Support all Application Commands
- Auto complete, Middlewares and more!
- Functional Programming style

## Installation
Maven
```xml
<dependency>
    <groupId>io.github.sonmoosans</groupId>
    <artifactId>jdak</artifactId>
    <version>1.2.0</version>
</dependency>
```
Gradle
```
implementation 'io.github.sonmoosans:jdak:1.2.0'
```
## Getting Started
```kotlin
val jda = JDABuilder.createDefault(System.getenv("TOKEN"))
    .build()
    .awaitReady()

JDAK.global(jda) {
    //put your global commands here
}

JDAK.guilds(jda, guilds) { 
    //put your guild-only commands here
}
```

### Creating a Slash command
```kotlin
slashcommand("hello", "Say hello") {
    //options
    val text = string("text", "text to send")
    val size = int("size", "Size of text").optional()
    
    //handle interaction events
    onEvent {
        event.reply("${text.value} in size of ${size.value}").queue()
    }
}
```
### Creating a Context command
```kotlin
usercommand("Test") {
    it.reply("Hello World").queue()
}

messagecommand("Test") {
    it.reply("Hello World").queue()
}
```
### Nested Slash commands
For more information, read Discord's [documentation](https://discord.com/developers/docs/interactions/application-commands)
```kotlin
//root command
slashcommand("test", "debug commands") {
    //subcommand group
    group("beta", "Beta features") {
        //subcommand
        subcommand("kill", "Kill someone") {
            val user = user("user", "The selected user")
            
            onEvent {
                event.reply("Killed ${user.value.asMention}").queue()
            }
        }
    }
}
```

## Auto Complete
We have built-in support for auto-complete which allows you to setup auto-complete in few lines of code

```kotlin
slashcommand("hello", "Say hello") {
    val text = string("text", "text to send")
        .autoComplete {
            mapOf(
                "Hello" to "World",
                "Money" to "Shark"
            )
        }

    onEvent {
        event.reply(text.value).queue()
    }
}
```

## Localizations
We support localizations for all Application commands
```kotlin
messagecommand("Test", mapOf(DiscordLocale.CHINESE_TAIWAN to "測試")) {
    it.reply("Hello World").queue()
}

slashcommand("test", "debug commands") {
    nameLocale = mapOf(
        DiscordLocale.CHINESE_TAIWAN to "測試"
    )

    onEvent {
        event.reply("Killed ${target.value.asMention}").queue()
    }
}
```

## Middleware
Middleware can be used to check for permissions when the user uses specified commands <br>
It allows you to control the event handler for each command

```kotlin
//Check if the bot has admin permissions
middleware({ event, next ->
    val member = event.member

    if (member == null) {
        println("Outside of guild")
    } else if (member.permissions.contains(Permission.ADMINISTRATOR)) {
        println("Are you admin!")
    } else {
        println("Ignore event: Missing permissions")

        return@middleware
    }

    next() //call the next handler
}) {
    protectedCommands()
}
```

## Permissions
JDAK also supports to set `guildOnly` and `permissions` options
```kotlin
messagecommand("Test",
    guildOnly = true,
    permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
) {
    it.reply("Hello World").queue()
}

slashcommand("test", "debug commands") {
    guildOnly = true
    permissions = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
    
    val target = user("target", "The target to kill")
    
    onEvent {
        event.reply("Killed ${target.value.asMention}")
            .queue()
    }
}
```

### Limit
We only support Application commands, text commands are not supported