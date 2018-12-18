/*******************************************************************************
 * Copyright (c) 2017,2018 Maschell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/

package de.mas.telegramircbot;

import java.util.List;
import java.util.Map.Entry;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.LongPollingBot;

import de.mas.telegramircbot.common.interfaces.Channel;
import de.mas.telegramircbot.discord.client.DiscordClientInstance;
import de.mas.telegramircbot.irc.IRCChannel;
import de.mas.telegramircbot.irc.IRCServer;
import de.mas.telegramircbot.telegram.bot.TelegramChannelBot;
import de.mas.telegramircbot.utils.Settings;
import de.mas.telegramircbot.utils.config.ConfigReader;
import de.mas.telegramircbot.utils.config.DiscordConfig;
import de.mas.telegramircbot.utils.config.IRCConfig;
import de.mas.telegramircbot.utils.config.TelegramConfig;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class Starter {
    public static void main(String[] args) {
        new Starter();
    }

    @Getter private final TelegramBotsApi botsApi;

    private Starter() {
        ConfigReader.readAPIConfig();
        List<IRCConfig> ircConfigs = ConfigReader.readIRCConfig();
        DiscordConfig discordConfigs = ConfigReader.readDiscordConfig();
        ApiContextInitializer.init();
        this.botsApi = new TelegramBotsApi();

        if (discordConfigs != null) {
            DiscordClientInstance discordInstance = DiscordClientInstance.startInstance(discordConfigs.getToken(), false);

            System.out.println("Available Discord Servers and Channels.");
            for (Server s : discordInstance.getServer()) {
                System.out.println("Server: " + s);
                for (ServerTextChannel  c : s.getTextChannels()) {
                    System.out.println("\t\t Channel: " + c);
                }
            }
            for (Entry<String, TelegramConfig> e : discordConfigs.getChannelList().entrySet()) {
                String channelID = e.getKey();
                TelegramConfig config = e.getValue();
                if (channelID.equals(Settings.PRIVATE_MESSAGES_CHANNEL_NAME)) {
                    log.info("Register Bot for Private Discord Messages");
                    Channel PMChannel = discordInstance.getChannelPM();
                    TelegramChannelBot bot = new TelegramChannelBot(config.getBotToken(), config.getTelegramChatID());
                    registerTelegramBot(bot);
                    ChannelToChannelConnection con = new ChannelToChannelConnection(PMChannel, bot.getChannel());
                    continue;
                }
                if (channelID.equals(Settings.MENTIONS_CHANNEL_NAME)) {
                    log.info("Register Bot for Discord Mentions");
                    Channel MentionsChannel = discordInstance.getChannelMentions();
                    TelegramChannelBot bot = new TelegramChannelBot(config.getBotToken(), config.getTelegramChatID());
                    registerTelegramBot(bot);
                    ChannelToChannelConnection con = new ChannelToChannelConnection(MentionsChannel, bot.getChannel());
                    continue;
                }

                Channel channel = discordInstance.getChannel(channelID);

                if (channel == null) {
                    log.info("ChannelID " + channelID + " was not found on the Discord Server. Skipping it.");
                    continue;
                } else {
                    log.info("Register Bot to Discord ChannelID: " + channelID + "(" + channel.getChannelName() + ")");
                }
                TelegramChannelBot bot = new TelegramChannelBot(config.getBotToken(), config.getTelegramChatID());
                registerTelegramBot(bot);
                ChannelToChannelConnection con = new ChannelToChannelConnection(channel, bot.getChannel());
            }
        }

        for (IRCConfig ircconfig : ircConfigs) {
            IRCServer server = IRCServer.startServer(ircconfig);

            for (Entry<String, TelegramConfig> e : ircconfig.getBotConfigs().entrySet()) {

                String channelName = e.getKey();
                TelegramConfig botConfig = e.getValue();
                IRCChannel c = server.getChannel(channelName);

                TelegramChannelBot bot = new TelegramChannelBot(botConfig.getBotToken(), botConfig.getTelegramChatID());
                registerTelegramBot(bot);
                ChannelToChannelConnection con = new ChannelToChannelConnection(c, bot.getChannel());
            }
        }
    }

    private void registerTelegramBot(LongPollingBot bot) {
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {           
            log.info("Error registrating the bot" + e.getMessage());
        }
    }
}