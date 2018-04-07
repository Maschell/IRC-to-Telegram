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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Messageable;
import de.mas.telegramircbot.common.DefaultMessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.message.Document;

public class DiscordMessageSender implements MessageSender {
    private final static Map<Messageable, DiscordMessageSender> instances = new HashMap<>();

    public static DiscordMessageSender getInstance(Messageable c) {
        DiscordMessageSender instance = instances.get(c);
        if (instance == null) {
            instance = new DiscordMessageSender(c);
            instances.put(c, instance);
        }

        return instance;
    }

    private final Messageable receiver;

    public DiscordMessageSender(Messageable receiver) {
        this.receiver = receiver;
    }

    @Override
    public MessageReturned sendFile(Document d) throws Exception {
        Future<Message> m = receiver.sendMessage(d.getDocumentStream(), d.getFilename());
        return new DefaultMessageReturned(new DiscordMessage(m.get()));

    }

    @Override
    public MessageReturned sendTextMessage(String formattedString) throws Exception {
        Future<Message> m = receiver.sendMessage(formattedString);
        return new DefaultMessageReturned(new DiscordMessage(m.get()));
    }

    @Override
    public MessageReturned editMessage(de.mas.telegramircbot.common.interfaces.Message to, String newContent) throws Exception {
        if (to != null && to.getInternalMessage() != null && to.getInternalMessage() instanceof Message) {
            Message message = (Message) to.getInternalMessage();
            message.edit(newContent).get();
        }
        return null;
    }
}
