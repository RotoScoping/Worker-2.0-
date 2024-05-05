package org.example;

import org.example.logger.AsyncLogger;
import org.example.model.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

public class Client  {

    private DatagramChannel channel;
    private final ScriptExecutor script;

    private static final AsyncLogger logger = AsyncLogger.get("client");

    private InetSocketAddress serverAddress;
    private Configuration configuration;

    public Client(Configuration configuration) {
        this.configuration =configuration;
        this.script = new ScriptExecutor();
    }

    public void run() {
        configuration.tryToConnect();
        serverAddress = configuration.getSocketAddress();

        logger.log(Level.INFO, "Начало работы клиента");
        try (
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            channel = DatagramChannel.open();
            channel.bind(null);
            InetSocketAddress localAddress = (InetSocketAddress) channel.getLocalAddress();
            logger.log(Level.INFO, String.format("Клиент открыл сокет %s:%d",localAddress.getAddress().toString(), localAddress.getPort() ));
            channel.configureBlocking(false);
            String[] commandAndArgs;
            while (true) {
                System.out.print("Введите команду: ");
                commandAndArgs = reader.readLine().split(" ");
                if (commandAndArgs[0].equals("exit")) break;
                sendPacket(commandAndArgs);
            }
            channel.disconnect();

        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Ошибка при работе с вводом команд: %s", e.getMessage()));
        }

        logger.log(Level.INFO, "Остановка приложения");
        logger.shutdown();
    }

    public void sendPacket(String[] commandAndArgs) {
        try  {
            if (commandAndArgs[0].equals("execute_script")) {
                    String result = script.execute(this, commandAndArgs);
                    logger.log(Level.INFO, result);
                    return;
            }
            Payload payload = new Payload(commandAndArgs[0], null);

            if (payload.isEmptyPayload()) {
                logger.log(Level.INFO, String.format("Команда %s не найдена", commandAndArgs[0]));
                System.out.printf("Команда %s не найдена\n", commandAndArgs[0]);
                return;
            }

            logger.log(Level.INFO, String.format("Клиент отправил команду %s на сервер %s:%d", commandAndArgs[0], serverAddress.getAddress().toString(), serverAddress.getPort()));
            ByteBuffer buffer = ByteBuffer.wrap(payload.getData());
            channel.send(buffer, serverAddress);
            buffer.clear();

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            if (selector.select(5000) > 0) { // timeout 5sec
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        channel.receive(buffer);
                        buffer.flip();
                        Message message = deserializeMessage(buffer.array());
                        System.out.println(message.getMessage());
                        buffer.clear();
                    }
                    iterator.remove();
                }
            } else {
                System.out.printf("Сервер %s:%d недоступен %n", serverAddress.getHostName(), serverAddress.getPort());
                logger.log(Level.WARNING, String.format("Сервер %s:%d ", serverAddress.getHostName(), serverAddress.getPort()));

            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Ошибка при отправке/получении пакета %s", e.getMessage()));
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


}
