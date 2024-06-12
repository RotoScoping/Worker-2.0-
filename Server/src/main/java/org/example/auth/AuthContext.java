package org.example.auth;

import org.example.logger.AsyncLogger;
import org.example.model.Form;
import org.example.model.Message;
import org.example.model.User;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AuthContext {


    private static final AsyncLogger logger = AsyncLogger.get("server");

    private static final AuthContext INSTANCE = new AuthContext();
    private final ConcurrentHashMap<UUID, String> sessions = new ConcurrentHashMap<>();
    private final AuthDao userDao ;

    private AuthContext (){
        userDao = AuthDao.getInstance();
    }

    public boolean isUserAuth(ByteBuffer buffer) {
        buffer.rewind();
        int commandCode = buffer.get();
        System.out.println("12");
        if (commandCode == 101 || commandCode == 111 || commandCode == 0) {
            return true;
        }
        byte[] tokenBytes = new byte[36];
        buffer.get(tokenBytes);
        if (zeroToken(tokenBytes)) return false;
        String token = new String(tokenBytes);
        System.out.println("14");
        return userDao.isSessionExists(UUID.fromString(token));
    }


    public Message signUp(Form form) {
        if (userAlreadyExists(form.login())) {
            return new Message("Такой пользователь уже зареган");
        }
        userDao.addUser(new User(form.password(), form.login()));
        UUID uuid = UUID.randomUUID();
        userDao.addSession(uuid, form.login());
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

        User user = userDao.getUserByUsername(form.login());

        if (!user.getPassword()
                .equals(userDao.hashPassword(form.password()))) {
            return message.setMessage("Неверный пароль.");
        }
        String token = userDao.getSessionByLogin(form.login()).orElse(null);
        if (token == null) {
            UUID newToken = UUID.randomUUID();
            userDao.addSession(newToken, form.login());
            token = newToken.toString();
            message.setToken(newToken);
        }
        message.setToken(UUID.fromString(token));
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
        return userDao.getUserByUsername(login) != null;
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


    public User getUserBySessionToken(String token) {
        return userDao.getUserBySessionToken(token);
    }


}

