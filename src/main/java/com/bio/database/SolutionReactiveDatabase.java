package com.bio.database;

import com.bio.entity.Reactive;
import com.bio.entity.SolutionReactive;
import com.bio.value.SolutionReactiveValue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SolutionReactiveDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS solution_reactive (solution BIGINT, reactive BIGINT, unit TEXT, count NUMERIC(8, 4), apply TEXT)";
    public static String insert = "INSERT INTO solution_reactive (solution, reactive, unit, count, apply) VALUES (?, ?, ?, ?, ?)";
    public static String rewriteBySolutionReactive = "UPDATE solution_reactive SET unit = ?, count = ?, apply = ? WHERE (solution = ?) and (reactive = ?)";
    public static String selectBySolution = "SELECT reactive, unit, count, apply FROM solution_reactive WHERE solution = ? ORDER BY reactive";
    public static String deleteBySolution = "DELETE FROM solution_reactive WHERE solution = ?";
    public static String selectBySolutionReactive = "SELECT reactive, unit, count, apply FROM solution_reactive WHERE (solution = ?) and (reactive = ?)";
    public static String deleteBySolutionReactive = "DELETE FROM solution_reactive WHERE (solution = ?) and (reactive = ?)";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long solution, SolutionReactive solutionReactive) throws SQLException {
        statement.setLong(1, solution);
        statement.setLong(2, solutionReactive.getReactive().getId());
        statement.setString(3, solutionReactive.getUnit());
        statement.setDouble(4, solutionReactive.getCount());
        statement.setString(5, solutionReactive.getApply());
    }

    public static SolutionReactive get(ResultSet rs) throws SQLException {
        SolutionReactive solutionReactive = new SolutionReactive();
        Reactive reactive = new Reactive();
        reactive.setId(rs.getLong(1));
        solutionReactive.setReactive(reactive);
        solutionReactive.setUnit(rs.getString(2));
        solutionReactive.setCount(rs.getDouble(3));
        solutionReactive.setApply(rs.getString(4));
        return solutionReactive;
    }

    public static void prepareSelectBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }

    public static void prepareRewriteBySolutionReactive(PreparedStatement statement, Long solution, Long reactive, SolutionReactiveValue solutionReactiveValue) throws SQLException {
        statement.setString(1, solutionReactiveValue.getUnit());
        statement.setDouble(2, solutionReactiveValue.getCount());
        statement.setString(3, solutionReactiveValue.getApply());
        statement.setLong(4, solution);
        statement.setLong(5, reactive);
    }

    public static void prepareDeleteBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }

    public static void prepareSelectBySolutionReactive(PreparedStatement statement, Long solution, Long reactive) throws SQLException {
        statement.setLong(1, solution);
        statement.setLong(2, reactive);
    }

    public static void prepareDeleteBySolutionReactive(PreparedStatement statement, Long solution, Long reactive) throws SQLException {
        statement.setLong(1, solution);
        statement.setLong(2, reactive);
    }

}
