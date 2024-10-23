package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.MaterialDatabase;
import com.bio.entity.Material;
import com.bio.value.MaterialValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

@Log
public class MaterialHandler extends HandlerCRUD {

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        MaterialValue materialValue = mapper.readValue(request, MaterialValue.class);
        StringBuffer error = new StringBuffer();
        if (materialValue.getName() == null) {
            error.append("Не указано название.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(MaterialDatabase.insert)) {
            MaterialDatabase.prepareInsert(statement, materialValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            Long id = rs.getLong(1);
            connection.commit();
            String message = String.format("Сырье %d добавлено.", id);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement()) {
            ArrayList<Material> materials = new ArrayList<>();
            ResultSet rs = statement.executeQuery(MaterialDatabase.select);
            while (rs.next()) {
                Material material = MaterialDatabase.get(rs);
                materials.add(material);
            }
            return new HttpResponse(200, materials.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(MaterialDatabase.selectById)) {
            MaterialDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Сырье %d не найдено.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Material material = MaterialDatabase.get(rs);
            return new HttpResponse(200, material);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(MaterialDatabase.deleteById)) {
            MaterialDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Сырье %d удалено.", id);
            }
            else {
                message = String.format("Сырье %d не найдено.", id);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
