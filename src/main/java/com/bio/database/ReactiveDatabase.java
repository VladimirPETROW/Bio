package com.bio.database;

import com.bio.entity.Reactive;
import com.bio.value.ReactiveValue;

import java.sql.*;

public class ReactiveDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS reactive (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name TEXT, kind TEXT, unit TEXT, count NUMERIC(8, 4), price NUMERIC(8, 2))";
    public static String setIdSeq = "SELECT setval('reactive_id_seq', (SELECT max(id) FROM reactive))";
    public static String insert = "INSERT INTO reactive (name, kind, unit, count, price) VALUES (?, ?, ?, ?, ?) RETURNING id, name, kind, unit, count, price";
    public static String select = "SELECT id, name, kind, unit, count, price FROM reactive ORDER BY id";
    public static String selectById = "SELECT id, name, kind, unit, count, price FROM reactive WHERE id = ?";
    public static String deleteById = "DELETE FROM reactive WHERE id = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
        statement.execute(setIdSeq);
    }

    public static void prepareInsert(PreparedStatement statement, ReactiveValue reactiveValue) throws SQLException {
        statement.setString(1, reactiveValue.getName());
        statement.setString(2, reactiveValue.getKind());
        statement.setString(3, reactiveValue.getUnit());
        statement.setObject(4, reactiveValue.getCount());
        statement.setObject(5, reactiveValue.getPrice());
    }

    public static Reactive get(ResultSet rs) throws SQLException {
        Reactive reactive = new Reactive();
        reactive.setId(rs.getLong(1));
        reactive.setName(rs.getString(2));
        reactive.setKind(rs.getString(3));
        reactive.setUnit(rs.getString(4));
        reactive.setCount(rs.getDouble(5));
        if (rs.wasNull()) reactive.setCount(null);
        reactive.setPrice(rs.getDouble(6));
        if (rs.wasNull()) reactive.setPrice(null);
        return reactive;
    }

    public static void prepareSelectById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    public static void prepareDeleteById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }
}
