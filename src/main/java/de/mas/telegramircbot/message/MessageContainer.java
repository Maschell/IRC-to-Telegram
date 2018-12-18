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
package de.mas.telegramircbot.message;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import de.mas.telegramircbot.message.TextMessage.MessageTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class MessageContainer implements Comparable<MessageContainer> {
    private final Date timestamp = new Date();
    private final String fromID;

    private User author;
    @Setter(AccessLevel.PRIVATE) private TextMessage textMessage;
    @Setter(AccessLevel.PRIVATE) private EditMessage editMessage;
    @Setter(AccessLevel.PRIVATE) private Command command;
    private Document document;

    private MessageContainer(String fromID) {
        this.fromID = fromID;
    }

    public static MessageContainer createEditMessage(String fromID, User author, String message, String oldMessage) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(author);
        result.setEditMessage(new EditMessage(message, oldMessage, author.getName()));
        return result;
    }

    public static MessageContainer createSystemMessage(String fromID, String message) {
        MessageContainer result = new MessageContainer(fromID);
        result.setTextMessage(new TextMessage(message, MessageTypes.SystemMessage));
        return result;
    }

    public static MessageContainer createTextMessageFromUser(String fromID, User author, String message) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(author);
        result.setTextMessage(new TextMessage(message, MessageTypes.TextFromUserMessage, author.getName()));
        return result;
    }

    public static MessageContainer createTextMessage(String fromID, String message) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(new User());
        result.setTextMessage(new TextMessage(message, MessageTypes.TextMessage));
        return result;
    }

    public static MessageContainer createJoinMessage(String fromID, User user) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(user);
        result.setTextMessage(new TextMessage(MessageTypes.JoinMessage, user.getName()));
        return result;
    }

    public static MessageContainer createLeaveMessage(String fromID, User user) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(user);
        result.setTextMessage(new TextMessage(MessageTypes.LeaveMessage, user.getName()));
        return result;
    }

    public static MessageContainer createDocumentMessage(String fromID, String filename, URL url, User user) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla/4.76");
        InputStream ins = con.getInputStream();
        return createDocumentMessage(fromID, filename, ins, user);
    }

    public static MessageContainer createDocumentMessage(String fromID, String filename, InputStream in, User user) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(user);
        result.setDocument(new Document(in, filename,user));
        return result;
    }

    public static MessageContainer createCommandMessage(String fromID, CommandType cmd) {
        return createCommandMessage(fromID, cmd, null);
    }

    public static MessageContainer createCommandMessage(String fromID, CommandType cmd, String param) {
        MessageContainer result = new MessageContainer(fromID);
        result.setAuthor(new User());
        result.setCommand(new Command(cmd, param));
        return result;
    }

    public boolean hasTextMessage() {
        return getTextMessage() != null;
    }

    public boolean hasDocument() {
        return getDocument() != null;
    }

    public boolean hasEditMessage() {
        return getEditMessage() != null;
    }

    public boolean hasCommand() {
        return getCommand() != null;
    }

    @Override
    public int compareTo(MessageContainer o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}