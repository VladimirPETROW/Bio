package com.bio.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class Handlers {

    private static Logger log = Logger.getLogger(Handlers.class.getName());

    public static void sendText(int code, String text, HttpExchange exchange) throws IOException {
        byte[] bytes = text.getBytes();
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream out = exchange.getResponseBody();
        out.write(bytes);
        out.close();
    }

    public static ByteArrayOutputStream readBytes(InputStream input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(input.available());
        int size = 1024;
        byte[] buffer = new byte[size];
        int count;
        while ((count = input.read(buffer)) > 0) {
            bytes.write(buffer, 0, count);
        }
        return bytes;
    }

}
