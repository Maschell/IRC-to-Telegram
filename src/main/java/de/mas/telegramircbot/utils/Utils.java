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

package de.mas.telegramircbot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String replacesSmileys(String s) {
        s = s.replaceAll("\\ud83d\\ude06", "xD"); // 😆
        s = s.replaceAll("\\ud83d\\ude4a", ":X"); // 🙊
        s = s.replaceAll("\u2764", "<3");         // ❤️
        return s;
    }

    // To get rid of the try catch thing..
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // And ignore the exception. ups.
        }
    }

    public static boolean isNumeric(String string) {
        boolean isValue = false;
        try {
            Integer.parseInt(string);
            isValue = true;
        } catch (NumberFormatException e) {
        }
        return isValue;
    }

    public static String escapeUsername(String username) {
        return username.replace("@", "").replace("+", "");
    }

    /**
     * 
     * @param input
     * @param replace
     * @param replaceWith
     * @return
     */
    public static String replaceStringInStringEscaped(String input, String replace, String replaceWith) {
        return input.replaceAll(Pattern.quote(replace), Matcher.quoteReplacement(replaceWith));
    }
}
