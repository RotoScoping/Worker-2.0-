package org.example;

public class Port {

    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;

    public static int tryPort(String port) {
        try {
            int numericPort = Integer.parseInt(port);
            return numericPort >= MIN_PORT && numericPort <= MAX_PORT ? numericPort : 0;
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    public static int tryPort(int port) {
        return port >= MIN_PORT && port <= MAX_PORT ? port : 0;
    }
}
