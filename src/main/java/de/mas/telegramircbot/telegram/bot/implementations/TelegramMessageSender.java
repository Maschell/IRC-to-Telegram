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

package de.mas.telegramircbot.telegram.bot.implementations;

import java.io.InputStream;

import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import de.mas.telegramircbot.common.DefaultMessageReturned;
import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.message.Document;
import de.mas.telegramircbot.message.User;
import de.mas.telegramircbot.telegram.bot.TelegramChannelBot;

public class TelegramMessageSender implements MessageSender {
    private final TelegramChannelBot channel;

    public TelegramMessageSender(TelegramChannelBot telegramChannelBot) {
        channel = telegramChannelBot;
    }

    private org.telegram.telegrambots.api.objects.Message sendDocument(String filename, InputStream in, User author) throws TelegramApiException {
        String caption = "";
        if(author != null && author.getName() != null)  caption = "From: " + author.getName();
        return channel.sendDocument(new SendDocument().setChatId(channel.getTelegramChatID()).setNewDocument(filename, in).setCaption(caption));
    }

    private org.telegram.telegrambots.api.objects.Message sendPhoto(String filename, InputStream in, User author) throws TelegramApiException {
        String caption = "";
        if(author != null && author.getName() != null)  caption = "From: " + author.getName();
        return channel.sendPhoto(new SendPhoto().setChatId(channel.getTelegramChatID()).setNewPhoto(filename, in).setCaption(caption));
    }

    private org.telegram.telegrambots.api.objects.Message sendVideo(String filename, InputStream in, User author) throws TelegramApiException {
        String caption = "";
        if(author != null && author.getName() != null)  caption = "From: " + author.getName();
        return channel.sendVideo(new SendVideo().setChatId(channel.getTelegramChatID()).setNewVideo(filename, in).setCaption(caption));
    }

    @Override
    public MessageReturned sendFile(Document d) throws Exception {
        String filename = d.getFilename();
        InputStream in = d.getDocumentStream();
        User author = d.getAuthor();

        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png") || filename.endsWith(".bmp")) {
            return new DefaultMessageReturned(new TelegramMessage(sendPhoto(filename, in, author), channel));
        } else if (filename.endsWith(".mp4") || filename.endsWith(".mov")) {
            return new DefaultMessageReturned(new TelegramMessage(sendVideo(filename, in, author), channel));
        }

        return new DefaultMessageReturned(new TelegramMessage(sendDocument(filename, in, author), channel));
    }

    @Override
    public MessageReturned sendTextMessage(String formattedString) throws Exception {
        return new DefaultMessageReturned(
                new TelegramMessage(channel.execute(new SendMessage().setChatId(channel.getTelegramChatID()).setText(formattedString)), channel));
    }

    @Override
    public MessageReturned editMessage(Message to, String newContent) throws Exception {

        channel.execute(new EditMessageText().setChatId(channel.getTelegramChatID()).setMessageId(Integer.parseInt(to.getID())).setText(newContent));
        return null;
    }

}
