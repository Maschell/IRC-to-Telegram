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

package de.mas.telegramircbot.discord.client.common;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.mas.telegramircbot.utils.Utils;

public class DiscordUtils {
    public static String getNameForUser(User u) {
        return getNameForUser(u, null);
    }

    public static String getNameForUser(User u, Server s) {
        String authorName = "unknown";
        if (u != null) {
            authorName = u.getName();
            if (s != null) {
                String newName = u.getNickname(s);
                if (newName != null) {
                    return newName + "*";
                }
            }
        }
        return authorName;
    }
    
    public static String getTextWithReplacedMetions(Message message){
        Channel channel = message.getChannelReceiver();
        Server server = null;
        if (channel != null) {
            server = channel.getServer();
        }

        String text = message.getContent();
        for (de.btobastian.javacord.entities.User u : message.getMentions()) {
            text = Utils.replaceStringInStringEscaped(text, "<@" + u.getId() + ">", "@" + DiscordUtils.getNameForUser(u, server));
        }
        for (Role u : message.getMentionedRoles()) {
            text = Utils.replaceStringInStringEscaped(text, "<@&" + u.getId() + ">", "@" + u.getName());
        }
        return text;
    }
}
