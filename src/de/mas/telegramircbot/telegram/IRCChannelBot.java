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

package de.mas.telegramircbot.telegram;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.google.common.io.Files;

import de.mas.telegramircbot.irc.IRCChannel;
import de.mas.telegramircbot.irc.IRCMessage;
import de.mas.telegramircbot.irc.IRCServer;
import de.mas.telegramircbot.utils.TelegramStrings;
import de.mas.telegramircbot.utils.Utils;
import de.mas.telegramircbot.utils.images.ImgurUploader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class IRCChannelBot extends TelegramLongPollingBot {
        public static final String FILE_API = "https://api.telegram.org/file/bot";
    
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
                    String[] lines = text.split(" ");
                    switch(lines[0].toLowerCase()){
                        case CommandsDefs.LIST_USER:
                            sendIRCUserListToTelegram();
                            break;
                        case CommandsDefs.SET_USER:
                            setUserForPrivateMessageResponses(text.substring(CommandsDefs.SET_USER.length()+1));
                            break;
                        default:
                            sendTelegramMessageToIRC(text);
                            break;
                    } 
                }
                if(update.getMessage().hasPhoto()){
                   processImage(update.getMessage().getPhoto());
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

        private void processImage(List<PhotoSize> photos){
            FileInputStream fileInputStream = null;
            try {
                if(photos == null || photos.isEmpty()){
                    return;
                }
                int maxSize = 0;
                String fileID = "";
                
                for(PhotoSize photo : photos){
                    if(photo.getFileSize() > maxSize){
                        maxSize = photo.getFileSize();
                        fileID = photo.getFileId();
                    }
                }
                GetFile getFileRequest = new GetFile();

                getFileRequest.setFileId(fileID);
                File file = getFile(getFileRequest);
                
                java.io.File fileFromSystem = downloadFile(file.getFilePath());

                byte[] bytes = Files.toByteArray(fileFromSystem);
               
                sendTelegramMessageToIRC(ImgurUploader.uploadImageAndGetLink(bytes));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(fileInputStream != null){
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        private void setUserForPrivateMessageResponses(String username) {
           getChannel().setUsernameToRespond(username);
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
