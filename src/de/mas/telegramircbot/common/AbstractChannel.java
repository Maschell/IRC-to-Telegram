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

package de.mas.telegramircbot.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.mas.telegramircbot.common.interfaces.Attachment;
import de.mas.telegramircbot.common.interfaces.Channel;
import de.mas.telegramircbot.common.interfaces.Message;
import de.mas.telegramircbot.common.interfaces.MessageHandler;
import de.mas.telegramircbot.common.interfaces.MessageReturned;
import de.mas.telegramircbot.common.interfaces.MessageSender;
import de.mas.telegramircbot.message.Command;
import de.mas.telegramircbot.message.EditMessage;
import de.mas.telegramircbot.message.MessageContainer;
import de.mas.telegramircbot.message.MessageResult;
import de.mas.telegramircbot.utils.CacheMap;
import lombok.extern.java.Log;

@Log
public abstract class AbstractChannel implements Channel {
    protected final ExecutorService pool = Executors.newCachedThreadPool();
    private Queue<MessageContainer> messagesToBot = new ConcurrentLinkedQueue<>();
    private Queue<MessageContainer> editedmessagesToBot = new ConcurrentLinkedQueue<>();
    protected MessageHandler messageHandler;

    protected AbstractChannel(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    Map<String, Message> ownMessages = new CacheMap<String, Message>(1000);

    public void addOwnMessage(Message msg) {
        ownMessages.put(msg.getID(), msg);
    }

    public void sendMessageTo(MessageContainer m) {
        sendMessageTo(Arrays.asList(m));
    }

    protected void sendMessageTo(List<MessageContainer> m) {
        messagesToBot.addAll(m);
    }

    protected void sendEditedMessagesTo(MessageContainer m) {
        sendEditedMessagesTo(Arrays.asList(m));
    }

    protected void sendEditedMessagesTo(List<MessageContainer> m) {
        editedmessagesToBot.addAll(m);
    }

    @Override
    public List<MessageContainer> getMessagesFrom() {
        List<MessageContainer> result = new ArrayList<>();
        MessageContainer m = null;
        while ((m = messagesToBot.poll()) != null) {
            result.add(m);
        }
        return result;
    }

    @Override
    public List<MessageContainer> getEditedMessagesFrom() {
        List<MessageContainer> result = new ArrayList<>();
        MessageContainer m = null;
        while ((m = editedmessagesToBot.poll()) != null) {
            result.add(m);
        }
        return result;
    }

    public boolean isMessageSentByTheBot(de.btobastian.javacord.entities.message.Message msg) {
        return ownMessages.containsKey(msg.getId());
    }

    @Override
    public List<Future<MessageResult>> sendEditedMessagesToThisChannel(List<MessageContainer> list) {
        List<Future<MessageResult>> result = new ArrayList<>();
        for (MessageContainer mc : list) {
            if (!mc.hasEditMessage()) continue;
            EditMessage em = mc.getEditMessage();
            Message m = ownMessages.get(em.getToID());
            if (m != null) {
                result.add(pool.submit(() -> {
                    try {
                        messageHandler.applyEditMessageTo(getMessageReceiverForSending(), m, em.getFormattedString());
                        return new MessageResult(true, mc.getFromID(), em.getToID());
                    } catch (Exception e) {
                        return new MessageResult(false, mc.getFromID());
                    }
                }));

            } else {
                log.info("Couldn't edit message. Not found.");
            }
        }
        return result;
    }

    @Override
    public List<Future<MessageResult>> sendMessagesToThisChannel(List<MessageContainer> msgFromBot) {
        List<Future<MessageResult>> result = new ArrayList<>();
        for (MessageContainer m : msgFromBot) {
            if (m == null) continue;
            if (m.hasDocument()) {
                result.add(pool.submit(() -> {
                    try {
                        MessageReturned messageReturned = messageHandler.sendDocumentTo(getMessageReceiverForSending(), m.getDocument());
                        Message message = (Message) messageReturned.getMessage();
                        addOwnMessage(message);
                        return new MessageResult(true, m.getFromID(), message.getID());
                    } catch (Exception e) {
                        return new MessageResult(false, m.getFromID());
                    }
                }));

            }
            if (m.hasTextMessage()) {
                result.add(pool.submit(() -> {
                    try {
                        MessageReturned messageReturned = messageHandler.sendTextMessageTo(getMessageReceiverForSending(), m.getTextMessage());

                        Message message = messageReturned.getMessage();
                        addOwnMessage(message);
                        return new MessageResult(true, m.getFromID(), message.getID());
                    } catch (Exception e) {
                        return new MessageResult(false, m.getFromID());
                    }
                }));
            }
            if (m.hasCommand()) {
                handleCommand(m.getCommand());
            }
        }
        return result;
    }

    public void addEditedMessageFrom(Message message, String oldContent) {
        sendEditedMessagesTo(messageHandler.handleEditMessageFrom(message, oldContent));
    }

    public void addMessageFrom(Message message) {
        List<MessageContainer> result = new ArrayList<>();

        Collection<Attachment> attachments = message.getAttachments();
        if (!attachments.isEmpty()) {
            result.addAll(messageHandler.handleAttachmentFrom(message.getID(), attachments, message.getAuthor()));
        }

        if (message.hasTextContent()) {
            result.add(messageHandler.handleTextMessageFrom(message));
        }
        sendMessageTo(result);
    }

    abstract protected MessageSender getMessageReceiverForSending();

    abstract protected void handleCommand(Command command);
}
