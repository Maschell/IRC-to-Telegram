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

import java.util.ArrayList;
import java.util.List;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
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
        words.add("sd cafiine");
        words.add("sdcafiine");
        words.add("hid to vpad");
        words.add("hidtovpad");
        words.add("jnustool");
        words.add("jnuslib");
        words.add("saviine");
        words.add("nuspacker");
        words.add("jwudtool");

    }

    public void checkAndAddMessage(Message message) {
        if (message == null) return;
        String content = "";
        if (message.getContent() != null) {
            content = DiscordUtils.getTextWithReplacedMetions(message);
        }
        if ((message.getMentions() != null && checkMentions(message.getMentions())) || checkTextContent(content)) {
            String authorName = "unkwn";
            User a = message.getAuthor();
            if (a != null && a.getName() != null) {
                authorName = a.getName();
            }

            if (message.isPrivateMessage()) {
                String msg = "Private message from " + authorName + ":" + message.getContent();
                sendMessageTo(MessageContainer.createTextMessage("", msg));
            } else {

                Channel c = message.getChannelReceiver();
                String channelName = "unkwn";
                String serverName = "unkwn";
                if (c != null && c.getName() != null) {
                    channelName = c.getName();
                    Server s = c.getServer();
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
        for (String s : words) {
            if (content.toLowerCase().contains(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkMentions(List<User> mentions) {
        for (User u : mentions) {
            if (u.isYourself()) {
                return true;
            }
        }
        return false;
    }

}
