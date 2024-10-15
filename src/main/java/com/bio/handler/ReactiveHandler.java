package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.ReactiveDatabase;
import com.bio.entity.Reactive;
import com.bio.value.ReactiveValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@Log
public class ReactiveHandler extends HandlerCRUD {

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
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(ReactiveDatabase.insert)) {
            ReactiveDatabase.prepareInsert(statement, reactiveValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            //Long id = rs.getLong(1);
            Reactive reactive = ReactiveDatabase.get(rs);
            Bio.database.commit();
            String message = String.format("Реактив %d добавлен.", reactive.getId());
            log.info(message);
            return new HttpResponse(200, reactive);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Bio.database.rollback();
        try (Statement statement = Bio.database.createStatement()) {
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
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(ReactiveDatabase.selectById)) {
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

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(ReactiveDatabase.deleteById)) {
            ReactiveDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Реактив %d удален.", id);
            }
            else {
                message = String.format("Реактив %d не найден.", id);
            }
            Bio.database.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
