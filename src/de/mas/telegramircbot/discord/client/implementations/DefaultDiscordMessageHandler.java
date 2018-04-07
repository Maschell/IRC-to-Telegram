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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mas.telegramircbot.common.interfaces.Attachment;
import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.discord.client.common.DiscordUtils;
import de.mas.telegramircbot.message.Document;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.TextMessage;
import de.mas.telegramircbot.message.User;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class DefaultDiscordMessageHandler implements MessageHandler {
    static @Getter private final DefaultDiscordMessageHandler instance = new DefaultDiscordMessageHandler();

    private DefaultDiscordMessageHandler() {

    }

    @Override
    public MessageContainer handleEditMessageFrom(Message msg, String oldContent) {
        org.javacord.api.entity.message.Message message = (org.javacord.api.entity.message.Message) msg.getInternalMessage();
        String text = DiscordUtils.getTextWithReplacedMetions(message);
        return MessageContainer.createEditMessage(Long.toString(message.getId()),
                new User(message.getAuthor().getName(), Long.toString(message.getAuthor().getId())), text, oldContent);
    }

    @Override
    public Collection<MessageContainer> handleAttachmentFrom(String fromID, Collection<Attachment> attachments, User author) {
        List<MessageContainer> result = new ArrayList<>();
        for (Attachment attachment : attachments) {
            try {
                InputStream is = attachment.getAsInputStream();
                result.add(MessageContainer.createDocumentMessage(fromID, attachment.getFileName(), is, author));
            } catch (IOException e) {
                log.info("failed to create input stream for sending a attachement." + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public MessageContainer handleTextMessageFrom(Message msg) {
        org.javacord.api.entity.message.Message message = (org.javacord.api.entity.message.Message) msg.getInternalMessage();

        String text = DiscordUtils.getTextWithReplacedMetions(message);

        String authorName = message.getAuthor().getDisplayName();
        String authorID = "unknown";

        return MessageContainer.createTextMessageFromUser(Long.toString(message.getId()), new User(authorName, authorID), text);
    }

    @Override
    public MessageReturned sendDocumentTo(MessageSender messageReceiver, Document document) throws Exception {
        if (messageReceiver == null) {
            return null;
        }
        return messageReceiver.sendFile(document);
        // addOwnMessage(result);
    }

    @Override
    public MessageReturned sendTextMessageTo(MessageSender messageReceiver, TextMessage textMessage) throws Exception {
        if (messageReceiver == null) {
            return null;
        }

        return messageReceiver.sendTextMessage(textMessage.getFormattedString());
    }

    @Override
    public MessageReturned applyEditMessageTo(MessageSender messageReceiver, Message m, String newContent) throws Exception {
        if (m == null) {
            return null;
        }
        return messageReceiver.editMessage(m, newContent);
    }

}
