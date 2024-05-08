package org.example;

import org.example.logger.AsyncLogger;
import org.example.model.Form;
import org.example.model.Message;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AuthContext {


    private static final AsyncLogger logger = AsyncLogger.get("server");

    private static final AuthContext INSTANCE = new AuthContext();
    private final ConcurrentHashMap<UUID, String> sessions = new ConcurrentHashMap<>();


    private final ConcurrentHashMap<String, String> userMap = new ConcurrentHashMap<>();

    private AuthContext (){
        userMap.put("mark", "2222");
    }

    public boolean isUserAuth(ByteBuffer buffer) {
        buffer.rewind();
        int commandCode = buffer.get();
        if (commandCode == 101 || commandCode == 111 || commandCode == 0) {
            return true;
        }
        byte[] tokenBytes = new byte[36];
        buffer.get(tokenBytes);
        if (zeroToken(tokenBytes)) return false;
        String token = new String(tokenBytes);
        return sessions.containsKey(UUID.fromString(token));
    }


    public Message signUp(Form form) {
        if (userAlreadyExists(form.login())) {
            return new Message("Такой пользователь уже зареган");
        }
        userMap.put(form.login(), form.password());
        UUID uuid = UUID.randomUUID();
        sessions.put(uuid, form.login());
        logger.log(Level.INFO, String.format("Пользователь %s успешно аутентифицировался!", form.login()));
        logger.log(Level.INFO, String.format("Всего активных пользователей на сервере - %d" , sessions.size()));
        Message message = new Message("Вы успешно прошли регистрацию! Ваш токен " + uuid);
        message.setToken(uuid);
        return message;
    }


    public Message signIn(Form form) {
        Message message = new Message();
        if (!userAlreadyExists(form.login())) {
            return message.setMessage("Пользователь не найден.");
        }

        if (!userMap.get(form.login())
                .equals(form.password())) {
            return message.setMessage("Неверный пароль.");
        }
        UUID token = getTokenIfExist(form.login()).orElse(null);
        if (token == null) {
            token = UUID.randomUUID(); // Генерируем новый UUID для сессии
            sessions.put(token, form.login()); // Связываем UUID с логином в сессии
            message.setToken(token);
        }
        logger.log(Level.INFO, String.format("Пользователь %s успешно аутентифицировался!", form.login()));
        logger.log(Level.INFO, String.format("Всего активных пользователей на сервере - %d" , sessions.size()));
        return message.setMessage("Успешный вход в систему. Токен: " + token);
    }


    private Optional<UUID> getTokenIfExist(String login) {
        for (UUID key : sessions.keySet()) {
            if (sessions.get(key).equals(login)) {
                return Optional.of(key);
            }
        }
        return Optional.empty();
    }

    private boolean userAlreadyExists(String login) {
        return userMap.containsKey(login);
    }

    private boolean zeroToken(byte[] tokenBytes) {
        for (int value : tokenBytes) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }

    public static AuthContext get() {
        return INSTANCE;
    }


}

