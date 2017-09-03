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

import java.io.ByteArrayInputStream;
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
import de.mas.telegramircbot.telegram.common.TelegramUtils;

public class TelegramAttachmentImpl extends TelegramAttachment {
    TelegramAttachmentImpl(String fileName, InputStream in) {
        super(fileName, in);
    }

    TelegramAttachmentImpl(BotApiObject obj, DefaultAbsSender absSender) {
        super(obj, absSender);
    }

    @Override
    Attachment init(Document document, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(document.getFileId());
        return new TelegramAttachmentImpl(document.getFileName(), new ByteArrayInputStream(data));
    }

    @Override
    Attachment init(PhotoSize photo, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(photo.getFileId());
        return new TelegramAttachmentImpl(photo.getFileId() + ".jpg", new ByteArrayInputStream(data));
    }

    @Override
    Attachment init(Location location, DefaultAbsSender absSender) {
        /*
         * byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(document.getFileId());
         * return new TelegramAttachmentImpl(location.getFileName(), new ByteArrayInputStream(data));
         */
        return null;
    }

    @Override
    Attachment init(Video video, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(video.getFileId());
        return new TelegramAttachmentImpl(video.getFileId() + ".mp4", new ByteArrayInputStream(data));
    }

    @Override
    Attachment init(Voice voice, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(voice.getFileId());
        return new TelegramAttachmentImpl(voice.getFileSize().toString() + ".ogg", new ByteArrayInputStream(data));
    }

    @Override
    Attachment init(Audio audio, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(audio.getFileId());
        return new TelegramAttachmentImpl(audio.getTitle(), new ByteArrayInputStream(data));
    }

    @Override
    Attachment init(Sticker sticker, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(sticker.getFileId());
        return new TelegramAttachmentImpl(sticker.getSetName(), new ByteArrayInputStream(data));
    }

    @Override
    Attachment init(VideoNote vn, DefaultAbsSender absSender) {
        byte[] data = TelegramUtils.getInstance(absSender).getBytesByFileID(vn.getFileId());
        return new TelegramAttachmentImpl(vn.getFileId() + ".mp4", new ByteArrayInputStream(data));
    }

}
