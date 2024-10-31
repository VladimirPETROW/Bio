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
public abstract class HandlerNestedCRUD {

    public void handleNestedCRUD(HttpExchange exchange, Function<HttpResponse, HttpResponse> formatter) throws IOException {
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
            case PUT: {
                worker = this::rewrite;
                break;
            }
            /*
            case PATCH: {
                worker = this::modify;
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

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
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
            return createNested(exchange, id);
        }
        return new HttpResponse(400, "Отсутствует первый идентификатор.");
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
            int pos = last.indexOf('/');
            if (pos > 0) {
                Long idFirst = Long.parseLong(last.substring(0, pos));
                Long idSecond = Long.parseLong(last.substring(pos + 1));
                return readNestedById(exchange, idFirst, idSecond);
            }
            Long id = Long.parseLong(last);
            return readNestedAll(exchange, id);
        }
        return new HttpResponse(400, "Отсутствует первый идентификатор.");
    }

    public HttpResponse rewrite(HttpExchange exchange) throws IOException, SQLException {
        String start = exchange.getHttpContext().getPath();
        String request = exchange.getRequestURI().getPath();
        if (!request.startsWith(start)) {
            String message = "Неверный путь.";
            log.severe(message);
            return new HttpResponse(500, message);
        }
        String last = request.substring(start.length());
        if (!last.isEmpty()) {
            int pos = last.indexOf('/');
            if (pos > 0) {
                Long idFirst = Long.parseLong(last.substring(0, pos));
                Long idSecond = Long.parseLong(last.substring(pos + 1));
                return rewriteNestedById(exchange, idFirst, idSecond);
            }
            return new HttpResponse(400, "Отсутствует второй идентификатор.");
        }
        return new HttpResponse(400, "Отсутствует первый идентификатор.");
    }

    public HttpResponse delete(HttpExchange exchange) throws SQLException {
        String start = exchange.getHttpContext().getPath();
        String request = exchange.getRequestURI().getPath();
        if (!request.startsWith(start)) {
            String message = "Неверный путь.";
            log.severe(message);
            return new HttpResponse(500, message);
        }
        String last = request.substring(start.length());
        if (!last.isEmpty()) {
            int pos = last.indexOf('/');
            if (pos > 0) {
                Long idFirst = Long.parseLong(last.substring(0, pos));
                Long idSecond = Long.parseLong(last.substring(pos + 1));
                return deleteNestedById(exchange, idFirst, idSecond);
            }
            Long id = Long.parseLong(last);
            return deleteNestedAll(exchange, id);
        }
        return new HttpResponse(400, "Отсутствует первый идентификатор.");
    }

    public abstract HttpResponse createNested(HttpExchange exchange, Long id) throws IOException, SQLException;

    public abstract HttpResponse readNestedById(HttpExchange exchange, Long idFirst, Long idSecond) throws SQLException;

    public abstract HttpResponse readNestedAll(HttpExchange exchange, Long id) throws SQLException;

    public abstract HttpResponse rewriteNestedById(HttpExchange exchange, Long idFirst, Long idSecond) throws IOException, SQLException;

    //public abstract HttpResponse modifyById(HttpExchange exchange, Long id) throws IOException, SQLException;

    public abstract HttpResponse deleteNestedById(HttpExchange exchange, Long idFirst, Long idSecond) throws SQLException;

    public abstract HttpResponse deleteNestedAll(HttpExchange exchange, Long id) throws SQLException;

}
