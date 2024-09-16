package com.bio.handler;

import com.bio.HttpResponse;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.SQLException;

public interface Worker {
    HttpResponse process(HttpExchange exchange) throws IOException, SQLException;
}

