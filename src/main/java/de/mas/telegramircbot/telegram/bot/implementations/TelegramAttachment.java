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

import java.io.IOException;
import java.io.InputStream;

import org.telegram.telegrambots.api.interfaces.BotApiObject;
import org.telegram.telegrambots.api.objects.Audio;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Video;
import org.telegram.telegrambots.api.objects.VideoNote;
import org.telegram.telegrambots.api.objects.Voice;
import org.telegram.telegrambots.api.objects.stickers.Sticker;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import de.mas.telegramircbot.common.interfaces.Attachment;

abstract public class TelegramAttachment implements Attachment {
    private Attachment attachment = null;
    private String fileName;
    private InputStream in;

    public TelegramAttachment(String fileName, InputStream in) {
        this.fileName = fileName;
        this.in = in;
    }

    public TelegramAttachment(BotApiObject obj, DefaultAbsSender absSender) {
        if (obj instanceof Document) {
            attachment = init((Document) obj, absSender);
        }

        if (obj instanceof PhotoSize) {
            attachment = init((PhotoSize) obj, absSender);
        }

        if (obj instanceof Location) {
            attachment = init((Location) obj, absSender);
        }

        if (obj instanceof Video) {
            attachment = init((Video) obj, absSender);
        }

        if (obj instanceof Voice) {
            attachment = init((Voice) obj, absSender);
        }

        if (obj instanceof Audio) {
            attachment = init((Audio) obj, absSender);
        }

        if (obj instanceof Sticker) {
            attachment = init((Sticker) obj, absSender);
        }

        if (obj instanceof VideoNote) {
            attachment = init((VideoNote) obj, absSender);
        }
    }

    abstract Attachment init(Document document, DefaultAbsSender absSender);

    abstract Attachment init(PhotoSize photo, DefaultAbsSender absSender);

    abstract Attachment init(Location location, DefaultAbsSender absSender);

    abstract Attachment init(Video video, DefaultAbsSender absSender);

    abstract Attachment init(Voice voice, DefaultAbsSender absSender);

    abstract Attachment init(Audio audio, DefaultAbsSender absSender);

    abstract Attachment init(Sticker sticker, DefaultAbsSender absSender);

    abstract Attachment init(VideoNote vn, DefaultAbsSender absSender);

    @Override
    public String getFileName() {
        if (attachment != null) {
            return attachment.getFileName();
        }
        return fileName;
    }

    @Override
    public InputStream getAsInputStream() throws IOException {
        if (attachment != null) {
            return attachment.getAsInputStream();
        }
        return in;
    }

}
