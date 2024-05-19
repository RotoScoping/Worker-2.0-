package org.example.logger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.*;
import java.util.logging.LogRecord;


/**
 * Асинхронный логгер на неблокирующей очереди
 */
public class AsyncLogger {

    private final BlockingQueue<java.util.logging.LogRecord> queue;
    private final Logger logger;
    private final Thread worker;
    private volatile boolean shutdownRequested = false;
    private static final ConcurrentHashMap<String, AsyncLogger> loggers = new ConcurrentHashMap<>();

    private static final String DEFAULT_PATH = "/var/log/";
    private static final String DEFAULT_NAME = "logger";
    private static final int DEFAULT_LOG_SIZE = 10 * 1024 * 1024;
    private static final int DEFAULT_LOG_COUNT = 5;


    /**
     * Настройка логгера и запуск потока
     */
    private AsyncLogger(String name, String path, int logSize, int logCount) {

        queue = new ArrayBlockingQueue<>(228);
        logger = Logger.getLogger(name);
        try {
            Handler fileHandler = new FileHandler(path, logSize, logCount, true);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            fileHandler.setFormatter(new GenericFormatter());
        } catch (IOException e) {
            System.err.println("Ошибка при настройке FileHandler для логгера: " + e.getMessage());
        }
        worker = new Thread(this::processQueue);
        worker.start();
    }


    public void setHandler(Handler handler) {
        handler.setFormatter(new GenericFormatter());
        logger.addHandler(handler);

    }

    /**
     * Процесс проверки очереди и обработка логов
     */
    private void processQueue() {
        try {
            while (!shutdownRequested || !queue.isEmpty()) {
                java.util.logging.LogRecord record = queue.poll();
                if (record != null) {
                    logger.log(record.getLevel(), record.getMessage());
                } else {
                    Thread.sleep(10);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
        }
    }

    /**
     * Внешний метод для логирования
     *
     * @param level   the level
     * @param message the message
     */
    public void log(Level level, String message) {
        if (!shutdownRequested) {
            queue.add(new LogRecord(level, message));
        }
    }

    /**
     * Отключение
     */
    public void shutdown() {
        shutdownRequested = true;
    }


    /**
     * Get async logger.
     *
     * @return the async logger
     */
    public static AsyncLogger registerLogger(String name, String path) {
        return registerLogger(name, path, DEFAULT_LOG_SIZE, DEFAULT_LOG_COUNT);
    }

    public static AsyncLogger registerLogger(String name, String path, int logSize, int logCount) {
        String effectiveName = (name != null) ? name : DEFAULT_NAME;
        return loggers.computeIfAbsent(effectiveName,
                k -> new AsyncLogger(effectiveName, (path != null) ? path : String.format("%s%s.log", DEFAULT_PATH, effectiveName),
                        logSize, logCount));
    }

    public static AsyncLogger get(String name) {
        return loggers.get(name);
    }


}