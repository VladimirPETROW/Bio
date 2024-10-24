package com.bio.database;

import com.bio.entity.Material;
import com.bio.value.MaterialValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MaterialDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS material (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name TEXT, unit TEXT, count NUMERIC(8, 4), price NUMERIC(8, 2))";
    public static String setIdSeq = "SELECT setval('material_id_seq', (SELECT max(id) FROM material))";
    public static String insert = "INSERT INTO material (name, unit, count, price) VALUES (?, ?, ?, ?) RETURNING id";
    public static String select = "SELECT id, name, unit, count, price FROM material ORDER BY id";
    public static String selectById = "SELECT id, name, unit, count, price FROM material WHERE id = ?";
    public static String deleteById = "DELETE FROM material WHERE id = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
        statement.execute(setIdSeq);
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
