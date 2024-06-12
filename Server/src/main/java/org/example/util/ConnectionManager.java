package org.example.util;

import org.example.util.properties.Keys;
import org.example.util.properties.PropertiesUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {



    static {
        loadDriver();
    }

    private ConnectionManager() {
    }


    public static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(Keys.URL_KEY.get()),
                    PropertiesUtil.get(Keys.USERNAME_KEY.get()),
                    PropertiesUtil.get(Keys.PASSWORD_KEY.get())
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}