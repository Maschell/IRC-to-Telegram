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

package de.mas.telegramircbot.utils;

public class Settings {
    public static final String BOT_CONFIG_FILE = "bot_config.yml";
    public static final String IRC_CONFIG_FILE = "irc_config.yml";
    public static final String API_CONFIG_FILE = "api_config.yml";
    
    public static final boolean SIMPLE_LOGGING_ENABLED = true;
        
    //Will be used if no config is provided.
    public static String IRC_SERVER = "localhost";
    public static String IRC_LOGIN = "<login>";
    public static String IRC_PASS = "<password>";
    public static String IRC_NICK = "<nick>";
    public static int IRC_PORT = 12345;
    
    public static final String PRIVATE_MESSAGES_CHANNEL_NAME = "PRIVATE_MESSAGES"; //This must not start with #
    public static String IMGUR_API_CLIENTID = "";
    public static final String IMGUR_API_URL = "https://api.imgur.com/3/image";
   
}
