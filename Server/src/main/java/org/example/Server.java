package org.example;

import org.example.command.ICommand;
import org.example.command.impl.*;
import org.example.logger.AsyncLogger;
import org.example.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Server {

    private static final AsyncLogger logger = AsyncLogger.get("server");
    public static final String SERVER_PORT = System.getenv("WORKER_SERVER_PORT");
    private volatile boolean isStopped;
    private final ICommand saver;
    private static final int BUFFER_SIZE = 32768;
    private static final Map<Integer, ICommand> commandMap;


    static {
        commandMap = new HashMap<>();
        commandMap.put(10, new HelpCommand());
        commandMap.put(20, new InfoCommand());
        commandMap.put(30, new ShowCommand());
        commandMap.put(11, new AddCommand());
        commandMap.put(21, new UpdateCommand());
        commandMap.put(32, new RemoveByIdCommand());
        commandMap.put(40, new ClearCommand());
        commandMap.put(60, new RemoveFirstCommand());
        commandMap.put(41, new AddIfMaxCommand());
        commandMap.put(51, new AddIfMinCommand());
        commandMap.put(70, new AvarageOfSalaryCommand());
        commandMap.put(80, new PrintFieldAscendingStatus());
        commandMap.put(90, new PrintFieldDescendingOrganization());
    }


    public Server() {
        this.isStopped = false;
        this.saver = new SaveCommand();
        new Thread(new StopController(this)).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                saver.execute(ByteBuffer.allocate(0))));
    }


    public void run() {
        try (var channel = DatagramChannel.open()) {
            setupServer(channel);
            processRequests(channel);
            logger.log(Level.INFO, "Сервер остановлен.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода-вывода: " + e.getMessage());
        }
    }

    private void setupServer(DatagramChannel channel ) throws IOException{
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(Port.tryPort(SERVER_PORT)));
        var localAddress = (InetSocketAddress) channel.getLocalAddress();
        logger.log(Level.INFO, String.format("Сервер запустился на порту %d", localAddress.getPort()));
    }


    private void processRequests(DatagramChannel channel ) throws IOException {
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_READ);
            logger.log(Level.INFO, "Сервер ожидает подключений...");
            while (!isStopped) {
                selector.select();
                logger.log(Level.INFO, "На сервер подключились");
                if (isStopped) break;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    logger.log(Level.INFO, "Получен selectionKey");
                    SelectionKey key = iter.next();
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);
                        logger.log(Level.INFO, String.format("Клиент %s:%d прислал команду", clientAddress.getAddress(), clientAddress.getPort()));
                        buffer.flip();
                        ICommand iCommand = commandMap.getOrDefault((int) buffer.get(), new UnknownCommand());
                        Message msg = iCommand.execute(buffer);
                        ByteBuffer responseBuffer = ByteBuffer.wrap(serialMessage(msg));
                        channel.send(responseBuffer, clientAddress);
                        logger.log(Level.INFO, "Отправлен ответ клиенту " + clientAddress);
                        buffer.clear();
                    }
                    iter.remove();
                }
            }
        }

    }

    private byte[] serialMessage(Message msg) {
        byte[] serMess = new byte[0];
        try (var bis = new ByteArrayOutputStream();
             var ois = new ObjectOutputStream(bis);) {
            ois.writeObject(msg);
            serMess = bis.toByteArray();

        } catch (IOException e) {
            logger.log(Level.INFO, "Ошибка при сериализации");
        }
        return serMess;

    }

    public void stop() {
        saver.execute(ByteBuffer.allocate(0));
        isStopped = true;
    }


}
