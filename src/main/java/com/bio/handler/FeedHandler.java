package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.FeedDatabase;
import com.bio.database.FeedMaterialDatabase;
import com.bio.database.FeedReactiveDatabase;
import com.bio.database.ReactiveDatabase;
import com.bio.entity.Feed;
import com.bio.entity.FeedMaterial;
import com.bio.entity.FeedReactive;
import com.bio.entity.Reactive;
import com.bio.value.FeedValue;
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
import java.util.List;

@Log
public class FeedHandler extends HandlerCRUD {

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        FeedValue feedValue = mapper.readValue(request, FeedValue.class);
        StringBuffer error = new StringBuffer();
        if (feedValue.getName() == null) {
            error.append("Не указано название.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(FeedDatabase.insert);
             PreparedStatement statementReactive = Bio.database.prepareStatement(FeedReactiveDatabase.insert);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(FeedMaterialDatabase.insert)) {
            FeedDatabase.prepareInsert(statement, feedValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            Long id = rs.getLong(1);
            List<FeedReactive> feedReactives = feedValue.getReactives();
            if (feedReactives != null) {
                for (FeedReactive feedReactive : feedReactives) {
                    FeedReactiveDatabase.prepareInsert(statementReactive, id, feedReactive);
                    statementReactive.executeUpdate();
                }
            }
            List<FeedMaterial> feedMaterials = feedValue.getMaterials();
            if (feedMaterials != null) {
                for (FeedMaterial feedMaterial : feedMaterials) {
                    FeedMaterialDatabase.prepareInsert(statementMaterial, id, feedMaterial);
                    statementMaterial.executeUpdate();
                }
            }
            Bio.database.commit();
            String message = String.format("Питательная среда %d добавлена.", id);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Bio.database.rollback();
        try (Statement statement = Bio.database.createStatement();
             PreparedStatement statementReactive = Bio.database.prepareStatement(FeedReactiveDatabase.selectByFeed);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(FeedMaterialDatabase.selectByFeed)) {
            ArrayList<Feed> feeds = new ArrayList<>();
            ResultSet rs = statement.executeQuery(FeedDatabase.select);
            while (rs.next()) {
                Feed feed = FeedDatabase.get(rs);
                Long id = feed.getId();
                // reactives
                FeedReactiveDatabase.prepareSelectByFeed(statementReactive, id);
                ResultSet rsReactive = statementReactive.executeQuery();
                ArrayList<FeedReactive> feedReactives = new ArrayList<>();
                while (rsReactive.next()) {
                    feedReactives.add(FeedReactiveDatabase.get(rsReactive));
                }
                if (feedReactives.size() > 0) {
                    feed.setReactives(feedReactives);
                }
                // materials
                FeedMaterialDatabase.prepareSelectByFeed(statementMaterial, id);
                ResultSet rsMaterial = statementMaterial.executeQuery();
                ArrayList<FeedMaterial> feedMaterials = new ArrayList<>();
                while (rsMaterial.next()) {
                    feedMaterials.add(FeedMaterialDatabase.get(rsMaterial));
                }
                if (feedMaterials.size() > 0) {
                    feed.setMaterials(feedMaterials);
                }
                feeds.add(feed);
            }
            return new HttpResponse(200, feeds.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(FeedDatabase.selectById);
             PreparedStatement statementReactive = Bio.database.prepareStatement(FeedReactiveDatabase.selectByFeed);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(FeedMaterialDatabase.selectByFeed)) {
            FeedDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Питательная среда %d не найдена.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Feed feed = FeedDatabase.get(rs);
            // reactives
            FeedReactiveDatabase.prepareSelectByFeed(statementReactive, id);
            ResultSet rsReactive = statementReactive.executeQuery();
            ArrayList<FeedReactive> feedReactives = new ArrayList<>();
            while (rsReactive.next()) {
                feedReactives.add(FeedReactiveDatabase.get(rsReactive));
            }
            if (feedReactives.size() > 0) {
                feed.setReactives(feedReactives);
            }
            // materials
            FeedMaterialDatabase.prepareSelectByFeed(statementMaterial, id);
            ResultSet rsMaterial = statementMaterial.executeQuery();
            ArrayList<FeedMaterial> feedMaterials = new ArrayList<>();
            while (rsMaterial.next()) {
                feedMaterials.add(FeedMaterialDatabase.get(rsMaterial));
            }
            if (feedMaterials.size() > 0) {
                feed.setMaterials(feedMaterials);
            }
            return new HttpResponse(200, feed);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(FeedDatabase.deleteById);
             PreparedStatement statementReactive = Bio.database.prepareStatement(FeedReactiveDatabase.deleteByFeed);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(FeedMaterialDatabase.deleteByFeed)) {
            FeedDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Питательная среда %d удалена.", id);
            }
            else {
                message = String.format("Питательная среда %d не найдена.", id);
            }
            // reactives
            FeedReactiveDatabase.prepareDeleteByFeed(statementReactive, id);
            statementReactive.executeUpdate();
            // materials
            FeedMaterialDatabase.prepareDeleteByFeed(statementMaterial, id);
            statementMaterial.executeUpdate();
            Bio.database.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
