package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.ExperimentDatabase;
import com.bio.entity.Experiment;
import com.bio.entity.Feed;
import com.bio.service.EntityNotFoundException;
import com.bio.service.FeedService;
import com.bio.value.ExperimentValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ExperimentHandler extends HandlerCRUD {

    private static Logger log = Logger.getLogger(ExperimentHandler.class.getName());

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ExperimentValue experimentValue = mapper.readValue(request, ExperimentValue.class);
        StringBuffer error = new StringBuffer();
        /*
        if (experimentValue.getOrganism() == null) {
            error.append("Не указан организм.");
        }
        */
        if (experimentValue.getFeed() == null) {
            if (error.length() > 0) {
                error.append(" ");
            }
            error.append("Не указана питательная среда.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Connection connection = Bio.database.getConnection();
        Experiment experiment = ExperimentDatabase.insert(connection, experimentValue);
        connection.commit();
        String message = String.format("Эксперимент %d добавлен.", experiment.getId());
        log.info(message);
        return new HttpResponse(200, experiment);
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement()) {
            ArrayList<Experiment> experiments = new ArrayList<>();
            ResultSet rs = statement.executeQuery(ExperimentDatabase.select);
            while (rs.next()) {
                Experiment experiment = ExperimentDatabase.get(rs);
                // feed
                try {
                    Long feedId = experiment.getFeed().getId();
                    Feed feed = FeedService.getById(feedId);
                    experiment.setFeed(feed);
                }
                catch (EntityNotFoundException e) {
                    String message = e.getMessage();
                    log.info(message);
                    return new HttpResponse(404, message);
                }
                experiments.add(experiment);
            }
            return new HttpResponse(200, experiments.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(ExperimentDatabase.selectById)) {
            ExperimentDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Эксперимент %d не найден.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Experiment experiment = ExperimentDatabase.get(rs);
            // feed
            try {
                Long feedId = experiment.getFeed().getId();
                Feed feed = FeedService.getById(feedId);
                experiment.setFeed(feed);
            }
            catch (EntityNotFoundException e) {
                String message = e.getMessage();
                log.info(message);
                return new HttpResponse(404, message);
            }
            return new HttpResponse(200, experiment);
        }
    }

    /*
    public HttpResponse updateById(HttpExchange exchange, Long id) throws IOException, SQLException {
        return null;
    }
    */

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(ExperimentDatabase.deleteById)) {
            ExperimentDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Эксперимент %d удален.", id);
            }
            else {
                message = String.format("Эксперимент %d не найден.", id);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
