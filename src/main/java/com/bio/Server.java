package com.bio;

import com.bio.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.function.Function;

public class Server {

    String host;
    int port;
    HttpServer httpServer;
    Handler handler;

    HashMap<String, HashMap<HttpMethod, Handlers>> contexts;

    public static int defaultServerPort = 80;

    public Server() {
        try {
            host = Bio.properties.getProperty("server.host", "localhost");
            String portProperty = Bio.properties.getProperty("server.port");
            port = (portProperty != null) ? Integer.parseInt(portProperty) : defaultServerPort;
            httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
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
        addContextViewItem("/feed/", HttpMethod.GET, "feed", HttpResponse::asText);
        addContextView("/history/", HttpMethod.GET, "history", HttpResponse::asText);
        addContextViewItem("/experiment/", HttpMethod.GET, "experiment", HttpResponse::asText);
        addContext("/api/program/", HttpMethod.GET, handler::program, HttpResponse::asJson);
        addContext("/api/program/stop", HttpMethod.POST, handler::stop, HttpResponse::asJson);
        addContextCRUD("/api/organism/", new OrganismHandler(), HttpResponse::asJson);
        addContextCRUD("/api/reactive/", new ReactiveHandler(), HttpResponse::asJson);
        addContextCRUD("/api/material/", new MaterialHandler(), HttpResponse::asJson);
        addContextCRUD("/api/solution/", new SolutionHandler(), HttpResponse::asJson);
        addContextNestedCRUD("/api/solutionReactive/", new SolutionReactiveHandler(), HttpResponse::asJson);
        addContextCRUD("/api/feed/", new FeedHandler(), HttpResponse::asJson);
        addContextCRUD("/api/experiment/", new ExperimentHandler(), HttpResponse::asJson);
    }

    void addContext(String path, HttpMethod method, Worker worker, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handler.handleMethod(method, worker, exchange, formatter));
    }

    void addContextView(String path, HttpMethod method, String view, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handler.handleView(method, view, exchange, formatter));
    }

    void addContextViewItem(String path, HttpMethod method, String view, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handler.handleViewItem(method, view, exchange, formatter));
    }

    void addContextCRUD(String path, HandlerCRUD handlerCRUD, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handlerCRUD.handleCRUD(exchange, formatter));
    }

    void addContextNestedCRUD(String path, HandlerNestedCRUD handlerNestedCRUD, Function<HttpResponse, HttpResponse> formatter) {
        httpServer.createContext(path, exchange -> handlerNestedCRUD.handleNestedCRUD(exchange, formatter));
    }

    void start() {
        httpServer.start();
    }

    void stop() {
        httpServer.stop(0);
    }
}
