package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.ExperimentDatabase;
import com.bio.database.OrganismDatabase;
import com.bio.entity.Experiment;
import com.bio.entity.Organism;
import com.bio.value.ExperimentValue;
import com.bio.value.OrganismValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

@Log
public class ExperimentHandler extends HandlerCRUD {

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        ExperimentValue experimentValue = mapper.readValue(request, ExperimentValue.class);
        StringBuffer error = new StringBuffer();
        if (experimentValue.getOrganism() == null) {
            error.append("Не указан организм.");
        }
        if (experimentValue.getFeed() == null) {
            if (error.length() > 0) {
                error.append(" ");
            }
            error.append("Не указана питательная среда.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(ExperimentDatabase.insert)) {
            ExperimentDatabase.prepareInsert(statement, experimentValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            Long id = rs.getLong(1);
            Bio.database.commit();
            String message = String.format("Эксперимент %d добавлен.", id);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Bio.database.rollback();
        try (Statement statement = Bio.database.createStatement()) {
            ArrayList<Experiment> experiments = new ArrayList<>();
            ResultSet rs = statement.executeQuery(ExperimentDatabase.select);
            while (rs.next()) {
                Experiment experiment = ExperimentDatabase.get(rs);
                experiments.add(experiment);
            }
            return new HttpResponse(200, experiments.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(ExperimentDatabase.selectById)) {
            ExperimentDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Эксперимент %d не найден.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Experiment experiment = ExperimentDatabase.get(rs);
            return new HttpResponse(200, experiment);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(ExperimentDatabase.deleteById)) {
            ExperimentDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Эксперимент %d удален.", id);
            }
            else {
                message = String.format("Эксперимент %d не найден.", id);
            }
            Bio.database.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
