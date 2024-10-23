package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.FeedDatabase;
import com.bio.entity.Feed;
import com.bio.value.FeedValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

@Log
public class FeedHandler extends HandlerCRUD {

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        FeedValue feedValue = mapper.readValue(request, FeedValue.class);
        StringBuffer error = new StringBuffer();
        if (feedValue.getSolution() == null) {
            error.append("Не указан раствор.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FeedDatabase.insert)) {
            FeedDatabase.prepareInsert(statement, feedValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            Long id = rs.getLong(1);
            connection.commit();
            String message = String.format("Питательная среда %d добавлена.", id);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement()) {
            ArrayList<Feed> feeds = new ArrayList<>();
            ResultSet rs = statement.executeQuery(FeedDatabase.select);
            while (rs.next()) {
                Feed feed = FeedDatabase.get(rs);
                feeds.add(feed);
            }
            return new HttpResponse(200, feeds.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FeedDatabase.selectById)) {
            FeedDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Питательная среда %d не найдена.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Feed feed = FeedDatabase.get(rs);
            return new HttpResponse(200, feed);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FeedDatabase.deleteById)) {
            FeedDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Питательная среда %d удалена.", id);
            }
            else {
                message = String.format("Питательная среда %d не найдена.", id);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
