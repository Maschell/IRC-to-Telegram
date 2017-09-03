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

package de.mas.telegramircbot.telegram.bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.message.MessageStrings;
import de.mas.telegramircbot.telegram.bot.implementations.DefaultTelegramMessageHandler;
import de.mas.telegramircbot.telegram.bot.implementations.TelegramMessage;
import de.mas.telegramircbot.telegram.bot.implementations.TelegramMessageSender;
import lombok.Getter;
import lombok.extern.java.Log;

@Log
public class TelegramChannelBot extends TelegramLongPollingBot {
    public static final String FILE_API = "https://api.telegram.org/file/bot";

    @Getter private final String botToken;
    @Getter private final long telegramChatID;

    @Getter private final TelegramChannel channel;

    public TelegramChannelBot(String botToken, long telegramChatID) {
        this(botToken, telegramChatID, DefaultTelegramMessageHandler.getInstance());
    }

    public TelegramChannelBot(String botToken, long telegramChatID, MessageHandler messageHandler) {
        super();
        this.botToken = botToken;
        this.telegramChatID = telegramChatID;
        this.channel = new TelegramChannel(messageHandler, new TelegramMessageSender(this));

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() || update.hasEditedMessage()) {
            Long senderChatID;

            if (update.getMessage() != null) {
                senderChatID = update.getMessage().getChatId();
            } else {
                senderChatID = update.getEditedMessage().getChatId();
            }

            if (!isFromValidUser(senderChatID)) {
                log.info("Oh. Someone tried to use the bot! ChatID: " + senderChatID);
                try {
                    sendMessage(String.format(MessageStrings.BOT_PRIVATE, senderChatID),senderChatID);
                } catch (TelegramApiException e) {
                }
                return;
            }

            org.telegram.telegrambots.api.objects.Message message = update.getMessage();

            if (update.hasEditedMessage()) {
                channel.addEditedMessageFrom(new TelegramMessage(update.getEditedMessage(), this), "");
            }
            if (message == null) return;

            channel.addMessageFrom(new TelegramMessage(update.getMessage(), this));
        }
    }

    private boolean isFromValidUser(Long chatId) {
        return (chatId.equals(getTelegramChatID()));
    }

    private Message sendMessage(String string, Long chatID) throws TelegramApiException {
        return execute(new SendMessage().setChatId(getTelegramChatID()).setText(string));
    }

    @Override
    public String getBotUsername() {
        return "";// getChannel().getChannelName();
    }

}
