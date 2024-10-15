package com.bio.database;

import com.bio.entity.SolutionReactive;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SolutionReactiveDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS solution_reactive (solution BIGINT, reactive BIGINT, unit TEXT, count NUMERIC(8, 4), apply TEXT)";
    public static String insert = "INSERT INTO solution_reactive (solution, reactive, unit, count, apply) VALUES (?, ?, ?, ?, ?)";
    public static String selectByFeed = "SELECT reactive, unit, count, apply FROM solution_reactive WHERE solution = ?";
    public static String deleteByFeed = "DELETE FROM solution_reactive WHERE solution = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long solution, SolutionReactive solutionReactive) throws SQLException {
        statement.setLong(1, solution);
        statement.setLong(2, solutionReactive.getReactive());
        statement.setString(3, solutionReactive.getUnit());
        statement.setDouble(4, solutionReactive.getCount());
        statement.setString(5, solutionReactive.getApply());
    }

    public static SolutionReactive get(ResultSet rs) throws SQLException {
        SolutionReactive solutionReactive = new SolutionReactive();
        solutionReactive.setReactive(rs.getLong(1));
        solutionReactive.setUnit(rs.getString(2));
        solutionReactive.setCount(rs.getDouble(3));
        solutionReactive.setApply(rs.getString(4));
        return solutionReactive;
    }

    public static void prepareSelectBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }

    public static void prepareDeleteBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }
}
