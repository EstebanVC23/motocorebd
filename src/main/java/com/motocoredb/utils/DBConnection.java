package com.motocoredb.utils;

import com.motocoredb.config.DataBaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(
                    DataBaseConfig.getDbUrl(),
                    DataBaseConfig.getDbUser(),
                    DataBaseConfig.getDbPassword()
                );
            } catch (SQLException e) {
                throw new SQLException("Failed to create database connection", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}