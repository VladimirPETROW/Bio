package com.bio.database;

import com.bio.entity.Info;

import java.sql.*;

public class InfoDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS info (name TEXT PRIMARY KEY, content TEXT)";
    public static String insert = "INSERT INTO info (name, content) VALUES (?, ?)";
    public static String selectByName = "SELECT name, content FROM info WHERE name = ?";
    public static String deleteByName = "DELETE FROM info WHERE name = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void insert(Connection connection, Info info) throws SQLException {
        try (PreparedStatement stmtInsert = connection.prepareStatement(insert)) {
            prepareInsert(stmtInsert, info);
            stmtInsert.executeUpdate();
        }
    }

    public static void prepareInsert(PreparedStatement statement, Info info) throws SQLException {
        statement.setString(1, info.getName());
        statement.setString(2, info.getContent());
    }

    public static Info get(ResultSet rs) throws SQLException {
        Info info = new Info();
        info.setName(rs.getString(1));
        info.setContent(rs.getString(2));
        return info;
    }

    public static Info select(Connection connection, String name) throws SQLException {
        try (PreparedStatement stmtSelect = connection.prepareStatement(selectByName)) {
            prepareSelectByName(stmtSelect, name);
            ResultSet rs = stmtSelect.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return get(rs);
        }
    }

    public static void prepareSelectByName(PreparedStatement statement, String name) throws SQLException {
        statement.setString(1, name);
    }

    public static void prepareDeleteByName(PreparedStatement statement, String name) throws SQLException {
        statement.setString(1, name);
    }

}
