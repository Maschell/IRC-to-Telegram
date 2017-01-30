package de.mas.telegramircbot.utils;

public class Utils {
    public static String replacesSmileys(String s) {
        s = s.replaceAll("\\ud83d\\ude06", "xD"); //😆
        s = s.replaceAll("\\ud83d\\ude4a", ":X"); //🙊
        s = s.replaceAll("\u2764", "<3");         //❤️
        return s;
    }

    //To get rid of the try catch thing..
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //And ignore the exception. ups.
        }
    }

    public static boolean isNumeric(String string) {
        boolean isValue = false;
        try{
            Integer.parseInt(string);
            isValue = true;
        }catch(NumberFormatException e){            
        }
        return isValue;
    }
}
