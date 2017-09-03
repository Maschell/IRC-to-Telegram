/*******************************************************************************
 * Copyright (c) 2017 Maschell
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

package de.mas.telegramircbot.discord.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.message.MessageEditListener;
import de.mas.telegramircbot.common.interfaces.Channel;
import de.mas.telegramircbot.discord.client.implementations.DiscordMessage;
import de.mas.telegramircbot.utils.Utils;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class DiscordClientInstance {
    @Getter private final User yourself;
    private final DiscordAPI api;
    private final Map<String, DiscordChannel> channelList = new HashMap<>();
    @Getter private final DiscordChannel channelPM = new DiscordChannelPrivateMessages(this);
    @Getter private final DiscordChannelMentions channelMentions = new DiscordChannelMentions();

    public static DiscordClientInstance startInstance(String username, String password) {
        return connect(Javacord.getApi(username, password));
    }

    public static DiscordClientInstance startInstance(String token, boolean bot) {
        return connect(Javacord.getApi(token, bot));
    }

    private static DiscordClientInstance connect(DiscordAPI api) {
        api.connectBlocking();
        api.setAutoReconnect(true);
        return new DiscordClientInstance(api);
    }

    private DiscordClientInstance(DiscordAPI api) {
        this.api = api;
        this.yourself = api.getYourself();

        addMessageListener();
    }

    public Channel getChannel(String channelID) {
        de.btobastian.javacord.entities.Channel discordChannel = api.getChannelById(channelID);
        if (discordChannel == null) {
            log.info("Couldn't find channel with the ID: " + channelID);
            return null;
        }

        DiscordChannel c = new DiscordChannel(discordChannel);
        channelList.put(discordChannel.getId(), c);
        return c;
    }

    private void addMessageListener() {
        api.registerListener(new MessageEditListener() {
            @Override
            public void onMessageEdit(DiscordAPI api, Message message, String oldContent) {
                if (message.isPrivateMessage()) {
                    channelPM.addEditedMessageFrom(new DiscordMessage(message), oldContent);
                    return;
                }

                DiscordChannel c = channelList.get(message.getChannelReceiver().getId());
                if (c != null) {
                    c.addEditedMessageFrom(new DiscordMessage(message), oldContent);
                }

            }
        });
        api.registerListener(new MessageCreateListener() {
            @Override
            public void onMessageCreate(DiscordAPI api, Message message) {
                channelMentions.checkAndAddMessage(message);
                if (message.isPrivateMessage()) {
                    if (!shouldSendMessageToTelegram(message, channelPM)) return;
                    channelPM.addMessageFrom(new DiscordMessage(message));
                    return;
                }

                DiscordChannel c = channelList.get(message.getChannelReceiver().getId());
                if (c != null) {
                    if (!shouldSendMessageToTelegram(message, c)) return;
                    c.addMessageFrom(new DiscordMessage(message));
                }
            }
        });
    }

    protected boolean isMyMessage(Message msg) {
        return msg.getAuthor().equals(getYourself());
    }

    private boolean shouldSendMessageToTelegram(Message msg, DiscordChannel channel) {
        if (isMyMessage(msg)) {
            Utils.sleep(500); // We wait before we check if it was sent by this bot.
            if (channel.isMessageSentByTheBot(msg)) {
                log.info("The message was sent by this bot. We're ignoring it!");
                return false;
            }
        }
        return true;
    }

    public String getUserID() {
        return api.getYourself().getId();
    }

    public Collection<de.btobastian.javacord.entities.Server> getServer() {
        return api.getServers();
    }

    public Collection<de.btobastian.javacord.entities.Channel> getChannels() {
        return api.getChannels();
    }

    public Collection<User> getUsers() {
        return api.getUsers();
    }

    public User getUserByID(String userID) {
        Future<User> future = api.getUserById(userID);
        try {
            return future.get();
        } catch (Exception e) {
            log.warning("Error while getting a User by ID." + e.getMessage());
            return null;
        }
    }
}
