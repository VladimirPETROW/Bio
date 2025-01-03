package com.bio.database;

import com.bio.entity.Solution;
import com.bio.value.SolutionValue;

import java.sql.*;

public class SolutionDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS solution (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name TEXT, apply TEXT)";
    public static String[] setIdSeq = {"ALTER TABLE solution ALTER COLUMN id RESTART WITH ", "SELECT max(id) FROM solution"};
    public static String insert = "INSERT INTO solution (name, apply) VALUES (?, ?)";
    public static String select = "SELECT id, name, apply FROM solution ORDER BY id";
    public static String selectById = "SELECT id, name, apply FROM solution WHERE id = ?";
    public static String deleteById = "DELETE FROM solution WHERE id = ?";

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

    public static Solution insert(Connection connection, SolutionValue solutionValue) throws SQLException {
        try (PreparedStatement stmtInsert = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtSelect = connection.prepareStatement(selectById)) {
            prepareInsert(stmtInsert, solutionValue);
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

    public static void prepareInsert(PreparedStatement statement, SolutionValue solutionValue) throws SQLException {
        statement.setString(1, solutionValue.getName());
        statement.setString(2, solutionValue.getApply());
    }

    public static Solution get(ResultSet rs) throws SQLException {
        Solution solution = new Solution();
        solution.setId(rs.getLong(1));
        solution.setName(rs.getString(2));
        solution.setApply(rs.getString(3));
        return solution;
    }

    public static void prepareSelectById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

    public static void prepareDeleteById(PreparedStatement statement, Long id) throws SQLException {
        statement.setLong(1, id);
    }

}
