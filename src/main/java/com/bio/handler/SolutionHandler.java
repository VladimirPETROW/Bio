package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.FeedDatabase;
import com.bio.database.SolutionDatabase;
import com.bio.database.SolutionMaterialDatabase;
import com.bio.database.SolutionReactiveDatabase;
import com.bio.entity.Solution;
import com.bio.entity.SolutionMaterial;
import com.bio.entity.SolutionReactive;
import com.bio.value.SolutionValue;
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
public class SolutionHandler extends HandlerCRUD {

    public HttpResponse create(HttpExchange exchange) throws IOException, SQLException {
        InputStream input = exchange.getRequestBody();
        String request = Handlers.readBytes(input).toString();
        ObjectMapper mapper = new ObjectMapper();
        //mapper.registerModule(new JavaTimeModule());
        SolutionValue solutionValue = mapper.readValue(request, SolutionValue.class);
        StringBuffer error = new StringBuffer();
        if (solutionValue.getName() == null) {
            error.append("Не указано название.");
        }
        if (error.length() > 0) {
            return new HttpResponse(400, error.toString());
        }
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(SolutionDatabase.insert);
             PreparedStatement statementReactive = Bio.database.prepareStatement(SolutionReactiveDatabase.insert);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(SolutionMaterialDatabase.insert)) {
            SolutionDatabase.prepareInsert(statement, solutionValue);
            ResultSet rs = statement.executeQuery();
            rs.next();
            Long id = rs.getLong(1);
            List<SolutionReactive> solutionReactives = solutionValue.getReactives();
            if (solutionReactives != null) {
                for (SolutionReactive solutionReactive : solutionReactives) {
                    SolutionReactiveDatabase.prepareInsert(statementReactive, id, solutionReactive);
                    statementReactive.executeUpdate();
                }
            }
            List<SolutionMaterial> solutionMaterials = solutionValue.getMaterials();
            if (solutionMaterials != null) {
                for (SolutionMaterial solutionMaterial : solutionMaterials) {
                    SolutionMaterialDatabase.prepareInsert(statementMaterial, id, solutionMaterial);
                    statementMaterial.executeUpdate();
                }
            }
            Bio.database.commit();
            String message = String.format("Раствор %d добавлен.", id);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Bio.database.rollback();
        try (Statement statement = Bio.database.createStatement();
             PreparedStatement statementReactive = Bio.database.prepareStatement(SolutionReactiveDatabase.selectByFeed);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(SolutionMaterialDatabase.selectByFeed)) {
            ArrayList<Solution> solutions = new ArrayList<>();
            ResultSet rs = statement.executeQuery(SolutionDatabase.select);
            while (rs.next()) {
                Solution solution = SolutionDatabase.get(rs);
                Long id = solution.getId();
                // reactives
                SolutionReactiveDatabase.prepareSelectBySolution(statementReactive, id);
                ResultSet rsReactive = statementReactive.executeQuery();
                ArrayList<SolutionReactive> solutionReactives = new ArrayList<>();
                while (rsReactive.next()) {
                    solutionReactives.add(SolutionReactiveDatabase.get(rsReactive));
                }
                if (solutionReactives.size() > 0) {
                    solution.setReactives(solutionReactives);
                }
                // materials
                SolutionMaterialDatabase.prepareSelectBySolution(statementMaterial, id);
                ResultSet rsMaterial = statementMaterial.executeQuery();
                ArrayList<SolutionMaterial> solutionMaterials = new ArrayList<>();
                while (rsMaterial.next()) {
                    solutionMaterials.add(SolutionMaterialDatabase.get(rsMaterial));
                }
                if (solutionMaterials.size() > 0) {
                    solution.setMaterials(solutionMaterials);
                }
                solutions.add(solution);
            }
            return new HttpResponse(200, solutions.toArray());
        }
    }

    public HttpResponse readById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(SolutionDatabase.selectById);
             PreparedStatement statementReactive = Bio.database.prepareStatement(SolutionReactiveDatabase.selectByFeed);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(SolutionMaterialDatabase.selectByFeed)) {
            FeedDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Раствор %d не найден.", id);
                log.info(message);
                return new HttpResponse(200, message);
            }
            Solution solution = SolutionDatabase.get(rs);
            // reactives
            SolutionReactiveDatabase.prepareSelectBySolution(statementReactive, id);
            ResultSet rsReactive = statementReactive.executeQuery();
            ArrayList<SolutionReactive> solutionReactives = new ArrayList<>();
            while (rsReactive.next()) {
                solutionReactives.add(SolutionReactiveDatabase.get(rsReactive));
            }
            if (solutionReactives.size() > 0) {
                solution.setReactives(solutionReactives);
            }
            // materials
            SolutionMaterialDatabase.prepareSelectBySolution(statementMaterial, id);
            ResultSet rsMaterial = statementMaterial.executeQuery();
            ArrayList<SolutionMaterial> solutionMaterials = new ArrayList<>();
            while (rsMaterial.next()) {
                solutionMaterials.add(SolutionMaterialDatabase.get(rsMaterial));
            }
            if (solutionMaterials.size() > 0) {
                solution.setMaterials(solutionMaterials);
            }
            return new HttpResponse(200, solution);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Bio.database.rollback();
        try (PreparedStatement statement = Bio.database.prepareStatement(SolutionDatabase.deleteById);
             PreparedStatement statementReactive = Bio.database.prepareStatement(SolutionReactiveDatabase.deleteByFeed);
             PreparedStatement statementMaterial = Bio.database.prepareStatement(SolutionMaterialDatabase.deleteByFeed)) {
            SolutionDatabase.prepareDeleteById(statement, id);
            String message;
            if (statement.executeUpdate() > 0) {
                message = String.format("Раствор %d удален.", id);
            }
            else {
                message = String.format("Раствор %d не найден.", id);
            }
            // reactives
            SolutionReactiveDatabase.prepareDeleteBySolution(statementReactive, id);
            statementReactive.executeUpdate();
            // materials
            SolutionMaterialDatabase.prepareDeleteBySolution(statementMaterial, id);
            statementMaterial.executeUpdate();
            Bio.database.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
