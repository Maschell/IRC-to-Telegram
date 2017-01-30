package de.mas.telegramircbot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Simple wrapper for println. This way we can easily mute this whole thing.
public class SimpleLogger {
    public static void log(String message){
        if(Settings.SIMPLE_LOGGING_ENABLED){
            System.out.println("[" +  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "] " +  message);
        }
    }
}
