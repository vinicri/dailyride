package com.dingoapp.dingo.util;

/**
 * Created by guestguest on 13/01/16.
 */
public class LogUtil {
    private static final String LOG_PREFIX = "dingo_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;


    public static String makeLogTag(String str){
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }
}
