package com.bio.database;

import com.bio.entity.Solution;
import com.bio.value.SolutionValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SolutionDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS solution (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name TEXT, apply TEXT)";
    public static String setIdSeq = "SELECT setval('solution_id_seq', (SELECT max(id) FROM solution))";
    public static String insert = "INSERT INTO solution (name, apply) VALUES (?, ?) RETURNING id";
    public static String select = "SELECT id, name, apply FROM solution ORDER BY id";
    public static String selectById = "SELECT id, name, apply FROM solution WHERE id = ?";
    public static String deleteById = "DELETE FROM solution WHERE id = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
        statement.execute(setIdSeq);
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