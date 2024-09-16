package com.bio.database;

import com.bio.entity.Material;
import com.bio.value.MaterialValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaterialDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS material (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name TEXT, unit TEXT, price INTEGER)";
    public static String insert = "INSERT INTO material (name, unit, price) VALUES (?, ?, ?) RETURNING id";
    public static String select = "SELECT id, name, unit, price FROM material ORDER BY id";
    public static String selectById = "SELECT id, name, unit, price FROM material WHERE id = ?";
    public static String deleteById = "DELETE FROM material WHERE id = ?";

    public static void prepareInsert(PreparedStatement statement, MaterialValue materialValue) throws SQLException {
        statement.setString(1, materialValue.getName());
        statement.setString(2, materialValue.getUnit());
        statement.setObject(3, materialValue.getPrice());
    }

    public static Material get(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setId(rs.getLong(1));
        material.setName(rs.getString(2));
        material.setUnit(rs.getString(3));
        material.setPrice(rs.getInt(4));
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
