package de.mas.telegramircbot;
import java.util.List;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.LongPollingBot;

import de.mas.telegramircbot.irc.IRCConfig;
import de.mas.telegramircbot.irc.IRCServer;
import de.mas.telegramircbot.telegram.BotConfig;
import de.mas.telegramircbot.telegram.IRCChannelBot;
import de.mas.telegramircbot.utils.ConfigReader;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class TeleIRCGram {
    public static void main(String[] args) {
        new TeleIRCGram();
    }
    
    @Getter private final IRCServer server;
    @Getter private final TelegramBotsApi botsApi;
    
    private TeleIRCGram(){
        IRCConfig ircConfig = ConfigReader.readIRCConfig();
        this.server = IRCServer.startServer(ircConfig);

        ApiContextInitializer.init();

        this.botsApi = new TelegramBotsApi();
        
        addIRCBotFromConfig();
    }
    
    private void addIRCBotFromConfig() {
        List<BotConfig> configs = ConfigReader.readBotConfigs();
        
        for(BotConfig config : configs){
            registerBot(IRCChannelBot.createBot(getServer(),config));
        }
    }

    private void registerBot(LongPollingBot bot){
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            if(bot instanceof IRCChannelBot){
                ((IRCChannelBot)bot).stopBot();
            }
            log.info("Error registrating the bot" + e.getMessage());
        }
    }
}