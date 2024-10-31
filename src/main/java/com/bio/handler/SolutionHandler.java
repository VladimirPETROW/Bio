package com.bio.handler;

import com.bio.Bio;
import com.bio.HttpResponse;
import com.bio.database.SolutionDatabase;
import com.bio.database.SolutionMaterialDatabase;
import com.bio.database.SolutionReactiveDatabase;
import com.bio.entity.Solution;
import com.bio.entity.SolutionMaterial;
import com.bio.entity.SolutionReactive;
import com.bio.service.EntityNotFoundException;
import com.bio.service.SolutionService;
import com.bio.value.SolutionValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.java.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
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
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SolutionDatabase.insert);
             PreparedStatement statementReactive = connection.prepareStatement(SolutionReactiveDatabase.insert);
             PreparedStatement statementMaterial = connection.prepareStatement(SolutionMaterialDatabase.insert)) {
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
            connection.commit();
            String message = String.format("Раствор %d добавлен.", id);
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse readAll(HttpExchange exchange) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (Statement statement = connection.createStatement();
             PreparedStatement statementReactive = connection.prepareStatement(SolutionReactiveDatabase.selectBySolution);
             PreparedStatement statementMaterial = connection.prepareStatement(SolutionMaterialDatabase.selectBySolution)) {
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
        try {
            Solution solution = SolutionService.getById(id);
            return new HttpResponse(200, solution);
        } catch (EntityNotFoundException e) {
            String message = e.getMessage();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }

    public HttpResponse deleteById(HttpExchange exchange, Long id) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SolutionDatabase.deleteById);
             PreparedStatement statementReactive = connection.prepareStatement(SolutionReactiveDatabase.deleteBySolution);
             PreparedStatement statementMaterial = connection.prepareStatement(SolutionMaterialDatabase.deleteBySolution)) {
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
            connection.commit();
            log.info(message);
            return new HttpResponse(200, message);
        }
    }
}
