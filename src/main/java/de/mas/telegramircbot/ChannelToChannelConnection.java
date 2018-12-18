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
package de.mas.telegramircbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import de.mas.telegramircbot.common.interfaces.Channel;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.MessageResult;
import de.mas.telegramircbot.utils.CacheMap;
import de.mas.telegramircbot.utils.Utils;
import lombok.extern.java.Log;

@Log
public class ChannelToChannelConnection {
    private final Channel channel1;
    private final Channel channel2;

    ChannelToChannelConnection(Channel channel1, Channel channel2) {
        this.channel1 = channel1;
        this.channel2 = channel2;
        log.info(ChannelToChannelConnection.class.getName() + " started for: " + channel1 + " and " + channel2);
        run();
    }

    public void run() {
        new Thread(() -> {
            while (true) {
                handleMessages();
                Utils.sleep(50);
            }
        }, "message-handling").start();

        new Thread(() -> {
            while (true) {
                MessageResult res = null;
                try {
                    res = getResult(futureChannelToBotSentQueue.take());
                } catch (Exception e) {
                    continue;
                }
                if (res != null) {
                    channelToBotMessageIDs.put(res.getFromID(), res.getToID());
                }
            }
        }, "channel-to-bot-messages").start();

        new Thread(() -> {
            while (true) {
                MessageResult res = null;
                try {
                    res = getResult(futureBotToChannelSentQueue.take());
                } catch (Exception e) {
                    continue;
                }
                if (res != null) {
                    botToChannelMessageIDs.put(res.getFromID(), res.getToID());
                }
            }
        }, "bot-to-channel-messages").start();
    }

    private BlockingQueue<Future<MessageResult>> futureChannelToBotSentQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Future<MessageResult>> futureBotToChannelSentQueue = new LinkedBlockingQueue<>();

    private Map<String, String> channelToBotMessageIDs = new CacheMap<String, String>(100);
    private Map<String, String> botToChannelMessageIDs = new CacheMap<String, String>(100);

    private MessageResult getResult(Future<MessageResult> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e1) {
            return null;
        }
    }

    private void handleMessages() {
        List<MessageContainer> from = channel1.getMessagesFrom();
        if (from == null) {
            from = new ArrayList<>();
        }
        List<Future<MessageResult>> to = channel2.sendMessagesToThisChannel(from);
        if (to == null) {
            to = new ArrayList<>();
        }
        futureChannelToBotSentQueue.addAll(to);

        from = channel2.getMessagesFrom();
        if (from == null) {
            from = new ArrayList<>();
        }
        to = channel1.sendMessagesToThisChannel(from);
        if (to == null) {
            to = new ArrayList<>();
        }
        futureBotToChannelSentQueue.addAll(to);

        List<MessageContainer> editMessages = new ArrayList<>();

        for (MessageContainer m : channel1.getEditedMessagesFrom()) {
            if (m == null) continue;
            String botMSGID = channelToBotMessageIDs.get(m.getFromID());
            if (botMSGID != null) {
                m.getEditMessage().setToID(botMSGID);
                editMessages.add(m);
            }
        }
        channel2.sendEditedMessagesToThisChannel(editMessages);

        editMessages.clear();

        for (MessageContainer m : channel2.getEditedMessagesFrom()) {
            if (m == null) continue;
            String botMSGID = botToChannelMessageIDs.get(m.getFromID());
            if (botMSGID != null) {
                m.getEditMessage().setToID(botMSGID);
                editMessages.add(m);
            }
        }
        channel1.sendEditedMessagesToThisChannel(editMessages);
    }

}
