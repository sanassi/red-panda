package com.example.demo.guiutils;

import java.util.logging.Logger;

public class ColorLogger {
    static final Logger LOGGER = java.util.logging.Logger.getLogger(ColorLogger.class.getName());
    private ColorLogger() {}

    public static Logger INSTANCE() {
        return LOGGER;
    }

    public static void logDebug(String logging) {
        LOGGER.info("\u001B[34m" + logging + "\u001B[0m");
    }
    public static void logInfo(String logging) {
        LOGGER.info("\u001B[32m" + logging + "\u001B[0m");
    }

    public static void logError(String logging) {
        LOGGER.info("\u001B[31m" + logging + "\u001B[0m");
    }
}
