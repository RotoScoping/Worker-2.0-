package org.example.client;

import org.example.gui.event.Event;
import org.example.gui.event.EventType;
import org.example.logger.AsyncLogger;
import org.example.model.Fragment;
import org.example.model.Message;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Client extends JFrame{

    private DatagramChannel channel;
    private final ScriptExecutor script;

    private static final AsyncLogger logger = AsyncLogger.get("client");
    private Map<Integer, byte[]> fragments = new TreeMap<>();
    private InetSocketAddress serverAddress;
    private Configuration configuration;
    private UUID token;




    public Client(Configuration configuration) {
        this.configuration = configuration;
        this.script = new ScriptExecutor();
    }


    public void run() {
        configuration.tryToConnect();
        serverAddress = configuration.getSocketAddress();
        logger.log(Level.INFO, String.format("Клиент подключился к серверу %s:%d", serverAddress.getAddress(), serverAddress.getPort()));
        try {
            channel = DatagramChannel.open();
            channel.bind(null);
            InetSocketAddress localAddress = (InetSocketAddress) channel.getLocalAddress();
            logger.log(Level.INFO, String.format("Клиент открыл сокет %s:%d", localAddress.getAddress()
                    .toString(), localAddress.getPort()));
            channel.configureBlocking(false);



/*
            while (true) {
                String[] commandAndArgs = ConsoleHelper.getCommandAndArgs();
                if (!validateCommand(commandAndArgs)) continue;
                if (commandAndArgs[0].equals("exit")) break;
                sendPacket(commandAndArgs);
            }
*/

          /*  channel.disconnect();*/

        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Ошибка при работе с вводом команд: %s", e.getMessage()));
        }

        logger.log(Level.INFO, "Остановка приложения");
        logger.shutdown();
    }

    public Message sendPacket(Event event) {
        try {
            if (event.getType().equals(EventType.EXECUTE_SCRIPT)) {
                String result = script.execute(this, event);
                logger.log(Level.INFO, result);
                return new Message(result);
            }
            Payload payload = new Payload(token, event);

            if (payload.isEmptyPayload()) {
                logger.log(Level.INFO, String.format("Команда %s не найдена", event.getType().name()));
                System.out.printf("Команда %s не найдена\n", event.getType().name());
                return new Message("Команда не найдена");
            }

            logger.log(Level.INFO, String.format("Клиент отправил команду %s на сервер %s:%d", event.getType().name(), serverAddress.getAddress()
                    .toString(), serverAddress.getPort()));
            ByteBuffer buffer = ByteBuffer.wrap(payload.getData());
            // buffer.clear();
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            if (!sendPacketWithRetries(selector, buffer, event.getType().name())) {
                return new Message("Сервер недосутпен");
            }
            buffer.clear();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    while (channel.receive(buffer) != null) {
                        buffer.flip();
                        Fragment fragment = deserializeFragment(buffer.array());
                        int fragmentId = fragment.getId();
                        byte[] fragmentData = fragment.getData();
                        fragments.put(fragmentId, fragmentData);
                        if (fragments.size() == fragment.getTotalPackages()) {
                            Message message = assemblePackage();
                            UUID receivedToken = message.getToken();
                            if (receivedToken != null) {
                                token = receivedToken;
                            }
                            fragments.clear();
                            return message;
                        }
                        buffer.clear();
                    }
                }
                iterator.remove();

            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Ошибка при отправке/получении пакета %s", e.getMessage()));
        }
        return new Message("Ошибка сервера");
    }

    private Message assemblePackage() {

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            fragments.values()
                    .forEach(bytes -> {
                        try {
                            bos.write((bytes));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            byte[] fullMessage = bos.toByteArray();
            return deserializeMessage(fullMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Message deserializeMessage(byte[] bytes) {
        try (var bis = new ByteArrayInputStream(bytes);
             var ois = new ObjectInputStream(bis)) {
            Message msg = (Message) ois.readObject();
            logger.log(Level.INFO, String.format("Получен объект: %s", msg));
            return msg;
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING, String.format("Ошибка во время десериализации ответа сервера: %s", e.getMessage()));
        }

        return new Message("Ошбика при чтении ответа сервера!");
    }

    private boolean sendPacketWithRetries(Selector selector, ByteBuffer buffer, String command) throws IOException {

        int attempts = 0;

        logger.log(Level.INFO, String.format("Клиент отправил команду %s на сервер %s:%d",
                command, serverAddress.getAddress()
                        .toString(), serverAddress.getPort()));

        channel.send(buffer, serverAddress);
        while (selector.select(10000) == 0 && attempts++ < 4) {
            System.out.printf("Сервер %s:%d недоступен %n", serverAddress.getHostName(), serverAddress.getPort());
            System.out.println("Пытаемся повторно отправить пакет ... ");
            logger.log(Level.WARNING, String.format("Сервер %s:%d недоступен", serverAddress.getHostName(), serverAddress.getPort()));
            buffer.rewind();
            channel.send(buffer, serverAddress);
        }

        if (attempts >= 4) {
            System.out.printf("Не удалось отправить команду на сервер %s:%d%n", serverAddress.getHostName(), serverAddress.getPort());
            logger.log(Level.WARNING, String.format("Не удалось отправить команду на сервер %s:%d", serverAddress.getHostName(), serverAddress.getPort()));
            return false;
        }

        return true;
    }



    private Fragment deserializeFragment(byte[] bytes) {
        try (var bis = new ByteArrayInputStream(bytes);
             var ois = new ObjectInputStream(bis)) {
            Fragment msg = (Fragment) ois.readObject();
            return msg;
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.WARNING, String.format("Ошибка во время десериализации ответа сервера: %s", e.getMessage()));
        }

        return null;
    }


    private boolean validateCommand(String[] commandAndArgs) {
        if (commandAndArgs == null || commandAndArgs.length == 0) {
            logger.log(Level.WARNING, "Команда не указана.");
            return false;
        }
        return true;
    }

    public UUID getToken() {
        return token;
    }
}
