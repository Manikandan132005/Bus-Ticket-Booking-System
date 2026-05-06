package com.busticket.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Singleton database connection manager.
 * Reads config from db.properties; falls back to defaults.
 */
public class DBConnection {

    private static final String DEFAULT_URL      = "jdbc:mysql://localhost:3306/bus_booking_db?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER     = "root";
    private static final String DEFAULT_PASSWORD = "mani@2005";

    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = loadProperties();
            String url  = props.getProperty("db.url",      DEFAULT_URL);
            String user = props.getProperty("db.username", DEFAULT_USER);
            String pass = props.getProperty("db.password", DEFAULT_PASSWORD);
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("[DB] Connected to database successfully.");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB] Error closing connection: " + e.getMessage());
            }
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            // will fall back to defaults
        }
        return props;
    }
}
