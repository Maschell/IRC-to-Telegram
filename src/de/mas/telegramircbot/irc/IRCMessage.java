package de.mas.telegramircbot.irc;
import java.util.Date;

import de.mas.telegramircbot.utils.TelegramStrings;
import lombok.Data;

@Data
public class IRCMessage implements Comparable<IRCMessage>{    
    private final Date timestamp = new Date();
    private final String user;
    private final String message;
    private final String channel;
    private MessageTypes messageType = MessageTypes.TextMessage;
    
    private IRCMessage(String channel, String user,String message){
        this.user = user;
        this.message = message;
        this.channel = channel;
    }
    
    private IRCMessage(String user, String message) {
        this("",user,message);
    }
    
    private IRCMessage(String user) {
        this(user,"");
    }
    
    public static IRCMessage createSystemMessage(String message){
        IRCMessage result = new IRCMessage("",message);
        result.setMessageType(MessageTypes.SystemMessage);
        return result;
    }

    public static IRCMessage createTextMessage(String user,String message){
        IRCMessage result = new IRCMessage(user,message);
        result.setMessageType(MessageTypes.TextMessage);
        return result;
    }
    
    public static IRCMessage createJoinMessage(String user){
        IRCMessage result = new IRCMessage(user);
        result.setMessageType(MessageTypes.JoinMessage);
        return result;
    }
    
    public static IRCMessage createLeaveMessage(String user){
        IRCMessage result = new IRCMessage(user);
        result.setMessageType(MessageTypes.LeaveMessage);
        return result;
    }

    public String getFormattedString() {
        switch(getMessageType()){
            case TextMessage:
                return String.format(TelegramStrings.TEXT_MESSAGE, user,message);
            case JoinMessage:
                return String.format(TelegramStrings.JOIN_MESSAGE, user);
            case LeaveMessage:
                return String.format(TelegramStrings.LEAVE_MESSAGE, user);
            case SystemMessage:
                return String.format(TelegramStrings.SYSTEM_MESSAGE, message);
            default:
                return String.format(TelegramStrings.TEXT_MESSAGE, user,message);         
        }       
    }

    @Override
    public int compareTo(IRCMessage o) {
        return this.timestamp.compareTo(o.timestamp);
    }
    
    @Override
    public String toString() {
        return timestamp + "  " + channel  +" - " +  user + ": " + message;
    }
    
    public enum MessageTypes{
        TextMessage,
        JoinMessage,
        LeaveMessage,
        SystemMessage
    }
}
