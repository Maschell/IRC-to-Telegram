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
