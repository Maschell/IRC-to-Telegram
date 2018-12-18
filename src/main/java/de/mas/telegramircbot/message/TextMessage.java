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

import lombok.Data;

@Data
public class TextMessage {
    private final String authorName;
    private final String content;
    private final MessageTypes messageType;
    
    public TextMessage(String message, MessageTypes type, String authorName) {
        this.authorName = authorName;
        this.content = message;
        this.messageType = type;
    }

    public TextMessage(String message, MessageTypes type) {
        this(message,type,"UnknownAuthor");
    }

    public TextMessage(MessageTypes type,String authorName) {
        this("emptyMessage",type,authorName);
    }

    public String getFormattedString() {
        switch (getMessageType()) {
        case TextFromUserMessage:
            return String.format(MessageStrings.TEXT_MESSAGE_FROM_USER, authorName, content);
        case TextMessage:
            return String.format(MessageStrings.TEXT_MESSAGE, content);
        case JoinMessage:
            return String.format(MessageStrings.JOIN_MESSAGE, authorName);
        case LeaveMessage:
            return String.format(MessageStrings.LEAVE_MESSAGE, authorName);
        case SystemMessage:
            return String.format(MessageStrings.SYSTEM_MESSAGE, content);
        default:
            return String.format(MessageStrings.TEXT_MESSAGE_FROM_USER, authorName, content);
        }
    }

    public enum MessageTypes {
        TextMessage, TextFromUserMessage, JoinMessage, LeaveMessage, SystemMessage
    }
}
