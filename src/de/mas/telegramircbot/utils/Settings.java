package de.mas.telegramircbot.utils;

public class Settings {
    public static final String BOT_CONFIG_FILE = "bot_config.yml";
    public static final String IRC_CONFIG_FILE = "irc_config.yml";
    
    public static final boolean SIMPLE_LOGGING_ENABLED = true;
        
    //Will be used if no config is provided.
    public static String IRC_SERVER = "localhost";
    public static String IRC_LOGIN = "<login>";
    public static String IRC_PASS = "<password>";
    public static String IRC_NICK = "<nick>";
    public static int IRC_PORT = 12345;
}
