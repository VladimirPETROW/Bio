package com.bio.database;

import com.bio.entity.SolutionMaterial;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SolutionMaterialDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS solution_material (solution BIGINT, material BIGINT, unit TEXT, count NUMERIC(8, 4), apply TEXT)";
    public static String insert = "INSERT INTO solution_material (solution, material, unit, count, apply) VALUES (?, ?, ?, ?, ?)";
    public static String selectByFeed = "SELECT material, unit, count, apply FROM solution_material WHERE solution = ?";
    public static String deleteByFeed = "DELETE FROM solution_material WHERE solution = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long solution, SolutionMaterial solutionMaterial) throws SQLException {
        statement.setLong(1, solution);
        statement.setLong(2, solutionMaterial.getMaterial());
        statement.setString(3, solutionMaterial.getUnit());
        statement.setDouble(4, solutionMaterial.getCount());
        statement.setString(5, solutionMaterial.getApply());
    }

    public static SolutionMaterial get(ResultSet rs) throws SQLException {
        SolutionMaterial solutionMaterial = new SolutionMaterial();
        solutionMaterial.setMaterial(rs.getLong(1));
        solutionMaterial.setUnit(rs.getString(2));
        solutionMaterial.setCount(rs.getDouble(3));
        solutionMaterial.setApply(rs.getString(4));
        return solutionMaterial;
    }

    public static void prepareSelectBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }

    public static void prepareDeleteBySolution(PreparedStatement statement, Long solution) throws SQLException {
        statement.setLong(1, solution);
    }
}
