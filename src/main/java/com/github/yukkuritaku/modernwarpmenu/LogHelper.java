package com.github.yukkuritaku.modernwarpmenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {

    public static void logDebug(String message, Throwable throwable){

        logDebug(message, throwable, new Object[0]);
    }
    public static void logDebug(String message, Object... params){
        logDebug(message, null, params);
    }

    public static void logDebug(String message, Throwable throwable, Object... params){
        //TODO replace with StackWalker, in java 9 or higher, can be replaceable
        String callingClassName = new Throwable().getStackTrace()[3].getClassName();
        Logger logger = LogManager.getLogger(callingClassName);

    }
}
