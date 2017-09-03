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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.telegram.telegrambots.api.objects.Audio;
import org.telegram.telegrambots.api.objects.Video;
import org.telegram.telegrambots.api.objects.VideoNote;
import org.telegram.telegrambots.api.objects.Voice;
import org.telegram.telegrambots.api.objects.stickers.Sticker;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import de.mas.telegramircbot.common.interfaces.Attachment;
import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.message.User;
import de.mas.telegramircbot.telegram.common.TelegramUtils;

public class TelegramMessage implements Message {
    private final org.telegram.telegrambots.api.objects.Message message;
    private final DefaultAbsSender absSender;

    public TelegramMessage(org.telegram.telegrambots.api.objects.Message message, DefaultAbsSender absSender) {
        this.message = message;
        this.absSender = absSender;
    }

    @Override
    public Collection<Attachment> getAttachments() {
        List<Attachment> m = new ArrayList<>();

        if (message.hasDocument()) {
            m.add(new TelegramAttachmentImpl(message.getDocument(), absSender));
        }
        if (message.hasPhoto()) {
            m.add(new TelegramAttachmentImpl(TelegramUtils.getBestPictureFileID(message.getPhoto()), absSender));
        }
        if (message.hasLocation()) {
            m.add(new TelegramAttachmentImpl(message.getLocation(), absSender));
        }

        Video video = message.getVideo();
        if (video != null) {
            m.add(new TelegramAttachmentImpl(video, absSender));
        }

        Voice v = message.getVoice();
        if (v != null) {
            m.add(new TelegramAttachmentImpl(v, absSender));
        }

        Audio a = message.getAudio();
        if (a != null) {
            m.add(new TelegramAttachmentImpl(a, absSender));
        }

        Sticker s = message.getSticker();
        if (s != null) {
            m.add(new TelegramAttachmentImpl(s, absSender));
        }

        VideoNote vn = message.getVideoNote();
        if (vn != null) {
            m.add(new TelegramAttachmentImpl(vn, absSender));
        }
        return m;
    }

    @Override
    public String getID() {
        return Integer.toString(message.getMessageId());
    }

    @Override
    public boolean hasTextContent() {
        return message.hasText();
    }

    @Override
    public Object getInternalMessage() {
        return message;
    }

    @Override
    public User getAuthor() {
        return new User(message.getFrom().getUserName());
    }

}
