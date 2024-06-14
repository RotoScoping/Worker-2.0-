package org.example;

import org.example.auth.AuthContext;
import org.example.command.CommandResolver;
import org.example.command.ICommand;
import org.example.command.impl.*;
import org.example.logger.AsyncLogger;
import org.example.model.Fragment;
import org.example.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;


public class Server {

    private static final AsyncLogger logger = AsyncLogger.get("server");
    public static final String SERVER_PORT = System.getenv("WORKER_SERVER_PORT");
    private volatile boolean isStopped;
    private final ICommand saver;
    private static final int BUFFER_SIZE = 1400;
    private static final Map<Integer, ICommand> commandMap;



    private final ForkJoinPool requestReadingPool = new ForkJoinPool();
    private final ForkJoinPool requestProcessingPool = new ForkJoinPool();
    private final ExecutorService responseSendingPool = Executors.newFixedThreadPool(10);
    private final AuthContext auth;


    private static final int MTU = 1400;
    private static final int HEADER_SIZE = 4;
    private static final int PAYLOAD_SIZE = MTU - HEADER_SIZE;


    private final Set<InetSocketAddress> users;

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
        commandMap.put(101, new SignUpCommand());
        commandMap.put(111, new SignInCommand());
    }


    public Server() {
        this.users = new HashSet<>();
        this.auth = AuthContext.get();
        this.isStopped = false;
        this.saver = new SaveCommand();
        new Thread(new StopController(this)).start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            responseSendingPool.shutdown();
            requestProcessingPool.shutdown();
            requestReadingPool.shutdown();
        }));
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
        try {
            channel.bind(new InetSocketAddress(Port.tryPort(SERVER_PORT)));
        } catch (AlreadyBoundException e) {
            logger.log(Level.INFO, "Порт " + SERVER_PORT + " занят");
            channel.bind(new InetSocketAddress(0));
        }
        var localAddress = (InetSocketAddress) channel.getLocalAddress();
        logger.log(Level.INFO, String.format("Сервер запустился на порту %d", localAddress.getPort()));
    }


    private void processRequests(DatagramChannel channel ) throws IOException {
        try (Selector selector = Selector.open()) {
            channel.register(selector, SelectionKey.OP_READ);
            logger.log(Level.INFO, "Сервер ожидает подключений...");
            while (!isStopped) {
                selector.select();
                if (isStopped) break;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    if (key.isReadable()) {
                        requestReadingPool.submit(() -> {
                            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                            InetSocketAddress clientAddress;
                            try {
                                clientAddress = (InetSocketAddress) channel.receive(buffer);
                                if (clientAddress != null) {
                                    handleRequest(channel, buffer, clientAddress);
                                }
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "Ошибка при чтении запроса: " + e.getMessage());
                            }
                        });
                    }
                    iter.remove();
                }
            }
        }

    }

    private void handleRequest(DatagramChannel channel, ByteBuffer buffer, InetSocketAddress clientAddress) {
        requestProcessingPool.submit(() -> {
            if (users.add(clientAddress)) {
                logger.log(Level.INFO, String.format("Клиент %s:%d подключился! Всего подключившихся %d", clientAddress.getAddress(), clientAddress.getPort(), users.size()));
            }
            buffer.flip();
            logger.log(Level.INFO, String.format("Сервер получил %d байт от %s:%d", buffer.remaining(), clientAddress.getAddress(), clientAddress.getPort()));
            Message msg;
            if (!auth.isUserAuth(buffer)) {
                msg = new Message().setMessage("Войдите в аккаунт или зарегистрируйтесь");
            } else {
                buffer.rewind();
                ICommand command = CommandResolver.get(buffer.get());
                logger.log(Level.INFO, String.format("Клиент %s:%d использует команду %s.", clientAddress.getAddress(), clientAddress.getPort(), command.getClass().getSimpleName()));
                msg = command.execute(buffer);
            }
            byte[] data = serialMessage(msg);
            sendResponse(channel, data, clientAddress, msg);
            buffer.clear();
        });
    }

    private void sendResponse(DatagramChannel channel, byte[] data, InetSocketAddress clientAddress, Message msg) {
        responseSendingPool.submit(() -> {
            int totalPackets = (int) Math.ceil((double) data.length / PAYLOAD_SIZE);
            for (int i = 0; i < totalPackets; i++) {
                int start = i * PAYLOAD_SIZE;
                int end = Math.min(data.length, start + PAYLOAD_SIZE);
                byte[] fragment = Arrays.copyOfRange(data, start, end);
                Fragment packet = new Fragment(i, totalPackets, fragment);
                byte[] serializedFragment = serialFragment(packet);
                ByteBuffer part = ByteBuffer.wrap(serializedFragment);

                try {
                    channel.send(part, clientAddress);
                    logger.log(Level.INFO, String.format("Сервер отправил пакет №%d размером %d байт для %s:%d", i + 1, fragment.length, clientAddress.getAddress(), clientAddress.getPort()));
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Ошибка при отправке ответа: " + e.getMessage());
                }
            }
        });
    }


    private byte[] serialMessage(Message msg) {
        byte[] serMess = new byte[0];
        try (var bis = new ByteArrayOutputStream();
             var ois = new ObjectOutputStream(bis)) {
            ois.writeObject(msg);
            serMess = bis.toByteArray();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при сериализации сообщения: " + e.getMessage());
            System.out.println(e.getMessage());
        }
        return serMess;

    }

    private byte[] serialFragment(Fragment msg) {
        byte[] serFrag = new byte[0];
        try (var bis = new ByteArrayOutputStream();
             var ois = new ObjectOutputStream(bis)) {
            ois.writeObject(msg);
            serFrag = bis.toByteArray();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при сериализации сообщения: " + e.getMessage());
            System.out.println(e.getMessage());
        }
        return serFrag;

    }

    public void stop() {
        responseSendingPool.shutdown();
        requestProcessingPool.shutdown();
        requestReadingPool.shutdown();
        saver.execute(ByteBuffer.allocate(0));
        isStopped = true;
    }


}
