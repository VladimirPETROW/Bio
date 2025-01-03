package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.OrganismDatabase;
import com.bio.entity.Organism;
import com.bio.value.OrganismValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class OrganismHandler extends HandlerCRUD {

    private static Logger log = Logger.getLogger(OrganismHandler.class.getName());

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
        Connection connection = Bio.database.getConnection();
        Organism organism = OrganismDatabase.insert(connection, organismValue);
        connection.commit();
        String message = String.format("Организм %d добавлен.", organism.getId());
        log.info(message);
        return new HttpResponse(200, organism);
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement()) {
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
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(OrganismDatabase.selectById)) {
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

    public HttpResponse rewriteById(HttpExchange exchange, Long id) throws IOException, SQLException {
        return null;
    }

    /*
    public HttpResponse modifyById(HttpExchange exchange, Long id) throws IOException, SQLException {
        return null;
    }
    */

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(OrganismDatabase.deleteById)) {
            OrganismDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Организм %d удален.", id);
            }
            else {
                message = String.format("Организм %d не найден.", id);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
