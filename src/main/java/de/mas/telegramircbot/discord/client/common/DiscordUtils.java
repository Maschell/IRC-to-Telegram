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

package de.mas.telegramircbot.discord.client.common;

import java.util.Optional;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import de.mas.telegramircbot.utils.Utils;

public class DiscordUtils {
    /*public static String getNameForMessageAuthor(MessageAuthor u) {
        return getNameForMessageAuthor(u, null);
    }*/

    /*
    public static String getNameForMessageAuthor(MessageAuthor u, Server s) {
        String authorName = "unknown";
        if (u != null) {
            if (u.isUser()) {
                return u.getDisplayName();
            }
            authorName = u.getName();
        }
        return authorName;
    }

    public static String getNameForUser(User u, Server s) {
        String authorName = "unknown";
        if (u != null) {
            authorName = u.getName();
            if (s != null) {
                Optional<String> newName = u.getNickname(s);
                if (newName.isPresent()) {
                    return newName.get() + "*";
                }
            }
        }
        return authorName;
    }*/

    public static String getTextWithReplacedMetions(Message message) {
        Optional<ServerTextChannel> channel = message.getServerTextChannel();
        Server server = null;
        if (channel.isPresent()) {
            server = channel.get().getServer();
        }

        String text = message.getContent();
        for (User u : message.getMentionedUsers()) {
            text = Utils.replaceStringInStringEscaped(text, "<@" + u.getId() + ">", "@" + u.getDisplayName(server));
        }
        for (Role u : message.getMentionedRoles()) {
            text = Utils.replaceStringInStringEscaped(text, "<@&" + u.getId() + ">", "@" + u.getName());
        }
        return text;
    }
}
