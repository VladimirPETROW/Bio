package com.bio.handler;

import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Log
public class Handlers {

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
