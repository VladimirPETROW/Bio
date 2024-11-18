package com.bio.database;

import java.sql.*;
import java.util.Properties;

public class DatabaseConnection {

    String url;
    Properties connProps;
    boolean autoCommit;

    Connection connection;

    boolean corrupted;

    public DatabaseConnection(String url, Properties connProps, boolean autoCommit) throws SQLException {
        this.url = url;
        this.connProps = connProps;
        this.autoCommit = autoCommit;

        connection = DriverManager.getConnection(url, connProps);
        connection.setAutoCommit(autoCommit);
    }

    public Connection getConnection() throws SQLException {
        if (corrupted) {
            throw new RuntimeException("Database corrupted.");
        }
        try {
            connection.rollback();
        }
        catch (SQLException ex) {
            System.out.println("Reconnecting to database.");
            connection = DriverManager.getConnection(url, connProps);
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    public void close() throws SQLException {
        connection.close();
    }

    public void setCorrupted(boolean corrupted) {
        this.corrupted = corrupted;
    }
}
