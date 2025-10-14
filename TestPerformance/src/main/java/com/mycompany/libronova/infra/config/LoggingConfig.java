package com.mycompany.libronova.infra.config;

import java.io.IOException;
import java.util.logging.*;

/**
 * Configuration class for application logging.
 * Sets up file logging to app.log with proper formatting.
 * 
 * @author Wilffren Muñoz
 */
public class LoggingConfig {
    
    private static final String LOG_FILE = "app.log";
    private static Logger rootLogger;
    
    /**
     * Initializes the logging configuration.
     * Creates a file handler that writes to app.log with custom formatting.
     */
    public static void initialize() {
        try {
            // Get the root logger
            rootLogger = Logger.getLogger("");
            
            // Remove default console handler
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            
            // Create file handler
            FileHandler fileHandler = new FileHandler(LOG_FILE, true); // append mode
            
            // Create custom formatter
            fileHandler.setFormatter(new CustomLogFormatter());
            
            // Add file handler to root logger
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
            
            // Also add console handler for development
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomLogFormatter());
            consoleHandler.setLevel(Level.WARNING);
            rootLogger.addHandler(consoleHandler);
            
            // Log initialization success
            Logger.getLogger(LoggingConfig.class.getName()).info("Logging system initialized successfully");
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logging: " + e.getMessage());
        }
    }
    
    /**
     * Gets a logger for the specified class.
     * 
     * @param clazz the class for which to get a logger
     * @return configured logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
    
    /**
     * Logs an info message.
     * 
     * @param className the name of the class logging the message
     * @param message the message to log
     */
    public static void logInfo(String className, String message) {
        Logger.getLogger(className).info(message);
    }
    
    /**
     * Logs an error message.
     * 
     * @param className the name of the class logging the message
     * @param message the error message to log
     * @param throwable the exception (optional)
     */
    public static void logError(String className, String message, Throwable throwable) {
        Logger logger = Logger.getLogger(className);
        if (throwable != null) {
            logger.log(Level.SEVERE, message, throwable);
        } else {
            logger.severe(message);
        }
    }
    
    /**
     * Logs a warning message.
     * 
     * @param className the name of the class logging the message
     * @param message the warning message to log
     */
    public static void logWarning(String className, String message) {
        Logger.getLogger(className).warning(message);
    }
    
    /**
     * Custom log formatter for consistent log format.
     */
    private static class CustomLogFormatter extends Formatter {
        
        @Override
        public String format(LogRecord record) {
            return String.format("[%1$tF %1$tT] [%2$s] [%3$s] %4$s%n",
                    record.getMillis(),
                    record.getLevel(),
                    record.getLoggerName(),
                    record.getMessage()
            );
        }
    }
}