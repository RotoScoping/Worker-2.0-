package org.example.storage.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.util.properties.Keys;
import org.example.util.properties.PropertiesUtil;


import javax.sql.DataSource;

public class DataSourceFactory {
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertiesUtil.get(Keys.URL_KEY.get()));
        config.setUsername(PropertiesUtil.get(Keys.USERNAME_KEY.get()));
        config.setPassword(PropertiesUtil.get(Keys.PASSWORD_KEY.get()));
        config.setMaximumPoolSize(10); // максимум 10

        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(20000);
        config.setInitializationFailTimeout(20000);

        config.setAutoCommit(true);
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}