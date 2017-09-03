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

package de.mas.telegramircbot.message;

import de.mas.telegramircbot.telegram.common.CommandsDefs;

public class MessageStrings {
    public static final String PEOPLE_IN_CHANNEL = "In %s are the following people: ";  // 1 param  (%s) channel name
    public static final String BOT_ONLINE = "Bot started: %s";                          // 1 param  (%s) channel name
    public static final String TEXT_MESSAGE_FROM_USER = "<%s> %s";                      // 2 params (%s,%s) user name,message
    public static final String TEXT_MESSAGE = "%s";                                     // 1 param  (%s) message
    public static final String JOIN_MESSAGE = "%s has joined";                          // 1 param  (%s) user name
    public static final String LEAVE_MESSAGE = "%s has left";                           // 1 param  (%s) user name
    public static final String USER_NOT_FOUND_MESSAGE = "User not found: %s";           // 1 param  (%s) user name
    public static final String BOT_PRIVATE = "Sorry, this bot is private =(\n You need to set the chatID to %d (your telegram chat id).";     // 1 param  (%d) chat id
    public static final String SYSTEM_MESSAGE = "SYSTEM: %s";                           // 1 param  (%s) message
    public static final String PRIVATE_MESSAGE_BOT_STARTED = "No username set. Please set a username which will receive the private messages with \"" + CommandsDefs.SET_USER_HELP+ "\"";
    public static final String NO_USERNAME_SET = "No username set.";
    public static final String EDITED_MESSAGE_FROM_USER = "*<%s> %s";                   // 2 params (%s,%s) user name,message
}
