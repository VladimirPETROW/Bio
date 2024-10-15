package com.bio.database;

import com.bio.entity.Organism;
import com.bio.value.OrganismValue;

import java.sql.*;

public class OrganismDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS organism (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name TEXT, kind TEXT, doubling NUMERIC(6, 2))";
    public static String setIdSeq = "SELECT setval('organism_id_seq', (SELECT max(id) FROM organism))";
    public static String insert = "INSERT INTO organism (name, kind, doubling) VALUES (?, ?, ?) RETURNING id, name, kind, doubling";
    public static String select = "SELECT id, name, kind, doubling FROM organism ORDER BY id";
    public static String selectById = "SELECT id, name, kind, doubling FROM organism WHERE id = ?";
    public static String deleteById = "DELETE FROM organism WHERE id = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
        statement.execute(setIdSeq);
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
