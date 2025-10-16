package org.example.sistemasrecetasbd_v.Data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DB {
    private static HikariDataSource ds;

    static {
        try (InputStream in = DB.class.getClassLoader().getResourceAsStream("db_copy.properties")) {
            Properties p = new Properties();
            p.load(in);

            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(p.getProperty("db.url"));
            cfg.setUsername(p.getProperty("db.user"));
            cfg.setPassword(p.getProperty("db.password"));
            cfg.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.pool.size", "10")));
            cfg.setPoolName("MyAppPool");

            // Opcionales recomendados:
            cfg.setMinimumIdle(2);
            cfg.setConnectionTimeout(10000);
            cfg.setIdleTimeout(60000);
            cfg.setMaxLifetime(1800000);

            ds = new HikariDataSource(cfg);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo inicializar el pool de conexiones", e);
        }
    }

    private DB() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}