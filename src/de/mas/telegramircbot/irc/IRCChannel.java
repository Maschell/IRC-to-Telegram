package de.mas.telegramircbot.irc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.mas.telegramircbot.utils.TelegramStrings;
import lombok.Getter;

public class IRCChannel {
    @Getter private final String channelName;
    @Getter private final IRCServer server;
    
    public IRCChannel(IRCServer server, String channelName) {
        this.server = server;
        this.channelName = channelName;
    }
    
    @Getter private List<String> userList = new ArrayList<>();

    public void join(String username) {
        if(!getUserList().contains(username)){
            sendMessageToTelegram(IRCMessage.createJoinMessage(username));
            getUserList().add(username);
        }
    }

    public void leave(String username) {
        if(getUserList().contains(username)){
            sendMessageToTelegram(IRCMessage.createLeaveMessage(username));
            getUserList().remove(username);
        }
    }

    @Getter private List<String> tempUserList = new ArrayList<>();
    public void addFullUserListInParts(List<String> user) {
        getTempUserList().addAll(user);
    }

    public void addFullUserListInPartsEnd() {
        getUserList().clear();
        getUserList().addAll(getTempUserList());
        getTempUserList().clear();
    }
        
    public IRCMessage peekLatestMessage(){
        return getMessages().peek();
    }
    
    public IRCMessage pollLatestMessage(){
        return getMessages().poll();
    }
    
    public void sendTextMessageToChannel(String text) throws IOException{
        getServer().sendTextMessageToChannel(this,text);
    }
    
    @Getter private Queue<IRCMessage> messages = new ConcurrentLinkedQueue<IRCMessage>();
    public void sendMessageToTelegram(IRCMessage m) {
        
        getMessages().add(m);
        if(getMessages().size()> 1000){
            //If the queue has 1000 messages, with no telegram bot attached, you probably won't need them anyways.
            getMessages().clear();
        }
    }

    public String getUserListFormatted() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(TelegramStrings.PEOPLE_IN_CHANNEL, getChannelName()));
        for(String s : getUserList()){
            sb.append(s + " ");
        }
        return sb.toString();
    }
}
