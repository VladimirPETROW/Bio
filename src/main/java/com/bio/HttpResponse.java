package com.bio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.Headers;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Log
public class HttpResponse {
    public int code;
    public Object message;
    HashMap<String, String> headers;

    public HttpResponse(int c, Object m) {
        code = c;
        message = m;
        headers = new HashMap<>();
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void headersTo(Headers h) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            h.add(entry.getKey(), entry.getValue());
        }
    }

    public static HttpResponse createResponse(int code) {
        String message = "";
        switch (code) {
            case 400:
                message = "Bad Request";
                break;
            case 405:
                message = "Method Not Allowed";
                break;
        }
        return new HttpResponse(code, message);
    }

    public static HttpResponse asJson(HttpResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (response.message instanceof String) {
                response.message = mapper.writeValueAsString(response);
            } else {
                mapper.registerModule(new JavaTimeModule());
                response.message = mapper.writeValueAsString(response.message);
            }
        } catch (JsonProcessingException e) {
            String message = "Ошибка сериализации.";
            log.log(Level.SEVERE, message, e);
            int code = 500;
            return new HttpResponse(code, String.format("{\"code\":%d,\"message\":\"%s\"}", code, message));
        }
        response.setHeader("Content-Type", "text/json");
        return response;
    }

    public static HttpResponse asText(HttpResponse response) {
        return response;
    }

}
