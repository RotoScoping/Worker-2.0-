package core.logger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.*;


/**
 * Асинхронный логгер на неблокирующей очереди
 */
public class AsyncLogger {

    private final BlockingQueue<LogRecord> queue;
    private static final AsyncLogger INSTANCE = new AsyncLogger();
    private final Logger logger;
    private final Thread worker;
    private volatile boolean shutdownRequested = false;


    /**
     * Настройка логгера и запуск потока
     */
    public AsyncLogger() {
        queue = new ArrayBlockingQueue<>(228);
        logger = Logger.getLogger(AsyncLogger.class.getName());
        try {
            Handler fileHandler = new FileHandler("mylog.log", true);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            System.err.println("Ошибка при настройке FileHandler для логгера: " + e.getMessage());
        }
        worker = new Thread(this::processQueue);
        worker.start();
    }

    /**
     * Процесс проверки очереди и обработка логов
     */
    private void processQueue() {
        try {
            while (!shutdownRequested || !queue.isEmpty()) {
                LogRecord record = queue.poll(); // Non-blocking
                if (record != null) {
                    logger.log(record.getLevel(), record.getMessage());
                } else {
                    Thread.sleep(10); // Reduce CPU usage if queue is empty
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt(); // Restore interrupted status
        }
    }

    /**
     * Внешний метод для логирования
     */
    public void log(Level level, String message) {
        if (!shutdownRequested) {
            queue.add(new LogRecord(level, message)); // Non-blocking
        }
    }

    /**
     * Отключение
     */
    public void shutdown() {
        shutdownRequested = true;
    }



    public static AsyncLogger get() {
        return INSTANCE;
    }


}