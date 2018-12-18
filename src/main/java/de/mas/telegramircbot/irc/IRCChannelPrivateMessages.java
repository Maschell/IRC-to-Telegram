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

package de.mas.telegramircbot.irc;

import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.irc.implementations.IRCMessage;
import de.mas.telegramircbot.message.Command;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.MessageStrings;
import de.mas.telegramircbot.utils.Settings;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class IRCChannelPrivateMessages extends IRCChannel {

    protected IRCChannelPrivateMessages(IRCServer server) {
        super(server, Settings.PRIVATE_MESSAGES_CHANNEL_NAME);
    }

    @Getter private String usernameToRespond = "";

    private void setUsernameToRespond(String username) {
        if (!usernameToRespond.equalsIgnoreCase(username)) {
            this.usernameToRespond = username;
            String s = "All messages will now be sent to: " + username;
            log.info(s);
            sendMessageTo(MessageContainer.createSystemMessage("IRC", s));
        }
    }

    @Override
    protected void handleCommand(Command command) {
        switch (command.getType()) {
        case ListUser:
            handleCMDListUser();
            break;
        case SetUser:
            handleCMDSetUser(command.getParam());
            break;
        default:
            break;
        }
    }

    @Override
    public void addMessageFrom(Message message) {
        if (message != null && message instanceof IRCMessage) {
            IRCMessage m = (IRCMessage) message.getInternalMessage();
            if (m == null || m.getContent() == null || m.getAuthor() == null || m.getAuthor().getName() == null) {
                log.info("Couldn't send private message. Value NULL!");
            } else {
                setUsernameToRespond(m.getAuthor().getName());
            }
        }

        super.addMessageFrom(message);
    }

    private void handleCMDSetUser(String user) {
        setUsernameToRespond(user);
    }

    @Override
    public String getTargetForSending() {
        if (getUsernameToRespond() != null && !getUsernameToRespond().isEmpty()) {
            return getUsernameToRespond();
        } else {
            sendMessageTo(MessageContainer.createSystemMessage("IRC", MessageStrings.NO_USERNAME_SET));
            return null;
        }
    }

}
