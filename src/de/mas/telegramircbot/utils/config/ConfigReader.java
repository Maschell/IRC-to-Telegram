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

package de.mas.telegramircbot.utils.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import de.mas.telegramircbot.utils.Settings;
import lombok.extern.java.Log;

@Log
public class ConfigReader {

    public static void main(String[] args) throws IOException {
        YamlWriter writer = new YamlWriter(new FileWriter(new File("test1.yaml")));
        IRCConfig config = IRCConfig.getDefaultConfig();
        config.getBotConfigs().put("channel1", new TelegramConfig("token1", 5));
        config.getBotConfigs().put("channel2", new TelegramConfig("token2", 5));

        IRCConfig config1 = IRCConfig.getDefaultConfig();
        config1.getBotConfigs().put("channel1", new TelegramConfig("token1", 5));
        config1.getBotConfigs().put("channel2", new TelegramConfig("token2", 5));

        writer.write(config);
        writer.write(config1);
        writer.close();

    }

    /*
     * public static void main(String[] args) throws IOException{
     * YamlWriter writer = new YamlWriter(new FileWriter(new File("test1.yaml")));
     * DiscordConfig config = new DiscordConfig();
     * config.setPassword("password");
     * config.setUsername("username");
     * config.getChannelList().put("channel1", new BotConfig("channelname", "username", "token", 5));
     * config.getChannelList().put("channel2", new BotConfig("channelname", "username", "token", 5));
     * writer.write(config);
     * writer.close();
     * 
     * IRCConfig c = getYAMLReader("test.yaml").read(IRCConfig.class);
     * System.out.println(c);
     * }
     */

    public static void readAPIConfig() {
        YamlReader reader = getYAMLReader(Settings.API_CONFIG_FILE);
        try {
            APIConfig config = reader.read(APIConfig.class);
            if (config != null) {
                Settings.IMGUR_API_CLIENTID = config.getImgurClientID();
            }
        } catch (YamlException e) {
            log.info("Error on reading APIConfig from " + Settings.API_CONFIG_FILE);
        }
    }

    public static List<IRCConfig> readIRCConfig() {
        YamlReader reader = getYAMLReader(Settings.IRC_CONFIG_FILE);
        if (reader == null) return new ArrayList<>();
        List<IRCConfig> list = new ArrayList<>();
        while (true) {
            IRCConfig object = null;
            try {
                object = reader.read(IRCConfig.class);
            } catch (YamlException e) {
                e.printStackTrace();
            }
            if (object == null) break;
            list.add(object);
        }
        return list;
    }

    public static DiscordConfig readDiscordConfig() {
        YamlReader reader = getYAMLReader(Settings.DISCORD_CONFIG_FILE);
        if (reader == null) return null;
        try {
            return reader.read(DiscordConfig.class);
        } catch (YamlException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static YamlReader getYAMLReader(String path) {
        try {
            return new YamlReader(new FileReader(path));
        } catch (FileNotFoundException e1) {
            return null;
        }
    }

}
