package core.logger;

import java.util.logging.Level;

/**
 * Класс-модель сообщения для логгера
 */
public class LogRecord {
    private final Level level;
    private final String message;

    public LogRecord(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}