package com.motocoredb.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtils {
    public static void beginTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }

    public static void commitTransaction(Connection connection) {
        try {
            if (connection != null) {
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Transaction commit failed", e);
        } finally {
            setAutoCommit(connection, true);
        }
    }

    public static void rollbackTransaction(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Transaction rollback failed", e);
        } finally {
            setAutoCommit(connection, true);
        }
    }

    private static void setAutoCommit(Connection connection, boolean autoCommit) {
        try {
            if (connection != null) {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to set auto-commit", e);
        }
    }
}