package org.example.logger;

import java.util.logging.Level;

/**
 * Класс-модель сообщения для логгера
 */
public class LogRecord {
    private final Level level;
    private final String message;

    /**
     * Instantiates a new Log record.
     *
     * @param level   the level
     * @param message the message
     */
    public LogRecord(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}