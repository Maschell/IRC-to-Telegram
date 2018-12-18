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

import org.javacord.api.entity.channel.ServerTextChannel;

import de.mas.telegramircbot.common.AbstractChannel;
import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.discord.client.implementations.DefaultDiscordMessageHandler;
import de.mas.telegramircbot.discord.client.implementations.DiscordMessageSender;
import de.mas.telegramircbot.message.Command;

public class DiscordChannel extends AbstractChannel {
    private final ServerTextChannel discordChannel;

    protected DiscordChannel(ServerTextChannel discordChannel) {
        this(discordChannel, DefaultDiscordMessageHandler.getInstance());
    }

    protected DiscordChannel(ServerTextChannel discordChannel, MessageHandler messageHandler) {
        super(messageHandler);
        this.discordChannel = discordChannel;
    }

    protected void handleCommand(Command command) {
        switch (command.getType()) {
        case ListUser:
            // handleCMDListUser();
            break;
        default:
            break;
        }
    }

    @Override
    public String getChannelName() {
        return discordChannel.getName();
    }

    @Override
    protected MessageSender getMessageReceiverForSending() {
        return DiscordMessageSender.getInstance(discordChannel);
    }
}
