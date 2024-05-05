package org.example;

import java.util.Scanner;

public class StopController implements Runnable {


    private Server server;

    public StopController(Server server) {
        this.server = server;
    }


    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                try {
                    if (scanner.hasNextLine()) {
                        String input = scanner.nextLine();
                        if ("exit".equalsIgnoreCase(input)) {
                            server.stop();
                            break;
                        }
                    } else {
                        System.out.println("ctrl+d");
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Программа прервана.");
                    break;
                }

            }
            server.stop();
        }

    }
}
