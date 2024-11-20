package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.ReactiveDatabase;
import com.bio.entity.Reactive;
import com.bio.value.ReactiveValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ReactiveHandler extends HandlerCRUD {

    private static Logger log = Logger.getLogger(ReactiveHandler.class.getName());

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        ReactiveValue reactiveValue = mapper.readValue(request, ReactiveValue.class);
        StringBuffer error = new StringBuffer();
        if (reactiveValue.getName() == null) {
            error.append("Не указано название.");
        }
        /*
        if (reactiveValue.getKind() == null) {
            if (error.length() > 0) {
                error.append(" ");
            }
            error.append("Не указан вид.");
        }
        */
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Connection connection = Bio.database.getConnection();
        Reactive reactive = ReactiveDatabase.insert(connection, reactiveValue);
        connection.commit();
        String message = String.format("Реактив %d добавлен.", reactive.getId());
        log.info(message);
        return new HttpResponse(200, reactive);
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement()) {
            ArrayList<Reactive> reactives = new ArrayList<>();
            ResultSet rs = statement.executeQuery(ReactiveDatabase.select);
            while (rs.next()) {
                Reactive reactive = ReactiveDatabase.get(rs);
                reactives.add(reactive);
            }
            return new HttpResponse(200, reactives.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(ReactiveDatabase.selectById)) {
            ReactiveDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Реактив %d не найден.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Reactive reactive = ReactiveDatabase.get(rs);
            return new HttpResponse(200, reactive);
        }
    }

    public HttpResponse rewriteById(HttpExchange exchange, Long id) throws IOException, SQLException {
        return null;
    }

    /*
    public HttpResponse modifyById(HttpExchange exchange, Long id) throws IOException, SQLException {
        return null;
    }
    */

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(ReactiveDatabase.deleteById)) {
            ReactiveDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Реактив %d удален.", id);
            }
            else {
                message = String.format("Реактив %d не найден.", id);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
