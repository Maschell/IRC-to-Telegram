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

package de.mas.telegramircbot.discord.client;

import org.javacord.api.entity.user.User;

import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.discord.client.implementations.DiscordMessageSender;
import de.mas.telegramircbot.message.Command;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.utils.Settings;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class DiscordChannelPrivateMessages extends DiscordChannel {
    private final DiscordClientInstance discordInstance;

    protected DiscordChannelPrivateMessages(DiscordClientInstance instance) {
        super(null);
        this.discordInstance = instance;
    }

    @Getter private User sendToUser = null;

    @Override
    public String getChannelName() {
        return Settings.PRIVATE_MESSAGES_CHANNEL_NAME;
    }

    private void setSendToUser(String userID) {
        User u = discordInstance.getUserByID(userID);
        if (u == null) {
            String message = "User is null. We can't sent the private messages to him.";
            log.info(message);
            sendMessageTo(MessageContainer.createSystemMessage("", message));
            return;
        }

        setSendToUser(u);
    }

    @Override
    public void addMessageFrom(Message message) {
        if (message != null) {
            org.javacord.api.entity.message.Message m = (org.javacord.api.entity.message.Message) message.getInternalMessage();
            if (m == null || m.getContent() == null || m.getAuthor() == null || m.getAuthor().getName() == null) {
                log.info("Couldn't send private discord message to Telegram. Value NULL!");
            } else {
                if (!discordInstance.isMyMessage(m)) {
                    setSendToUser(Long.toString(m.getAuthor().getId()));
                }
            }
        }

        super.addMessageFrom(message);
    }

    private void setSendToUser(User user) {
        if (sendToUser != null && this.sendToUser.equals(user)) return;
        this.sendToUser = user;
        String message = "Set userID for private discord messages to " + sendToUser.getName() + "(" + sendToUser.getId() + ")";
        log.info(message);
        sendMessageTo(MessageContainer.createSystemMessage("", message));
    }

    public void handleCMDSetUser(String userID) {
        setSendToUser(userID);
    }

    public void handleCMDListUser() {
        StringBuilder sb = new StringBuilder();
        sb.append("Last chats:" + System.lineSeparator());
        for (User u : discordInstance.getUsers()) {
            sb.append(u.getName() + " " + u.getId() + System.lineSeparator());
        }
        sendMessageTo(MessageContainer.createTextMessage("", sb.toString()));
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
    protected MessageSender getMessageReceiverForSending() {
        return DiscordMessageSender.getInstance(getSendToUser());
    }
}
