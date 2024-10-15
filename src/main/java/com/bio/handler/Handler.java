package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpMethod;
import com.bio.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Level;

@Log
public class Handler {

    public void handleMethod(HttpMethod method, Worker worker, HttpExchange exchange, Function<HttpResponse, HttpResponse> formatter) throws IOException {
        HttpResponse response;
        if (!exchange.getRequestMethod().equals(method.toString())) {
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

    public void handleView(HttpMethod method, String view, HttpExchange exchange, Function<HttpResponse, HttpResponse> formatter) throws IOException {
        HttpResponse response;
        if (!exchange.getRequestMethod().equals(method.toString())) {
            response = HttpResponse.createResponse(405);
        }
        else {
            try {
                InputStream resource;
                String resourcesWhere = Bio.properties.getProperty("server.resources");
                if (resourcesWhere != null) {
                    resource = Files.newInputStream(Paths.get(resourcesWhere, view + ".html"));
                }
                else {
                    ClassLoader loader = this.getClass().getClassLoader();
                    resource = loader.getResourceAsStream(view + ".html");
                    if (resource == null) {
                        throw new Exception(view + ".html not found.");
                    }
                }
                String text = Handlers.readBytes(resource).toString();
                response = new HttpResponse(200, text);
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

    public HttpResponse root(HttpExchange exchange) throws IOException {
        String start = exchange.getHttpContext().getPath();
        String request = exchange.getRequestURI().getPath();
        if (!request.startsWith(start)) {
            String message = "Неверный путь.";
            log.severe(message);
            return new HttpResponse(500, message);
        }
        String last = request.substring(start.length());
        InputStream resource;
        String resourcesWhere = Bio.properties.getProperty("server.resources");
        if (last.isEmpty()) {
            if (resourcesWhere != null) {
                resource = Files.newInputStream(Paths.get(resourcesWhere, "organism.html"));
            }
            else {
                ClassLoader loader = this.getClass().getClassLoader();
                resource = loader.getResourceAsStream("organism.html");
                if (resource == null) {
                    return HttpResponse.createResponse(400);
                }
            }
        }
        else {
            if (resourcesWhere != null) {
                resource = Files.newInputStream(Paths.get(resourcesWhere, "static", last));
            }
            else {
                ClassLoader loader = this.getClass().getClassLoader();
                resource = loader.getResourceAsStream("static/" + last);
                if (resource == null) {
                    return HttpResponse.createResponse(400);
                }
            }
        }
        String text = Handlers.readBytes(resource).toString();
        return new HttpResponse(200, text);
    }

}
