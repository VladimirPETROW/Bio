package com.bio.database;

import com.bio.entity.SolutionRef;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SolutionRefDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS solution_ref (solution BIGINT, ref BIGINT, unit TEXT, count NUMERIC(8, 4), apply TEXT)";
    public static String insert = "INSERT INTO solution_ref (solution, ref, unit, count, apply) VALUES (?, ?, ?, ?, ?)";
    public static String selectBySolution = "SELECT ref, unit, count, apply FROM solution_ref WHERE solution = ?";
    public static String deleteBySolution = "DELETE FROM solution_ref WHERE solution = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long solution, SolutionRef solutionRef) throws SQLException {
        statement.setLong(1, solution);
        statement.setLong(2, solutionRef.getRef());
        statement.setString(3, solutionRef.getUnit());
        statement.setDouble(4, solutionRef.getCount());
        statement.setString(5, solutionRef.getApply());
    }

    public static SolutionRef get(ResultSet rs) throws SQLException {
        SolutionRef solutionRef = new SolutionRef();
        solutionRef.setRef(rs.getLong(1));
        solutionRef.setUnit(rs.getString(2));
        solutionRef.setCount(rs.getDouble(3));
        solutionRef.setApply(rs.getString(4));
        return solutionRef;
    }

    public static void prepareSelectBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }

    public static void prepareDeleteBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }
}
