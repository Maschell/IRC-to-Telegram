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

package de.mas.telegramircbot.irc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.mas.telegramircbot.common.AbstractChannel;
import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.irc.implementations.DefaultIRCMessageHandler;
import de.mas.telegramircbot.irc.implementations.IRCMessageSender;
import de.mas.telegramircbot.message.Command;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.MessageStrings;
import de.mas.telegramircbot.message.User;
import lombok.Getter;

public class IRCChannel extends AbstractChannel{
    @Getter private final IRCServer server;
    @Getter private final String channelName;

    protected IRCChannel(IRCServer server, String channelName) {
        this(server, channelName, DefaultIRCMessageHandler.getInstance());
    }

    protected IRCChannel(IRCServer server, String channelName, MessageHandler messageHandler) {
        super(messageHandler);
        this.server = server;
        this.messageHandler = messageHandler;
        this.channelName = channelName;
    }
    
    @Getter private Set<String> userList = new TreeSet<>();
    @Getter private List<String> tempUserList = new ArrayList<>();

    public void join(String username) {
        if (!getUserList().contains(username)) {
            sendMessageTo(MessageContainer.createJoinMessage("IRC", new User(username)));
            getUserList().add(username);
        }
    }

    public void leave(String username) {
        if (getUserList().contains(username)) {
            sendMessageTo(MessageContainer.createLeaveMessage("IRC", new User(username)));
            getUserList().remove(username);
        }
    }

    public void addFullUserListInParts(List<String> user) {
        getTempUserList().addAll(user);
    }

    public void addFullUserListInPartsEnd() {
        getUserList().clear();
        getUserList().addAll(getTempUserList());
        getTempUserList().clear();
    }

    public String getUserListFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(MessageStrings.PEOPLE_IN_CHANNEL, getChannelName()));
        for (String s : getUserList()) {
            sb.append(s + " ");
        }
        return sb.toString();
    }

    public void handleCMDListUser() {
        sendMessageTo(MessageContainer.createTextMessage("IRC", getUserListFormatted()));
    }

    protected void handleCommand(Command command) {
        switch (command.getType()) {
        case ListUser:
            handleCMDListUser();
            break;
        default:
            break;
        }
    }

    /**
     * 
     * @return name of the target. Returns null if the target is not set.
     */
    public String getTargetForSending() {
        return getChannelName();
    }

    @Override
    protected MessageSender getMessageReceiverForSending() {
        return new IRCMessageSender(getServer(), getTargetForSending());
    }

}
