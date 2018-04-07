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

package de.mas.telegramircbot.discord.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.javacord.api.AccountType;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.MessageEditEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.message.MessageEditListener;

import de.mas.telegramircbot.common.interfaces.Channel;
import de.mas.telegramircbot.discord.client.implementations.DiscordMessage;
import de.mas.telegramircbot.utils.Utils;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class DiscordClientInstance {
    @Getter private final User yourself;
    private final DiscordApi api;
    private final Map<Long, DiscordChannel> channelList = new HashMap<>();
    @Getter private final DiscordChannel channelPM = new DiscordChannelPrivateMessages(this);
    @Getter private final DiscordChannelMentions channelMentions = new DiscordChannelMentions();

    public static DiscordClientInstance startInstance(String token, boolean bot) {
        AccountType type = AccountType.BOT;
        if (!bot) {
            type = AccountType.CLIENT;
        }
        try {
            return connect(new DiscordApiBuilder(), type, token);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static DiscordClientInstance connect(DiscordApiBuilder api, AccountType type, String token) throws InterruptedException, ExecutionException {
        return new DiscordClientInstance(api.setAccountType(type).setToken(token).login().get());
    }

    private DiscordClientInstance(DiscordApi api) {
        this.api = api;
        this.yourself = api.getYourself();

        addMessageListener();
    }

    public Channel getChannel(String channelID) {
        Optional<ServerTextChannel> discordChannel = api.getServerTextChannelById(channelID);
        if (!discordChannel.isPresent()) {
            log.info("Couldn't find channel with the ID: " + channelID);
            return null;
        }

        ServerTextChannel dChannel = discordChannel.get();

        DiscordChannel c = new DiscordChannel(dChannel);
        channelList.put(dChannel.getId(), c);
        return c;
    }

    private void addMessageListener() {
        api.addMessageEditListener(new MessageEditListener() {
            @Override
            public void onMessageEdit(MessageEditEvent event) {
                if (!event.getMessage().isPresent()) {
                    return;
                }

                Message message = event.getMessage().get();

                String oldContent = "<null>";
                if (event.getOldContent().isPresent()) {
                    oldContent = event.getOldContent().get();
                }

                if (message.getPrivateChannel().isPresent()) {
                    channelPM.addEditedMessageFrom(new DiscordMessage(message), oldContent);
                    return;
                }

                DiscordChannel c = channelList.get(message.getChannel().getId());
                if (c != null) {
                    c.addEditedMessageFrom(new DiscordMessage(message), oldContent);
                }

            }
        });
        api.addMessageCreateListener(new MessageCreateListener() {

            @Override
            public void onMessageCreate(MessageCreateEvent event) {
                Message message = event.getMessage();

                channelMentions.checkAndAddMessage(message);

                if (message.getPrivateChannel().isPresent()) {
                    if (!shouldSendMessageToTelegram(message, channelPM)) return;
                    channelPM.addMessageFrom(new DiscordMessage(message));
                    return;
                }

                DiscordChannel c = channelList.get(message.getChannel().getId());
                if (c != null) {
                    if (!shouldSendMessageToTelegram(message, c)) return;
                    c.addMessageFrom(new DiscordMessage(message));
                }

            }
        });

    }

    protected boolean isMyMessage(Message msg) {
        if (msg.getUserAuthor().isPresent()) {
            return msg.getUserAuthor().get().equals(getYourself());
        }
        return false;
    }

    private boolean shouldSendMessageToTelegram(Message msg, DiscordChannel channel) {
        if (isMyMessage(msg)) {
            Utils.sleep(500); // We wait before we check if it was sent by this bot.
            if (channel.isMessageSentByTheBot(new DiscordMessage(msg))) {
                log.info("The message was sent by this bot. We're ignoring it!");
                return false;
            }
        }
        return true;
    }

    public String getUserID() {
        return Long.toString(api.getYourself().getId());
    }

    public Collection<Server> getServer() {
        return api.getServers();
    }

    public Collection<TextChannel> getChannels() {
        return api.getTextChannels();
    }

    public Collection<User> getUsers() {
        return api.getCachedUsers();
    }

    public User getUserByID(String userID) {
        User user;
        try {
            user = api.getUserById(userID).get();
            return user;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
