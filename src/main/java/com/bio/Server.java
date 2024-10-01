package com.bio;

import com.bio.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.function.Function;

public class Server {

    HttpServer httpServer;
    Handler handler;

    HashMap<String, HashMap<HttpMethod, Handlers>> contexts;

    public Server() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress("localhost", 80), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        handler = new Handler();
    }

    void createContexts() {
        contexts = new HashMap<>();
        addContext("/", HttpMethod.GET, handler::root, HttpResponse::asText);
        addContextView("/organism/", HttpMethod.GET, "organism", HttpResponse::asText);
        addContextView("/reactive/", HttpMethod.GET, "reactive", HttpResponse::asText);
        addContextView("/material/", HttpMethod.GET, "material", HttpResponse::asText);
        addContextView("/solution/", HttpMethod.GET, "solution", HttpResponse::asText);
        addContextView("/feed/", HttpMethod.GET, "feed", HttpResponse::asText);
        addContextView("/history/", HttpMethod.GET, "history", HttpResponse::asText);
        addContextView("/experiment/", HttpMethod.GET, "experiment", HttpResponse::asText);
        addContextCRUD("/api/organism/", new OrganismHandler(), HttpResponse::asJson);
        addContextCRUD("/api/reactive/", new ReactiveHandler(), HttpResponse::asJson);
        addContextCRUD("/api/material/", new MaterialHandler(), HttpResponse::asJson);
        addContextCRUD("/api/feed/", new FeedHandler(), HttpResponse::asJson);
        addContextCRUD("/api/experiment/", new ExperimentHandler(), HttpResponse::asJson);
    }

    void addContext(String path, HttpMethod method, Worker worker, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handler.handleMethod(method, worker, exchange, formatter));
    }

    void addContextView(String path, HttpMethod method, String view, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handler.handleView(method, view, exchange, formatter));
    }

    void addContextCRUD(String path, HandlerCRUD handlerCRUD, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handlerCRUD.handleCRUD(exchange, formatter));
    }

    void start() {
        httpServer.start();
    }

}
