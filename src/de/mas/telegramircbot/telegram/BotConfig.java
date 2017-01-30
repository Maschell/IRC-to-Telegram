package de.mas.telegramircbot.telegram;

import lombok.Data;

@Data
public class BotConfig {
    private final String channelName;
    private final String botUsername;
    private final String botToken;
    private final long telegramChatID;
}
