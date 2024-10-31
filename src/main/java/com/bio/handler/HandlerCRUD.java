package com.bio.handler;

import com.bio.HttpMethod;
import com.bio.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.logging.Level;

@Log
public abstract class HandlerCRUD {

    public void handleCRUD(HttpExchange exchange, Function<HttpResponse, HttpResponse> formatter) throws IOException {
        HttpResponse response;
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        Worker worker = null;
        switch (method) {
            case POST: {
                worker = this::create;
                break;
            }
            case GET: {
                worker = this::read;
                break;
            }
            /*
            case PATCH: {
                worker = this::update;
                break;
            }
            */
            case DELETE: {
                worker = this::delete;
                break;
            }
        }
        if (worker == null) {
            response = HttpResponse.createResponse(405);
        }
        else {
            try {
                response = worker.process(exchange);
            } catch (Exception e) {
                log.log(Level.SEVERE, "", e);
                response = HttpResponse.createResponse(400);
            }
        }
        response = formatter.apply(response);
        Headers headers = exchange.getResponseHeaders();
        response.headersTo(headers);
        Handlers.sendText(response.code, response.message.toString(), exchange);
    }

    public HttpResponse read(HttpExchange exchange) throws SQLException {
        String start = exchange.getHttpContext().getPath();
        String request = exchange.getRequestURI().getPath();
        if (!request.startsWith(start)) {
            String message = "Неверный путь.";
            log.severe(message);
            return new HttpResponse(500, message);
        }
        String last = request.substring(start.length());
        if (!last.isEmpty()) {
            Long id = Long.parseLong(last);
            return readById(exchange, id);
        }
        return readAll(exchange);
    }

    /*
    public HttpResponse update(HttpExchange exchange) throws IOException, SQLException {
        String start = exchange.getHttpContext().getPath();
        String request = exchange.getRequestURI().getPath();
        if (!request.startsWith(start)) {
            String message = "Неверный путь.";
            log.severe(message);
            return new HttpResponse(500, message);
        }
        String last = request.substring(start.length());
        if (!last.isEmpty()) {
            Long id = Long.parseLong(last);
            return updateById(exchange, id);
        }
        return HttpResponse.createResponse(400);
    }
    */

    public HttpResponse delete(HttpExchange exchange) throws SQLException {
        String start = exchange.getHttpContext().getPath();
        String request = exchange.getRequestURI().getPath();
        if (!request.startsWith(start)) {
            String message = "Неверный путь.";
            log.severe(message);
            return new HttpResponse(500, message);
        }
        String last = request.substring(start.length());
        Long id = Long.parseLong(last);
        return deleteById(exchange, id);
    }

    public abstract HttpResponse create(HttpExchange exchange) throws IOException, SQLException;

    public abstract HttpResponse readById(HttpExchange exchange, Long id) throws SQLException;

    public abstract HttpResponse readAll(HttpExchange exchange) throws SQLException;

    //public abstract HttpResponse updateById(HttpExchange exchange, Long id) throws IOException, SQLException;

    public abstract HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException;

}
