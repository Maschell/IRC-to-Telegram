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
package de.mas.telegramircbot.telegram.bot.implementations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.telegram.telegrambots.api.objects.Message;

import de.mas.telegramircbot.common.interfaces.Attachment;
import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.message.CommandType;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.TextMessage;
import de.mas.telegramircbot.message.User;
import de.mas.telegramircbot.telegram.common.CommandsDefs;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class DefaultTelegramMessageHandler implements MessageHandler {
    static @Getter private final DefaultTelegramMessageHandler instance = new DefaultTelegramMessageHandler();

    @Override
    public MessageContainer handleTextMessageFrom(de.mas.telegramircbot.common.interfaces.Message message) {
        Message m = (Message) message.getInternalMessage();
        String fromID = m.getMessageId().toString();
        String text = m.getText();
        String[] lines = text.split(" ");

        switch (lines[0].toLowerCase()) {
        case CommandsDefs.LIST_USER:
            return MessageContainer.createCommandMessage(fromID, CommandType.ListUser);
        case CommandsDefs.SET_USER:
            return MessageContainer.createCommandMessage(fromID, CommandType.SetUser, text.substring(CommandsDefs.SET_USER.length() + 1));
        default:
            return MessageContainer.createTextMessage(m.getMessageId().toString(), text);
        }
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
    public MessageContainer handleEditMessageFrom(de.mas.telegramircbot.common.interfaces.Message message, String oldContent) {
        Message msg = (Message) message.getInternalMessage();
        return MessageContainer.createEditMessage(msg.getMessageId().toString(), new User("", ""), msg.getText(), "");
    }

    @Override
    public MessageReturned sendDocumentTo(MessageSender sender, de.mas.telegramircbot.message.Document document) throws Exception {
        if (sender == null) {
            return null;
        }
        return sender.sendFile(document);
    }

    @Override
    public MessageReturned sendTextMessageTo(MessageSender sender, TextMessage textMessage) throws Exception {
        if (sender == null) {
            return null;
        }

        return sender.sendTextMessage(textMessage.getFormattedString());
    }

    @Override
    public MessageReturned applyEditMessageTo(MessageSender sender, de.mas.telegramircbot.common.interfaces.Message msg, String newContent) throws Exception {
        if (msg == null) {
            return null;
        }

        return sender.editMessage(msg, newContent);

    }

}
