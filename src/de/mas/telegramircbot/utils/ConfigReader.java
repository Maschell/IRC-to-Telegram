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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import de.mas.telegramircbot.irc.IRCConfig;
import de.mas.telegramircbot.telegram.BotConfig;
import lombok.extern.java.Log;

@Log
public class ConfigReader {
    public static List<BotConfig> readBotConfigs(){
        YamlReader reader = getYAMLReader(Settings.BOT_CONFIG_FILE);
        if(reader == null) return new ArrayList<>();
        
        List<BotConfig> list = new ArrayList<>();
        while (true) {
            BotConfig object;
            try {
                object = reader.read(BotConfig.class);
            } catch (YamlException e) {
                continue;
            }
            if (object == null) break;
            list.add(object);
        }
        return list;
    }

    public static void readAPIConfig() {
        YamlReader reader = getYAMLReader(Settings.API_CONFIG_FILE);
        try {
            APIConfig config = reader.read(APIConfig.class);
            if(config != null){
                Settings.IMGUR_API_CLIENTID = config.getImgurClientID();
            }
        } catch (YamlException e) {
            log.info("Error on reading APIConfig from " + Settings.API_CONFIG_FILE);
        }
    }
   
    public static IRCConfig readIRCConfig() {
        YamlReader reader = getYAMLReader(Settings.IRC_CONFIG_FILE);
        IRCConfig result = IRCConfig.getDefaultConfig();
        if(reader == null) return result;
        try {
            IRCConfig config = reader.read(IRCConfig.class);
            if(config != null){
                result = config;
            }
        } catch (YamlException e) {
            log.info("Error on reading IRCConfig from " + Settings.IRC_CONFIG_FILE);
        }
        return result;
    }
    
    private static YamlReader getYAMLReader(String path){
        try {
            return new YamlReader(new FileReader(path));
        } catch (FileNotFoundException e1) {
            return null;
        }
    }
}
