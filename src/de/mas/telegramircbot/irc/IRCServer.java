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

package de.mas.telegramircbot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.mas.telegramircbot.irc.implementations.IRCMessage;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.MessageStrings;
import de.mas.telegramircbot.message.User;
import de.mas.telegramircbot.utils.Settings;
import de.mas.telegramircbot.utils.SimpleLogger;
import de.mas.telegramircbot.utils.Utils;
import de.mas.telegramircbot.utils.config.IRCConfig;
import lombok.extern.java.Log;

@Log
public class IRCServer implements Runnable {

    private static HashMap<IRCConfig, IRCServer> servers = new HashMap<>();

    private static IRCServer getServer(IRCConfig ircConfig) {
        if (!servers.containsKey(ircConfig)) {
            servers.put(ircConfig, new IRCServer(ircConfig));
        }
        return servers.get(ircConfig);
    }

    private final IRCConfig ircConfig;

    public static IRCServer startServer(IRCConfig ircConfig) {
        IRCServer server;
        new Thread((server = getServer(ircConfig))).start();
        return server;
    }

    private IRCServer(IRCConfig ircConfig) {
        this.ircConfig = ircConfig;
        this.channelPM = new IRCChannelPrivateMessages(this);
    }

    public IRCConfig getIrcConfig() {
        return ircConfig;
    }

    private Socket serverSocket = null;

    private void openSocket(String server, int port) throws IOException {
        closeSocketIfOpen();
        serverSocket = new Socket(server, port);
    }

    private Socket getSocket() throws IOException {
        if (serverSocket == null) {
            throw new IOException();
        }
        return serverSocket;
    }

    private void closeSocketIfOpen() throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }

    private BufferedWriter writer = null;

    private BufferedWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new BufferedWriter(new OutputStreamWriter(getSocket().getOutputStream()));
        }
        return writer;
    }

    private void closeWriter() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

    private BufferedReader reader = null;

    private BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        }
        return reader;
    }

    private void closeReader() throws IOException {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    private Map<String, IRCChannel> channels = new HashMap<>();
    private final IRCChannelPrivateMessages channelPM;

    public IRCChannel getChannel(String channelName) {
        String channel_name = channelName.toLowerCase();

        if (channel_name.equalsIgnoreCase(Settings.PRIVATE_MESSAGES_CHANNEL_NAME)) {
            return channelPM;
        }

        if (!channels.containsKey(channel_name)) {
            try {
                if (!channel_name.equalsIgnoreCase(Settings.PRIVATE_MESSAGES_CHANNEL_NAME)) {
                    do_join_channel(channel_name);
                }
            } catch (IOException e) {
            }
            channels.put(channel_name, new IRCChannel(this, channel_name));
        }
        return channels.get(channel_name);
    }

    private void login() throws IOException {
        StringBuilder sb = new StringBuilder();
        if (getIrcConfig().getPass() != null && !getIrcConfig().getPass().isEmpty()) {
            sb.append("PASS " + getIrcConfig().getPass() + "\r\n");
        }
        sb.append("NICK " + getIrcConfig().getNick() + "\r\n");
        sb.append("USER " + getIrcConfig().getLogin() + " 8 * : Java IRC Hacks Bot"); // \r\n removed on purpose! Will be added by sendMessage.

        sendMessage(sb.toString());

        lookForLoginErrors();
    }

    private void lookForLoginErrors() throws IOException {
        BufferedReader reader = getReader();
        String line = null;
        while ((line = reader.readLine()) != null) {
            SimpleLogger.log(line);
            if (line.indexOf("004") >= 0) {
                // Login successful!
                break;
            } else if (line.indexOf("433") >= 0) {
                SimpleLogger.log("Nickname is already in use.");
                System.exit(0);
            } else if (line.indexOf("464") >= 0) {
                SimpleLogger.log("Couldn't connect: " + line.substring(line.indexOf("464")));
                System.exit(0);
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                openSocket(getIrcConfig().getServer(), getIrcConfig().getPort());

                login();

                String line;
                // Keep reading lines from the server.
                while ((line = reader.readLine()) != null) {
                    handleMessage(line);
                }

            } catch (IOException e) {
                log.info("Connection error! Lets try it again in 10 seconds! " + e.getMessage());
                Utils.sleep(10*1000);
            } finally {
                try {
                    closeSocketIfOpen();
                    closeWriter();
                    closeReader();
                } catch (IOException e) {
                    // TODO: handle?
                }
            }
        }
    }

    private void handleMessage(String line) throws IOException {
        if (line.toUpperCase().startsWith("PING ")) {
            String ping_server = line.substring(5);
            recv_ping(ping_server);
        } else {
            String[] message = line.split(" ");
            if (message.length > 1) {
                String command = message[1];
                if (!Utils.isNumeric(command)) {
                    if ((command.equals("JOIN") || command.equals("PART")) && message.length > 2) {
                        String username = message[0].substring(1).split("!")[0];
                        String channel = message[2].substring(1);

                        if (!channel.startsWith("#")) channel = "#" + channel;

                        switch (command) {
                        case "JOIN":
                            recv_join(channel, username);
                            break;
                        case "PART":
                            recv_part(channel, username);
                            break;
                        default:
                            break;
                        }
                    } else if ((command.equals("PRIVMSG") || command.equals("QUIT")) && message.length > 3) {
                        String username = message[0].substring(1).split("!")[0];

                        switch (command) {
                        case "QUIT":
                            recv_quit(username);
                            break;
                        case "PRIVMSG":
                            String channelname = message[2];
                            String textmessage = line.split(channelname + " :")[1];
                            PRIVMSG(channelname, username, textmessage);
                            break;
                        default:
                            break;
                        }
                    } else {
                        SimpleLogger.log(line);
                    }
                } else {
                    switch (Integer.parseInt(command)) { // Should never throw an Exception.
                    case 353: // RPL_NAMREPLY - gets List of user in the Channel until a 366 arrived
                        if (message.length < 5) {
                            break;
                        }
                        String channel = message[4];
                        List<String> user = new ArrayList<String>();

                        for (int i = 5; i < message.length; i++) {
                            String username = message[i];
                            if (message[i].startsWith(":")) {
                                message[i].substring(1);
                            }
                            username = Utils.escapeUsername(username);
                            user.add(username);
                        }
                        recv_RPL_NAMREPLY(channel, user);
                        break;
                    case 366: // RPL_ENDOFNAMES
                        if (message.length < 5) {
                            break;
                        }
                        channel = message[3];
                        recv_RPL_ENDOFNAMES(channel);
                        break;
                    case 401: // RPL_ENDOFNAMES
                        if (message.length < 4) {
                            break;
                        }
                        getChannel(Settings.PRIVATE_MESSAGES_CHANNEL_NAME)
                                .sendMessageTo(MessageContainer.createSystemMessage("IRC", String.format(MessageStrings.USER_NOT_FOUND_MESSAGE, message[3])));
                        break;
                    default:
                        SimpleLogger.log(line);
                        break;
                    }
                }
            }
        }
    }

    /*
     * Command handler
     */
    private void recv_quit(String username) {
        SimpleLogger.log("Quit: " + username + " has quit the server");
        for (Entry<String, IRCChannel> entry : channels.entrySet()) {
            entry.getValue().leave(username);
        }
    }

    private void PRIVMSG(String channel, String username, String text) throws IOException {
        if (channel.startsWith("#")) { // It's in a real channel
            SimpleLogger.log(channel + " - " + username + ": " + text);
            getChannel(channel).addMessageFrom(new IRCMessage(new User(username), text));
        } else {
            SimpleLogger.log("Private Message from " + username + ": " + text);
            channelPM.addMessageFrom(new IRCMessage(new User(username), text));
        }
    }

    private void recv_RPL_NAMREPLY(String channel, List<String> user) {
        SimpleLogger.log(channel + " user : " + Arrays.toString(user.toArray()));
        getChannel(channel).addFullUserListInParts(user);
    }

    private void recv_RPL_ENDOFNAMES(String channel) {
        SimpleLogger.log(channel + " :End of NAMES list");
        getChannel(channel).addFullUserListInPartsEnd();
    }

    private void recv_join(String channel, String username) {
        SimpleLogger.log("Joining: " + username + " joined " + channel);
        getChannel(channel).join(username);
    }

    private void recv_part(String channel, String username) {
        SimpleLogger.log("Leaving: " + username + " left " + channel);
        getChannel(channel).leave(username);
    }

    private void recv_ping(String ping_server) throws IOException {
        SimpleLogger.log("Got PING from " + ping_server);
        send_pong(ping_server);
    }

    private void send_pong(String ping_server) throws IOException {
        SimpleLogger.log("Send PONG to " + ping_server);
        sendMessage("PONG to " + ping_server);
    }

    private void do_join_channel(String channel_name) throws IOException {
        sendMessage("JOIN :" + channel_name);
    }

    public void sendMessage(String message) throws IOException {
        getWriter().write(message + "\r\n");
        getWriter().flush();
    }
}
