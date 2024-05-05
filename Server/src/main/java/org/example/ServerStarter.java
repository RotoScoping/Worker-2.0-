package org.example;
import org.example.logger.AsyncLogger;
import java.util.logging.Level;
public class ServerStarter {

    private static final String LOG_FILE_PATH = System.getenv("WORKER_SERVER_LOG_FILE_PATH");

    public static void main(String[] args) {
        AsyncLogger logger = AsyncLogger.registerLogger("server", LOG_FILE_PATH);
        logger.log(Level.INFO, "Начало работы программы сервера");
        Server server = new Server();
        server.run();
    }
}