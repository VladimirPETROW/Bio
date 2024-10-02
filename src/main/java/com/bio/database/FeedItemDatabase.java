package com.bio.database;

import com.bio.entity.FeedItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FeedItemDatabase {

    public static String createTable = "CREATE TABLE IF NOT EXISTS feed_item (feed BIGINT, item BIGINT, unit TEXT, count INTEGER)";
    public static String insert = "INSERT INTO feed_item (feed, item, unit, count) VALUES (?, ?, ?, ?)";
    public static String selectByFeed = "SELECT item, unit, count FROM feed_item WHERE feed = ?";
    public static String deleteByFeed = "DELETE FROM feed_item WHERE feed = ?";

    public static void init(Statement statement) throws SQLException {
        statement.execute(createTable);
    }

    public static void prepareInsert(PreparedStatement statement, Long feed, FeedItem feedItem) throws SQLException {
        statement.setLong(1, feed);
        statement.setLong(2, feedItem.getItem());
        statement.setString(3, feedItem.getUnit());
        statement.setInt(4, feedItem.getCount());
    }

    public static FeedItem get(ResultSet rs) throws SQLException {
        FeedItem feedItem = new FeedItem();
        feedItem.setItem(rs.getLong(1));
        feedItem.setUnit(rs.getString(2));
        feedItem.setCount(rs.getInt(3));
        return feedItem;
    }

    public static void prepareSelectByFeed(PreparedStatement statement, Long feed) throws SQLException {
        statement.setLong(1, feed);
    }

    public static void prepareDeleteByFeed(PreparedStatement statement, Long feed) throws SQLException {
        statement.setLong(1, feed);
    }
}
