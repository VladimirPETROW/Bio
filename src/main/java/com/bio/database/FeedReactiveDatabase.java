package com.bio.database;

import com.bio.entity.FeedReactive;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedReactiveDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS feed_reactive (feed BIGINT, reactive BIGINT, unit TEXT, count INTEGER)";
    public static String insert = "INSERT INTO feed_reactive (feed, reactive, unit, count) VALUES (?, ?, ?, ?)";
    public static String selectByFeed = "SELECT reactive, unit, count FROM feed_reactive WHERE feed = ?";
    public static String deleteByFeed = "DELETE FROM feed_reactive WHERE feed = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long feed, FeedReactive feedReactive) throws SQLException {
        statement.setLong(1, feed);
        statement.setLong(2, feedReactive.getReactive());
        statement.setString(3, feedReactive.getUnit());
        statement.setInt(4, feedReactive.getCount());
    }

    public static FeedReactive get(ResultSet rs) throws SQLException {
        FeedReactive feedReactive = new FeedReactive();
        feedReactive.setReactive(rs.getLong(1));
        feedReactive.setUnit(rs.getString(2));
        feedReactive.setCount(rs.getInt(3));
        return feedReactive;
    }

    public static void prepareSelectByFeed(PreparedStatement statement, Long feed) throws SQLException {
        statement.setLong(1, feed);
    }

    public static void prepareDeleteByFeed(PreparedStatement statement, Long feed) throws SQLException {
        statement.setLong(1, feed);
    }
}
