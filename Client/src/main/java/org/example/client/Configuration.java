package org.example.client;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Configuration {

    private int serverPort;
    private String serverAddress;

    public Configuration(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public Configuration() {
        this("127.0.0.1", 35000);
    }

    public boolean tryToConnect() {
        try (var socket = new DatagramSocket()) {
            socket.setSoTimeout(5000); // Установка таймаута ожидания ответа
            while (true) {
                System.out.printf("Пытаемся подлкючиться к %s:%d ... %n", serverAddress, serverPort);
                byte[] sendData = {0};
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverAddress), serverPort);
                socket.send(sendPacket);
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket); // Попытка получить ответ
                System.out.println("Сервер доступен для получения команд!");
                return true;
            }


        } catch (SocketTimeoutException e) {
            System.out.println("Сервер не ответил в течение заданного времени.");
            changeCredentials();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    private void changeCredentials() {
        Scanner s = new Scanner(System.in);
        System.out.print("Хотите поменять адрес и порт сервера ? (y/n): ");
        if (!getUserResponse(s)) return ;
        setHost(s);
        setPort(s);
        tryToConnect();
        Class<? extends Configuration> aClass = this.getClass();
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(serverAddress, serverPort);
    }

    public void setHost(Scanner s) {
        System.out.print("Введите адрес сервера (по умолчанию " + serverAddress +  " ): ");
        String addr = s.nextLine()
                .trim();
        if (!addr.isEmpty()) serverAddress = addr;

    }



    public void setPort(Scanner s) {
        System.out.print("Введите порт, сейчас используется " + serverPort + ": ");
        String input = s.nextLine().trim(); // Считываем строку ввода
        if (input.isEmpty()) {
            System.out.println("Используется порт по умолчанию: " + serverPort);
            return; // Завершаем метод, используя порт по умолчанию
        }

        try {
            int port = Integer.parseInt(input); // Пытаемся преобразовать строку в число
            if (port > 1023 && port < 65536) {
                 serverPort = port; // Устанавливаем новое значение порта, если оно в допустимом диапазоне
            } else {
                System.out.println("Введённое значение порта вне допустимого диапазона (1024-65535). Используется порт по умолчанию.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Введённые данные не являются целым числом. Используется порт по умолчанию.");
        }
    }

    public boolean getUserResponse(Scanner scanner) {
        String input = scanner.nextLine().trim().toLowerCase();
        while (!input.equals("y") && !input.equals("n")) {
            System.out.print("Неверный ввод. Пожалуйста, введите 'y' для да или 'n' для нет: ");
            input = scanner.nextLine().trim().toLowerCase();
        }
        return input.equals("y");
    }


}
