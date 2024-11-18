package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.ReactiveDatabase;
import com.bio.database.SolutionReactiveDatabase;
import com.bio.entity.Reactive;
import com.bio.entity.SolutionReactive;
import com.bio.value.SolutionReactiveValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.logging.Logger;

public class SolutionReactiveHandler extends HandlerNestedCRUD {

    private static Logger log = Logger.getLogger(SolutionReactiveHandler.class.getName());

    public HttpResponse createNested(HttpExchange exchange, Long id) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        SolutionReactive solutionReactive = mapper.readValue(request, SolutionReactive.class);
        StringBuffer error = new StringBuffer();
        if (solutionReactive.getReactive() == null) {
            error.append("Не указан реактив.");
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
        try (PreparedStatement statement = connection.prepareStatement(SolutionReactiveDatabase.insert)) {
            SolutionReactiveDatabase.prepareInsert(statement, id, solutionReactive);
            int rs = statement.executeUpdate();
            connection.commit();
            String message = String.format("Реактив %d добавлен в раствор.", solutionReactive.getReactive().getId());
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    @Override
    public HttpResponse readNestedById(HttpExchange exchange, Long idFirst, Long idSecond) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SolutionReactiveDatabase.selectBySolutionReactive);
             PreparedStatement statementReactive = connection.prepareStatement(ReactiveDatabase.selectById)) {
            SolutionReactiveDatabase.prepareSelectBySolutionReactive(statement, idFirst, idSecond);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Реактив %d в растворе не найден.", idSecond);
                log.info(message);
                return new HttpResponse(404, message);
            }
            SolutionReactive solutionReactive = SolutionReactiveDatabase.get(rs);
            ReactiveDatabase.prepareSelectById(statementReactive, solutionReactive.getReactive().getId());
            ResultSet rsReactive = statementReactive.executeQuery();
            rsReactive.next();
            Reactive reactive = ReactiveDatabase.get(rsReactive);
            solutionReactive.setReactive(reactive);
            return new HttpResponse(200, solutionReactive);
        }
    }

    @Override
    public HttpResponse readNestedAll(HttpExchange exchange, Long id) throws SQLException {
        return null;
    }

    public HttpResponse rewriteNestedById(HttpExchange exchange, Long idFirst, Long idSecond) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        SolutionReactiveValue solutionReactiveValue = mapper.readValue(request, SolutionReactiveValue.class);
        /*
        StringBuffer error = new StringBuffer();
        if (solutionReactive.getReactive() == null) {
            error.append("Не указан реактив.");
        }
        if (reactiveValue.getKind() == null) {
            if (error.length() > 0) {
                error.append(" ");
            }
            error.append("Не указан вид.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        */
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SolutionReactiveDatabase.rewriteBySolutionReactive)) {
            SolutionReactiveDatabase.prepareRewriteBySolutionReactive(statement, idFirst, idSecond, solutionReactiveValue);
            int rs = statement.executeUpdate();
            connection.commit();
            String message = String.format("Реактив %d обновлен в растворе.", idSecond);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse deleteNestedById(HttpExchange exchange, Long idFirst, Long idSecond) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SolutionReactiveDatabase.deleteBySolutionReactive)) {
            SolutionReactiveDatabase.prepareDeleteBySolutionReactive(statement, idFirst, idSecond);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Реактив %d удален из раствора.", idSecond);
            }
            else {
                message = String.format("Реактив %d не найден в растворе.", idSecond);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    @Override
    public HttpResponse deleteNestedAll(HttpExchange exchange, Long id) throws SQLException {
        return null;
    }

    /*
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
    */
}
