package de.mas.telegramircbot.utils;

public class TelegramStrings {
    public static final String PEOPLE_IN_CHANNEL = "In %s are the following people: ";  // 1 param  (%s) channel name
    public static final String BOT_ONLINE = "Bot started: %s";                          // 1 param  (%s) channel name
    public static final String TEXT_MESSAGE = "<%s> %s";                                // 2 params (%s,%s) user name,message
    public static final String JOIN_MESSAGE = "%s has joined";                          // 1 param  (%s) user name
    public static final String LEAVE_MESSAGE = "%s has left";                           // 1 param  (%s) user name
    public static final String BOT_PRIVATE = "Sorry, this bot is private =(\n You need to set the chatID to %d (your telegram chat id).";     // 1 param  (%d) chat id
}
