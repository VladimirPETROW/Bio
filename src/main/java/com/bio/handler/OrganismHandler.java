package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.OrganismDatabase;
import com.bio.entity.Organism;
import com.bio.value.OrganismValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

@Log
public class OrganismHandler extends HandlerCRUD {

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        OrganismValue organismValue = mapper.readValue(request, OrganismValue.class);
        StringBuffer error = new StringBuffer();
        if ((organismValue.getName() == null) || (organismValue.getName().trim().isEmpty())) {
            error.append("Не указано название.");
        }
        if (organismValue.getDoubling() == null) {
            if (error.length() > 0) {
                error.append(" ");
            }
            error.append("Не указана скорость удвоения.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(OrganismDatabase.insert)) {
            OrganismDatabase.prepareInsert(statement, organismValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            //Long id = rs.getLong(1);
            Organism organism = OrganismDatabase.get(rs);
            Bio.database.commit();
            String message = String.format("Организм %d добавлен.", organism.getId());
            log.info(message);
            return new HttpResponse(200, organism);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Bio.database.rollback();
        try (Statement statement = Bio.database.createStatement()) {
            ArrayList<Organism> organisms = new ArrayList<>();
            ResultSet rs = statement.executeQuery(OrganismDatabase.select);
            while (rs.next()) {
                Organism organism = OrganismDatabase.get(rs);
                organisms.add(organism);
            }
            return new HttpResponse(200, organisms.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(OrganismDatabase.selectById)) {
            OrganismDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Организм %d не найден.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Organism organism = OrganismDatabase.get(rs);
            return new HttpResponse(200, organism);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(OrganismDatabase.deleteById)) {
            OrganismDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Организм %d удален.", id);
            }
            else {
                message = String.format("Организм %d не найден.", id);
            }
            Bio.database.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
