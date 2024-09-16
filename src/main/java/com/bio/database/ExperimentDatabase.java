package com.bio.database;

import com.bio.entity.Experiment;
import com.bio.value.ExperimentValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ExperimentDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS experiment (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, created TIMESTAMP, organism BIGINT, feed BIGINT, whole INTEGER, product INTEGER, koe INTEGER)";
    public static String insert = "INSERT INTO experiment (created, organism, feed, whole, product, koe) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    public static String select = "SELECT id, created, organism, feed, whole, product, koe FROM experiment ORDER BY id";
    public static String selectById = "SELECT id, created, organism, feed, whole, product, koe FROM experiment WHERE id = ?";
    public static String deleteById = "DELETE FROM experiment WHERE id = ?";

    public static void prepareInsert(PreparedStatement statement, ExperimentValue experimentValue) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        statement.setLong(2, experimentValue.getOrganism());
        statement.setLong(3, experimentValue.getFeed());
        statement.setObject(4, experimentValue.getWhole());
        statement.setObject(5, experimentValue.getProduct());
        statement.setObject(6, experimentValue.getKoe());
    }

    public static Experiment get(ResultSet rs) throws SQLException {
        Experiment experiment = new Experiment();
        experiment.setId(rs.getLong(1));
        experiment.setCreated(rs.getTimestamp(2).toLocalDateTime());
        experiment.setOrganism(rs.getLong(3));
        experiment.setFeed(rs.getLong(4));
        experiment.setWhole(rs.getInt(5));
        if (rs.wasNull()) experiment.setWhole(null);
        experiment.setProduct(rs.getInt(6));
        if (rs.wasNull()) experiment.setProduct(null);
        experiment.setKoe(rs.getInt(7));
        if (rs.wasNull()) experiment.setKoe(null);
        return experiment;
    }

    public static void prepareSelectById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    public static void prepareDeleteById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }
}
