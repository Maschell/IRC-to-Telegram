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
package de.mas.telegramircbot.telegram.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.common.io.Files;

final public class TelegramUtils {
    private static final Map<DefaultAbsSender, TelegramUtils> instances = new HashMap<>();

    public static TelegramUtils getInstance(DefaultAbsSender telegramChannel) {
        TelegramUtils instance = instances.get(telegramChannel);

        if (instance == null) {
            instance = new TelegramUtils(telegramChannel);
            instances.put(telegramChannel, instance);
        }
        return instance;
    }

    private final DefaultAbsSender telegramChannel;

    private TelegramUtils(DefaultAbsSender telegramChannel) {
        this.telegramChannel = telegramChannel;
    }

    public static PhotoSize getBestPictureFileID(List<PhotoSize> photos) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        int maxSize = 0;
        PhotoSize result = null;
        for (PhotoSize photo : photos) {
            if (photo.getFileSize() > maxSize) {
                maxSize = photo.getFileSize();
                result = photo;
            }
        }
        return result;
    }

    public byte[] getBytesByFileID(String fileID) {
        FileInputStream fileInputStream = null;
        try {
            GetFile getFileRequest = new GetFile();

            getFileRequest.setFileId(fileID);
            File uploadedFilePath = telegramChannel.execute(getFileRequest);

            java.io.File fileFromSystem = telegramChannel.downloadFile(uploadedFilePath.getFilePath());

            byte[] bytes = Files.toByteArray(fileFromSystem);
            return bytes;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
