package de.mas.telegramircbot.irc;

import de.mas.telegramircbot.utils.Settings;
import lombok.Data;

@Data
public class IRCConfig {
    private final String server;
    private final String login;
    private final String pass ;
    private final String nick;   
    private final int port;    
    
    public static IRCConfig getDefaultConfig() {
        return new IRCConfig(Settings.IRC_SERVER, Settings.IRC_LOGIN, Settings.IRC_PASS, Settings.IRC_NICK, Settings.IRC_PORT);
    }
}
