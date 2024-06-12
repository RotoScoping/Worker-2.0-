package org.example.auth;

import org.example.logger.AsyncLogger;
import org.example.model.User;
import org.example.storage.pool.DataSourceFactory;

import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class AuthDao {

    private static final AsyncLogger logger = AsyncLogger.get("server");
    private static final AuthDao INSTANCE = new AuthDao();

    private final DataSource connections;

    private final static String EXISTS_SESSION_SQL =  "SELECT 1 FROM sessions WHERE token = ?";
    private final static String GET_ID_BY_USERNAME_SQL =  "SELECT id FROM users WHERE username = ?";
    private final static String INSERT_SESSION_SQL =  "INSERT INTO sessions (user_id, token) VALUES (?, ?)";
    private final static String GET_SESSION_BY_USER_ID_SQL =  "SELECT token FROM sessions WHERE user_id = ?";
    private final static String GET_USER_BY_LOGIN_SQL =  "SELECT id, username, password_hash FROM users WHERE username = ?";
    private final static String INSERT_USER_SQL =  "INSERT INTO users (username, password_hash) VALUES (?, ?)";
    private final static String GET_USER_BY_TOKEN =   "SELECT users.id as id, users.username as username, users.password_hash as password FROM users " +
                                                               "JOIN sessions ON users.id = sessions.user_id WHERE sessions.token = ?";

    public AuthDao() {
        connections = DataSourceFactory.getDataSource();
    }



    public boolean isSessionExists(UUID token) {
        try (Connection connection = connections.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXISTS_SESSION_SQL)) {
            preparedStatement.setString(1, token.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while checking if session exists", e);
        }
    }


    public void addSession(UUID token, String login) {
        try (Connection connection = connections.getConnection()) {
            connection.setAutoCommit(false);
            long userId;
            try (PreparedStatement selectStmt = connection.prepareStatement(GET_ID_BY_USERNAME_SQL)) {
                selectStmt.setString(1, login);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getLong("id");
                    } else {
                        connection.rollback();
                        throw new SQLException("User not found");
                    }
                }
            }
            try (PreparedStatement insertStmt = connection.prepareStatement(INSERT_SESSION_SQL)) {
                insertStmt.setLong(1, userId);
                insertStmt.setString(2, token.toString());
                insertStmt.executeUpdate();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding session", e);
        }

    }

    public Optional<String> getSessionByLogin(String login) {
        try (Connection connection = connections.getConnection()) {
            long userId;
            try (PreparedStatement selectUserStmt = connection.prepareStatement(GET_ID_BY_USERNAME_SQL)) {
                selectUserStmt.setString(1, login);
                try (ResultSet rs = selectUserStmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getLong("id");
                    } else {
                        return Optional.empty();
                    }
                }
            }
            try (PreparedStatement selectSessionStmt = connection.prepareStatement(GET_SESSION_BY_USER_ID_SQL)) {
                selectSessionStmt.setLong(1, userId);
                try (ResultSet rs = selectSessionStmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(rs.getString("token"));
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while getting session by login", e);
        }

    }

    public void addUser(User user) {
        try (Connection connection = connections.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashPassword(user.getPassword()));

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public User getUserByUsername(String username) {
        try (Connection connection = connections.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_LOGIN_SQL)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String login = resultSet.getString("username");
                    String passwordHash = resultSet.getString("password_hash");
                    return new User(id, login, passwordHash);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while getting user by username", e);
        }
    }


    public User getUserBySessionToken(String token) {
        try (Connection connection = connections.getConnection();
             PreparedStatement pstmt = connection.prepareStatement( GET_USER_BY_TOKEN)) {

            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String username = rs.getString("username");
                    String passwordHash = rs.getString("password");
                    return new User(id, username, passwordHash);
                }
            }
        } catch (SQLException e) {
           logger.log(Level.WARNING, String.format("Произошла ошибка во время получения user: %s", e.getMessage()));
        }
        return null;
    }



    public static AuthDao getInstance() {
        return INSTANCE;
    }
     String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.WARNING, String.format("Ошибка при хэшировании пароля: %s", e.getMessage()));
        }

        return password;
    }


}
