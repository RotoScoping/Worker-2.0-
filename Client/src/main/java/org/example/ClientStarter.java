package org.example;


import org.example.client.Client;
import org.example.client.Configuration;
import org.example.gui.BaseWindow;
import org.example.logger.AsyncLogger;

import javax.swing.*;
import java.util.logging.Level;

public class ClientStarter {
    private static final String LOG_FILE_PATH = System.getenv("WORKER_CLIENT_LOG_FILE_PATH");
    public static void main(String[] args) {
        AsyncLogger logger = AsyncLogger.registerLogger("client", LOG_FILE_PATH);
        logger.log(Level.INFO, "Начало работы клиента");
        Client client = new Client(new Configuration());
        client.run();
        SwingUtilities.invokeLater(() -> {
            BaseWindow clientGUI = new BaseWindow(client);
            clientGUI.setVisible(true);
        });
    }








}