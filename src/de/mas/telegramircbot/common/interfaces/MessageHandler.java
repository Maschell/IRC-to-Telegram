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

package de.mas.telegramircbot.common.interfaces;

import java.util.Collection;

import de.mas.telegramircbot.message.Document;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.TextMessage;
import de.mas.telegramircbot.message.User;

public interface MessageHandler {
    public MessageContainer handleTextMessageFrom(Message message);

    public Collection<MessageContainer> handleAttachmentFrom(String fromID, Collection<Attachment> attachments, User Author);

    public MessageContainer handleEditMessageFrom(Message message, String oldContent);

    public MessageReturned sendDocumentTo(MessageSender sender, Document document) throws Exception;

    public MessageReturned sendTextMessageTo(MessageSender sender, TextMessage textMessage) throws Exception;

    public MessageReturned applyEditMessageTo(MessageSender sender, Message m, String newContent) throws Exception;

}
