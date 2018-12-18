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

package de.mas.telegramircbot.utils.images;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.mas.telegramircbot.utils.Settings;

public class ImgurUploader {

    public static String uploadImageAndGetLink(InputStream is) throws IOException {
        return uploadImageAndGetLink(IOUtils.toByteArray(is));
    }

    public static String uploadImageAndGetLink(byte[] image) throws IOException {
        return uploadImageAndGetLink(Settings.IMGUR_API_CLIENTID, image);
    }

    public static String uploadImageAndGetLink(String clientID, InputStream is) throws IOException {
        return uploadImageAndGetLink(clientID, IOUtils.toByteArray(is));
    }

    public static String uploadImageAndGetLink(String clientID, byte[] image) throws IOException {
        URL url;
        url = new URL(Settings.IMGUR_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        String dataImage = Base64.getEncoder().encodeToString(image);
        String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Client-ID " + clientID);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        conn.connect();
        StringBuilder stb = new StringBuilder();
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            stb.append(line).append("\n");
        }
        wr.close();
        rd.close();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ImgurResponse.class, new ImgurResponseDeserializer());
        Gson gson = gsonBuilder.create();

        // The JSON data
        try {
            ImgurResponse response = gson.fromJson(stb.toString(), ImgurResponse.class);
            return response.getLink();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stb.toString();
    }

}