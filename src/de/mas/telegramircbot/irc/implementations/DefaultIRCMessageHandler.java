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

package de.mas.telegramircbot.irc.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import de.mas.telegramircbot.common.interfaces.Attachment;
import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.message.Document;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.TextMessage;
import de.mas.telegramircbot.message.TextMessage.MessageTypes;
import de.mas.telegramircbot.message.User;
import de.mas.telegramircbot.utils.Utils;
import de.mas.telegramircbot.utils.images.ImgurUploader;
import lombok.Getter;

public class DefaultIRCMessageHandler implements MessageHandler {
    @Getter private final static MessageHandler instance = new DefaultIRCMessageHandler();

    @Override
    public MessageContainer handleTextMessageFrom(Message message) {
        if (message instanceof IRCMessage) {
            IRCMessage ircmsg = (IRCMessage) message;
            return MessageContainer.createTextMessageFromUser(ircmsg.getID(), ircmsg.getAuthor(), ircmsg.getContent());
        }
        return null;
    }

    @Override
    public Collection<MessageContainer> handleAttachmentFrom(String fromID, Collection<Attachment> attachments, User author) {
        return new ArrayList<>();
    }

    @Override
    public MessageContainer handleEditMessageFrom(Message message, String oldContent) {
        return null;
    }

    @Override
    public MessageReturned sendDocumentTo(MessageSender sender, Document document) throws Exception {
        try {
            String link = ImgurUploader.uploadImageAndGetLink(document.getDocumentStream());
            return sendTextMessageTo(sender, new TextMessage(link, MessageTypes.TextMessage, document.getAuthor().getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MessageReturned sendTextMessageTo(MessageSender sender, TextMessage textMessage) throws Exception {
        return sender.sendTextMessage(Utils.replacesSmileys(textMessage.getFormattedString()));
    }

    @Override
    public MessageReturned applyEditMessageTo(MessageSender sender, Message m, String newContent) throws Exception {
        return null;
    }
}
