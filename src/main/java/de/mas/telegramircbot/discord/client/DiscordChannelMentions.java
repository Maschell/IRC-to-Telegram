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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import de.mas.telegramircbot.discord.client.common.DiscordUtils;
import de.mas.telegramircbot.message.MessageContainer;

public class DiscordChannelMentions extends DiscordChannel {

    private final List<String> words = new ArrayList<>();

    protected DiscordChannelMentions() {
        super(null);
        initWordList();
    }

    private void initWordList() {
        words.add("maschell");
        words.add("maschelldev");
        // words.add("sd cafiine");
        // words.add("sdcafiine");
        words.add("hid to vpad");
        words.add("hidtovpad");
        words.add("jnustool");
        words.add("jnuslib");
        words.add("saviine");
        words.add("nuspacker");
        words.add("jwudtool");
        words.add("wups");
        words.add("wii u plugin system");
        words.add("wiiu plugin system");

    }

    public void checkAndAddMessage(Message message) {
        if (message == null) return;
        String content = "";
        if (message.getContent() != null) {
            content = DiscordUtils.getTextWithReplacedMetions(message);
        }
        if ((message.getMentionedRoles() != null && checkMentionsUser(message.getMentionedUsers()))
                || (message.getMentionedRoles() != null && checkMentionsRoles(message.getMentionedRoles())) || checkTextContent(content)) {
            String authorName = "unkwn";
            MessageAuthor a = message.getAuthor();
            if (a != null && a.getName() != null) {
                authorName = a.getName();
            }

            if (message.getPrivateChannel().isPresent()) {
                String msg = "Private message from " + authorName + ":" + message.getContent();
                sendMessageTo(MessageContainer.createTextMessage("", msg));
            } else {

                Optional<ServerTextChannel> c = message.getServerTextChannel();
                String channelName = "unkwn";
                String serverName = "unkwn";
                if (c.isPresent() && c.get().getName() != null) {

                    channelName = c.get().getName();
                    Server s = c.get().getServer();
                    if (s != null && s.getName() != null) {
                        serverName = s.getName();
                    }
                }
                String msg = "[" + channelName + "@" + serverName + "] " + authorName + ": " + content;
                sendMessageTo(MessageContainer.createTextMessage("", msg));
            }
        }
    }

    private boolean checkTextContent(String content) {
        return words.stream().anyMatch(w -> content.toLowerCase().contains(w));
    }

    private boolean checkMentionsUser(Collection<User> mentions) {
        return mentions.stream().anyMatch(u -> u.isYourself());
    }

    private boolean checkMentionsRoles(List<Role> mentionedRoles) {
        return mentionedRoles.stream().anyMatch(r -> checkMentionsUser(r.getUsers()));
    }

}
