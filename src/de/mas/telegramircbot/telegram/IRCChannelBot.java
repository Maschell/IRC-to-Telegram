package de.mas.telegramircbot.telegram;
import java.io.IOException;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import de.mas.telegramircbot.irc.IRCChannel;
import de.mas.telegramircbot.irc.IRCMessage;
import de.mas.telegramircbot.irc.IRCServer;
import de.mas.telegramircbot.utils.TelegramStrings;
import de.mas.telegramircbot.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class IRCChannelBot extends TelegramLongPollingBot {        
        @Getter private final IRCChannel channel;
        @Getter private final String botUsername;
        @Getter private final String botToken;
        @Getter private final long telegramChatID;
        
        @Getter private boolean threadStarted = false;
        @Setter @Getter private boolean threadRunning = false;
        
        
        public static IRCChannelBot createBot(IRCServer server, BotConfig config) {   
            de.mas.telegramircbot.irc.IRCChannel channel = server.getChannel(config.getChannelName());
            return new IRCChannelBot(channel,config.getBotUsername(),config.getBotToken(),config.getTelegramChatID()); 
        }
        
        private IRCChannelBot(IRCChannel channel,String botUsername,String botToken,long telegramChatID) {
            super();
            this.channel = channel;
            this.botUsername = botUsername;
            this.botToken = botToken;
            this.telegramChatID = telegramChatID;
            
            startCheckForMessageThread();
            
            try {
                sendMessage(String.format(TelegramStrings.BOT_ONLINE, getChannel().getChannelName()));
            } catch (TelegramApiException e) {
            }
        }
        
        private void startCheckForMessageThread() {
            if(!threadStarted){
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        int sleepfor = 500;
                        setThreadRunning(true);
                        while(isThreadRunning()){
                            sleepfor = handleIRCMessages(); 
                            Utils.sleep(sleepfor);
                        }
                    }            
                }).start();
                threadStarted = true;
            }else{
                log.info("Error: startCheckForMessageThread was already called for " + channel.getChannelName());
            }
        }
        
        //handles the IRC Messages and retuns the new sleeping time.
        private int handleIRCMessages(){
            IRCMessage m = null;
            int result = 50; 
            if((m = getChannel().peekLatestMessage()) != null){
                try {
                    sendIRCMessageToTelegram(m); //Try to send the message.                                
                    getChannel().pollLatestMessage(); //On success remove from the list.
                    result = 50;
                } catch (TelegramApiException e) {
                    log.info("Something weird happend to the Telegram API " + e.getMessage());
                    result = 5000;
                }
            }else{
                result = 500;
            }
            return result;
        }
       
        @Override
        public void onUpdateReceived(Update update) {            
            if (update.hasMessage() && isFromValidUser(update.getMessage().getChatId())) {
                if(update.getMessage().hasText()){
                    String text = update.getMessage().getText();
                    switch(text.toLowerCase()){
                        case CommandsDefs.LIST_USER:
                            sendIRCUserListToTelegram();
                            break;
                        default:
                            sendTelegramMessageToIRC(text);
                            break;
                    } 
                }
            }else{
                if(update.hasMessage()){
                    long chatID = update.getMessage().getChatId();
                    log.info("Oh. Someone tried to use the bot! ChatID: " + chatID);
                    try {
                        sendMessage(new SendMessage().setChatId(chatID).setText(String.format(TelegramStrings.BOT_PRIVATE,chatID)));
                    } catch (TelegramApiException e) {
                    }
                }
            }
        }

        private boolean isFromValidUser(Long chatId) {
            return(chatId == getTelegramChatID());
        }

        private void sendTelegramMessageToIRC(String text) {
            try {
                for(String s : text.split("\\n|\\r")){ //Split up lines into single messages
                    s = Utils.replacesSmileys(s);
                    getChannel().sendTextMessageToChannel(s);
                }                
            } catch (IOException e) {
                log.info("Sending message into channel " + getChannel().getChannelName() + " failed: " + e.getMessage());
            }            
        }
        
        public void sendIRCMessageToTelegram(IRCMessage message) throws TelegramApiException{
            sendMessage(message.getFormattedString());
        }    

        private void sendIRCUserListToTelegram() {
            String userList = getChannel().getUserListFormatted();            
            try {
                sendMessage(userList);
            } catch (TelegramApiException e) {
                log.info("Sending userlist failed" + e.getMessage());
            }
        }

        private void sendMessage(String string) throws TelegramApiException {
            sendMessage(new SendMessage().setChatId(getTelegramChatID()).setText(string));
        }

        public void stopBot() {
            setThreadRunning(false);
            log.warning("IRCChannel for " + getChannel().getChannelName() + " has been stopped. Probably the bot token was wrong?");
        }
}
