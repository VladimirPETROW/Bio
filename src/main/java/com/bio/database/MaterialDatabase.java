package com.bio.database;

import com.bio.entity.Material;
import com.bio.value.MaterialValue;

import java.sql.*;

public class MaterialDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS material (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name TEXT, unit TEXT, count NUMERIC(8, 4), price NUMERIC(8, 2))";
    public static String[] setIdSeq = {"ALTER TABLE material ALTER COLUMN id RESTART WITH ", "SELECT max(id) FROM material"};
    public static String insert = "INSERT INTO material (name, unit, count, price) VALUES (?, ?, ?, ?)";
    public static String select = "SELECT id, name, unit, count, price FROM material ORDER BY id";
    public static String selectById = "SELECT id, name, unit, count, price FROM material WHERE id = ?";
    public static String deleteById = "DELETE FROM material WHERE id = ?";

    public static long standardId = 1000;

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
        // id
        ResultSet rs = statement.executeQuery(setIdSeq[1]);
        rs.next();
        long max = rs.getLong(1);
        max = Math.max(max, standardId);
        statement.execute(setIdSeq[0] + (max + 1));
    }

    public static Material insert(Connection connection, MaterialValue materialValue) throws SQLException {
        try (PreparedStatement stmtInsert = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtSelect = connection.prepareStatement(selectById)) {
            prepareInsert(stmtInsert, materialValue);
            stmtInsert.executeUpdate();
            ResultSet rs = stmtInsert.getGeneratedKeys();
            rs.next();
            Long id = rs.getLong(1);
            prepareSelectById(stmtSelect, id);
            rs = stmtSelect.executeQuery();
            rs.next();
            return get(rs);
        }
    }

    public static void prepareInsert(PreparedStatement statement, MaterialValue materialValue) throws SQLException {
        statement.setString(1, materialValue.getName());
        statement.setString(2, materialValue.getUnit());
        statement.setObject(3, materialValue.getCount());
        statement.setObject(4, materialValue.getPrice());
    }

    public static Material get(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setId(rs.getLong(1));
        material.setName(rs.getString(2));
        material.setUnit(rs.getString(3));
        material.setCount(rs.getDouble(4));
        if (rs.wasNull()) material.setCount(null);
        material.setPrice(rs.getDouble(5));
        if (rs.wasNull()) material.setPrice(null);
        return material;
    }

    public static void prepareSelectById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    public static void prepareDeleteById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }
}
