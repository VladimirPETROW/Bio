package com.bio.database;

import com.bio.entity.Organism;
import com.bio.value.OrganismValue;

import java.sql.*;

public class OrganismDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS organism (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name TEXT, kind TEXT, doubling NUMERIC(6, 2))";
    public static String[] setIdSeq = {"ALTER TABLE organism ALTER COLUMN id RESTART WITH ", "SELECT max(id) FROM organism"};
    public static String insert = "INSERT INTO organism (name, kind, doubling) VALUES (?, ?, ?)";
    public static String select = "SELECT id, name, kind, doubling FROM organism ORDER BY id";
    public static String selectById = "SELECT id, name, kind, doubling FROM organism WHERE id = ?";
    public static String deleteById = "DELETE FROM organism WHERE id = ?";

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

    public static Organism insert(Connection connection, OrganismValue organismValue) throws SQLException {
        try (PreparedStatement stmtInsert = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtSelect = connection.prepareStatement(selectById)) {
            prepareInsert(stmtInsert, organismValue);
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

    public static void prepareInsert(PreparedStatement statement, OrganismValue organismValue) throws SQLException {
        statement.setString(1, organismValue.getName());
        statement.setString(2, organismValue.getKind());
        statement.setDouble(3, organismValue.getDoubling());
    }

    public static Organism get(ResultSet rs) throws SQLException {
        Organism organism = new Organism();
        organism.setId(rs.getLong(1));
        organism.setName(rs.getString(2));
        organism.setKind(rs.getString(3));
        organism.setDoubling(rs.getDouble(4));
        return organism;
    }

    public static void prepareSelectById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    public static void prepareDeleteById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

}
