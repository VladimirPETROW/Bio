package com.bio.database;

import com.bio.entity.Experiment;
import com.bio.entity.Feed;
import com.bio.value.ExperimentValue;

import java.sql.*;
import java.time.LocalDateTime;

public class ExperimentDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS experiment (id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, organism BIGINT, feed BIGINT, ferment_begin TIMESTAMP, ferment_end TIMESTAMP, speed NUMERIC(6, 2), temperature NUMERIC(6, 2), ph NUMERIC(4, 2), whole NUMERIC(8, 4), product NUMERIC(8, 4), koe NUMERIC(8, 4), comment TEXT)";
    public static String[] setIdSeq = {"ALTER TABLE experiment ALTER COLUMN id RESTART WITH ", "SELECT max(id) FROM experiment"};
    public static String insert = "INSERT INTO experiment (organism, feed, ferment_begin, ferment_end, speed, temperature, ph, whole, product, koe, comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static String select = "SELECT id, organism, feed, ferment_begin, ferment_end, speed, temperature, ph, whole, product, koe, comment FROM experiment ORDER BY id";
    public static String selectById = "SELECT id, organism, feed, ferment_begin, ferment_end, speed, temperature, ph, whole, product, koe, comment FROM experiment WHERE id = ?";
    public static String deleteById = "DELETE FROM experiment WHERE id = ?";

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

    public static Experiment insert(Connection connection, ExperimentValue experimentValue) throws SQLException {
        try (PreparedStatement stmtInsert = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtSelect = connection.prepareStatement(selectById)) {
            prepareInsert(stmtInsert, experimentValue);
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

    public static void prepareInsert(PreparedStatement statement, ExperimentValue experimentValue) throws SQLException {
        //statement.setLong(1, experimentValue.getOrganism());
        statement.setObject(1, experimentValue.getOrganism());
        statement.setLong(2, experimentValue.getFeed());
        LocalDateTime fermentBegin = experimentValue.getFermentBegin();
        if (fermentBegin == null) {
            statement.setObject(3, null);
        }
        else {
            statement.setTimestamp(3, Timestamp.valueOf(fermentBegin));
        }
        LocalDateTime fermentEnd = experimentValue.getFermentEnd();
        if (fermentEnd == null) {
            statement.setObject(4, null);
        }
        else {
            statement.setTimestamp(4, Timestamp.valueOf(fermentEnd));
        }
        statement.setObject(5, experimentValue.getSpeed());
        statement.setObject(6, experimentValue.getTemperature());
        statement.setObject(7, experimentValue.getPh());
        statement.setObject(8, experimentValue.getWhole());
        statement.setObject(9, experimentValue.getProduct());
        statement.setObject(10, experimentValue.getKoe());
        statement.setString(11, experimentValue.getComment());
    }

    public static Experiment get(ResultSet rs) throws SQLException {
        Experiment experiment = new Experiment();
        experiment.setId(rs.getLong(1));
        experiment.setOrganism(rs.getLong(2));
        if (rs.wasNull()) {
            experiment.setOrganism(null);
        }
        Feed feed = new Feed();
        feed.setId(rs.getLong(3));
        experiment.setFeed(feed);
        Timestamp fermentBegin = rs.getTimestamp(4);
        if (!rs.wasNull()) {
            experiment.setFermentBegin(fermentBegin.toLocalDateTime());
        }
        Timestamp fermentEnd = rs.getTimestamp(5);
        if (!rs.wasNull()) {
            experiment.setFermentEnd(fermentEnd.toLocalDateTime());
        }
        experiment.setSpeed(rs.getDouble(6));
        if (rs.wasNull()) {
            experiment.setSpeed(null);
        }
        experiment.setTemperature(rs.getDouble(7));
        if (rs.wasNull()) {
            experiment.setTemperature(null);
        }
        experiment.setPh(rs.getDouble(8));
        if (rs.wasNull()) {
            experiment.setPh(null);
        }
        experiment.setWhole(rs.getDouble(9));
        if (rs.wasNull()) {
            experiment.setWhole(null);
        }
        experiment.setProduct(rs.getDouble(10));
        if (rs.wasNull()) {
            experiment.setProduct(null);
        }
        experiment.setKoe(rs.getDouble(11));
        if (rs.wasNull()) {
            experiment.setKoe(null);
        }
        experiment.setComment(rs.getString(12));
        return experiment;
    }

    public static void prepareSelectById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    public static void prepareDeleteById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }
}
