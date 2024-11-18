package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.FeedDatabase;
import com.bio.database.SolutionDatabase;
import com.bio.database.SolutionReactiveDatabase;
import com.bio.entity.Feed;
import com.bio.entity.Solution;
import com.bio.entity.SolutionReactive;
import com.bio.service.EntityNotFoundException;
import com.bio.service.FeedService;
import com.bio.service.SolutionService;
import com.bio.value.FeedValue;
import com.bio.value.SolutionValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FeedHandler extends HandlerCRUD {

    private static Logger log = Logger.getLogger(FeedHandler.class.getName());

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        FeedValue feedValue = mapper.readValue(request, FeedValue.class);
        StringBuffer error = new StringBuffer();
        String name = feedValue.getSolution().getName();
        if (name == null) {
            error.append("Не указано название.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        boolean exist = FeedService.existByName(name);
        if (exist) {
            return new HttpResponse(400, "Питательная среда \"" + name + "\" уже существует.");
        }
        Feed baseFeed = null;
        Long baseFeedId = feedValue.getBase();
        if (baseFeedId != null) {
            try {
                baseFeed = FeedService.getById(baseFeedId);
            }
            catch (EntityNotFoundException e) {
                String message = e.getMessage();
                log.info(message);
                return new HttpResponse(200, message);
            }
        }
        Connection connection = Bio.database.getConnection();
        // solution
        SolutionValue solutionValue = feedValue.getSolution();
        Solution solution = SolutionDatabase.insert(connection, solutionValue);
        // feed
        Feed feed = FeedDatabase.insert(connection, feedValue, solution.getId());
        if (baseFeed != null) {
            List<SolutionReactive> solutionReactives = baseFeed.getSolution().getReactives();
            if ((solutionReactives != null) && (solutionReactives.size() > 0)) {
                try (PreparedStatement statementSolutionReactive = connection.prepareStatement(SolutionReactiveDatabase.insert)) {
                    Long solutionTo = solution.getId();
                    for (SolutionReactive solutionReactive : solutionReactives) {
                        SolutionReactiveDatabase.prepareInsert(statementSolutionReactive, solutionTo, solutionReactive);
                        int rc = statementSolutionReactive.executeUpdate();
                    }
                }
                solution.setReactives(solutionReactives);
            }
        }
        feed.setSolution(solution);
        connection.commit();
        String message = String.format("Питательная среда %d добавлена.", feed.getId());
        log.info(message);
        return new HttpResponse(200, feed);
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement()) {
            ArrayList<Feed> feeds = new ArrayList<>();
            ResultSet rs = statement.executeQuery(FeedDatabase.select);
            while (rs.next()) {
                Feed feed = FeedDatabase.get(rs);
                // solution
                try {
                    Long solutionId = feed.getSolution().getId();
                    Solution solution = SolutionService.getById(solutionId);
                    feed.setSolution(solution);
                }
                catch (EntityNotFoundException e) {
                    String message = e.getMessage();
                    log.info(message);
                    return new HttpResponse(404, message);
                }
                feeds.add(feed);
            }
            return new HttpResponse(200, feeds.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        try {
            Feed feed = FeedService.getById(id);
            return new HttpResponse(200, feed);
        } catch (EntityNotFoundException e) {
            String message = e.getMessage();
            log.info(message);
            return new HttpResponse(404, message);
        }
    }

    /*
    public HttpResponse updateById(HttpExchange exchange, Long id) throws IOException, SQLException {
        return null;
    }
    */

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FeedDatabase.deleteById)) {
            FeedDatabase.prepareDeleteById(statement, id);
            // TODO delete corresponding solutions
            int code;
            String message;
            if (statement.executeUpdate() > 0) {
                code = 200;
                message = String.format("Питательная среда %d удалена.", id);
            }
            else {
                code = 404;
                message = String.format("Питательная среда %d не найдена.", id);
            }
            connection.commit();
            log.info(message);
            return new HttpResponse(code, message);
        }
    }
}
