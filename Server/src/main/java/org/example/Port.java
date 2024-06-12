package org.example;

import org.example.logger.AsyncLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Port {

    private static final AsyncLogger logger = AsyncLogger.get("server");
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    public static int tryPort(String port) {
        try {
            int numericPort = Integer.parseInt(port);
            int candidatePort = numericPort >= MIN_PORT && numericPort <= MAX_PORT ? numericPort : 0;
            try (ServerSocket serverSocket = new ServerSocket(candidatePort)) {
                return candidatePort;
            } catch (IOException e) {
                return 0;
            }
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    public static int tryPort(int port) {
        return port >= MIN_PORT && port <= MAX_PORT ? port : 0;
    }
}
