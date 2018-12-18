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

import de.mas.telegramircbot.common.DefaultMessageReturned;
import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.irc.IRCServer;
import de.mas.telegramircbot.message.Document;
import de.mas.telegramircbot.message.User;
import lombok.Data;

@Data
public class IRCMessageSender implements MessageSender {
    private final IRCServer server;
    private final String target;

    @Override
    public MessageReturned sendFile(Document d) throws Exception {        
        return null;
    }

    @Override
    public MessageReturned sendTextMessage(String formattedString) throws Exception {        
        server.sendMessage("PRIVMSG " + getTarget() + " :" + formattedString);
        return new DefaultMessageReturned(new IRCMessage(new User(""), ""));
    }

    @Override
    public MessageReturned editMessage(Message toID, String newContent) throws Exception {        
        return null;
    }

}
