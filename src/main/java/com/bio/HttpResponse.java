package com.bio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.Headers;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    public int code;
    public Object message;
    HashMap<String, String> headers;

    private static Logger log = Logger.getLogger(HttpResponse.class.getName());

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
            case 404:
                message = "Not Found";
                break;
            case 405:
                message = "Method Not Allowed";
                break;
            case 500:
                message = "Internal Server Error";
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
