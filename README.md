# IRC-to-Telegram 
IRC-to-Telegram is small tool to interact with an IRC channel via Telegram.
This is still an early WIP and very experimental. You'll achieve the best result in combination with an IRC Bouncer (for example ZNC)
# How does it work  
When using IRC-to-Telegram you'll end up in Bot in Telegram for every channel. If the channels are spread on multiple servers, you (currently) need to run multiple instances of IRC-to-Telegram. All messages in each channel will be send directly to you via the Bot. When you respond to the Bot, the message will be send as an IRC Message to the IRC channel.  
The Bot will will only interact to Telegram Account, that is set in the configuration file.  

# How to setup  
This tool has only been tested in combination with an IRC Bouncer and through an HTTP Connection. HTTPS connections are currently not supported.

## Configuration  
The IRC Server and Telegram bots can be set via config files.
Example configs can be found in the "exampleconfigs" folder.  

### IRC  
Create a file called "irc_config.yml" in the path from where the .jar is excuted.  
```
server: "localhost"  
login: "login"  
pass: "password"  
nick: "nickname"  
port: 123456  
```
If you don't need a password, left the value empty, but don't remove the line.

### Telegram

In order to use it, you need to create Telegram Bot for every channel you want to join.
Bots easily can be created directly in Telegram. Contact the @BotFather for more information.

Once you've create the bot(s), you're ready to setup the config file.  
Create a file called "bot_config.yml" in the path from where the .jar is excuted.

The file should look like this

```
channelName: "#channel1"
botUsername: Bot123
botToken: 123412345:AABBCCDDEEFFGGHHIIJJKKLLMMNNOOPPQQR
telegramChatID: 12341234
---
channelName: "#channel2"
botUsername: Bot456
botToken: 123412345:AABBCCDDEEFFGGHHIIJJKKLLMMNNOOPPQQR
telegramChatID: 12341234
---
channelName: "PRIVATE_MESSAGES"
botUsername: Bot789
botToken: 123412345:AABBCCDDEEFFGGHHIIJJKKLLMMNNOOPPQQR
telegramChatID: 12341234
```
Each channel is seperated by an "---".  
 
**channelName**: is the channel this bot will observe.  
**botUsername**: and **botToken** are the bot data you'll get while creating a bot.  
**telegramChatID**: is the chatID the bot will interact with. All other Telegram account will be ignored from the bot. If you don't know your ChatID, just enter any ChatID and try to contact the bot. It should throw you an error message including your chatID.

# Private Messages
For private messages, an own Telegram bot is needed. This bot has to be set to the channel "PRIVATE_MESSAGES" (can be changed in the Settings.java).  
To write a private message to a specific user, you need to set the bot to this user first. This can be done via sending a message containing "/setUser <username>" to the Bot (without the <> obviously). If you get a private message, the user will be set automatically to this sender.

# Running it  
Once you have set the config files, you can simply execute the .jar.  
More Settings are avaible in the */utils/Settings.java*. The message layout can be changed in */utils/TelegramStrings.java*

# Sending images via Telegram
When you send an image via Telegram, this images will be automatically uploaded the imgur and convert to a link. This link will be posted in IRC channel.  
Before you can do this, you need to set your imgur client id. This can be done in the Settings.java, or in a config file called "api_config.yml". Example:  

```
imgurClientID: "abcdefg12345678"
```

### Commands  
Currently this tool is very basic. It just redirecting the messages from A to B and from B to A.  
Only simple text can be exchanged, this means:
- Private Messages are not possible
- Kicking, Banning, "/me" actions are not possible
- any other fancy things

But you can use other commands:
-"/listuser" - Sends you a list of all people in the channel


### Aren't the Telegram bots public? Can everbody see my messages?  
Telegram Bots are in fact public, but this tool only intacts with one Telegram Account which can be set for each channel.  
All other people who try to contact the Bot will only get an error message and will never receive any other message.

### Credits 
Coding - Maschell  
Libraries used (Huge thanks!)  
Java Telegram API - https://github.com/rubenlagus/TelegramBotsExample  
Lombok - https://projectlombok.org/index.html  
YAML Parser - https://github.com/EsotericSoftware/yamlbeans  