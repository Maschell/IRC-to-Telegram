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

package de.mas.telegramircbot.discord.client.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAttachment;

import de.mas.telegramircbot.common.interfaces.Attachment;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.message.User;

public class DiscordMessage implements de.mas.telegramircbot.common.interfaces.Message, MessageReturned {
    private final Message message;

    public DiscordMessage(Message m) {
        this.message = m;
    }

    @Override
    public Collection<Attachment> getAttachments() {
        List<Attachment> res = new ArrayList<>();
        for (MessageAttachment a : message.getAttachments()) {
            res.add(new DiscordAttachment(a));
        }
        return res;
    }

    @Override
    public String getID() {
        return Long.toString(message.getId());
    }

    @Override
    public boolean hasTextContent() {
        return (message.getContent() != null && !message.getContent().isEmpty());
    }

    @Override
    public Object getInternalMessage() {
        return message;
    }

    @Override
    public de.mas.telegramircbot.common.interfaces.Message getMessage() {
        return this;
    }

    @Override
    public User getAuthor() {
        return new User(message.getAuthor().getName());
    }

}
