package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpMethod;
import com.bio.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler {

    private static Logger log = Logger.getLogger(Handler.class.getName());

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

    public void handleViewItem(HttpMethod method, String view, HttpExchange exchange, Function<HttpResponse, HttpResponse> formatter) throws IOException {
        HttpResponse response;
        if (!exchange.getRequestMethod().equals(method.toString())) {
            response = HttpResponse.createResponse(405);
        }
        else {
            try {
                String start = exchange.getHttpContext().getPath();
                String request = exchange.getRequestURI().getPath();
                if (!request.startsWith(start)) {
                    String message = "Неверный путь.";
                    log.severe(message);
                    response = new HttpResponse(500, message);
                }
                String last = request.substring(start.length());
                if (!last.isEmpty()) {
                    Long id = Long.parseLong(last);
                    String textModel = "\t<script>\n\t\t" + view + "Id = " + id + ";\n\t</script>\n";
                    InputStream resourceBegin, resourceEnd;
                    String resourcesWhere = Bio.properties.getProperty("server.resources");
                    if (resourcesWhere != null) {
                        resourceBegin = Files.newInputStream(Paths.get(resourcesWhere, view + "ItemBegin.html"));
                        resourceEnd = Files.newInputStream(Paths.get(resourcesWhere, view + "ItemEnd.html"));
                    }
                    else {
                        ClassLoader loader = this.getClass().getClassLoader();
                        resourceBegin = loader.getResourceAsStream(view + "ItemBegin.html");
                        if (resourceBegin == null) {
                            throw new Exception(view + "ItemBegin.html not found.");
                        }
                        resourceEnd = loader.getResourceAsStream(view + "ItemEnd.html");
                        if (resourceEnd == null) {
                            throw new Exception(view + "ItemEnd.html not found.");
                        }
                    }
                    String textBegin = Handlers.readBytes(resourceBegin).toString();
                    String textEnd = Handlers.readBytes(resourceEnd).toString();
                    String text = textBegin + textModel + textEnd;
                    response = new HttpResponse(200, text);
                }
                else {
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
                }
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
                Path resourcePath = Paths.get(resourcesWhere, "static", last);
                if (!Files.exists(resourcePath)) {
                    return HttpResponse.createResponse(400);
                }
                resource = Files.newInputStream(resourcePath);
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

    public HttpResponse program(HttpExchange exchange) {
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
            return new HttpResponse(200, Bio.program);
        }
        else {
            return HttpResponse.createResponse(400);
        }
    }

    public HttpResponse stop(HttpExchange exchange) {
        try {
            Bio.stopServer();
        } catch (Throwable e) {
            //throw new RuntimeException(e);
        }
        try {
            Bio.database.close();
        } catch (Throwable e) {
            //throw new RuntimeException(e);
        }
        System.exit(0);
        return null;
    }

}
