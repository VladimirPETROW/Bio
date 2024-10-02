package com.bio.database;

import com.bio.entity.FeedMaterial;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedMaterialDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS feed_material (feed BIGINT, material BIGINT, unit TEXT, count INTEGER)";
    public static String insert = "INSERT INTO feed_material (feed, material, unit, count) VALUES (?, ?, ?, ?)";
    public static String selectByFeed = "SELECT material, unit, count FROM feed_material WHERE feed = ?";
    public static String deleteByFeed = "DELETE FROM feed_material WHERE feed = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long feed, FeedMaterial feedMaterial) throws SQLException {
        statement.setLong(1, feed);
        statement.setLong(2, feedMaterial.getMaterial());
        statement.setString(3, feedMaterial.getUnit());
        statement.setInt(4, feedMaterial.getCount());
    }

    public static FeedMaterial get(ResultSet rs) throws SQLException {
        FeedMaterial feedMaterial = new FeedMaterial();
        feedMaterial.setMaterial(rs.getLong(1));
        feedMaterial.setUnit(rs.getString(2));
        feedMaterial.setCount(rs.getInt(3));
        return feedMaterial;
    }

    public static void prepareSelectByFeed(PreparedStatement statement, Long feed) throws SQLException {
        statement.setLong(1, feed);
    }

    public static void prepareDeleteByFeed(PreparedStatement statement, Long feed) throws SQLException {
        statement.setLong(1, feed);
    }
}
