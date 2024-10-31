package com.bio.service;

import com.bio.Bio;
import com.bio.database.*;
import com.bio.entity.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SolutionService {

    public static Solution getById(Long id) throws SQLException, EntityNotFoundException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SolutionDatabase.selectById);
             PreparedStatement statementSolutionReactive = connection.prepareStatement(SolutionReactiveDatabase.selectBySolution);
             PreparedStatement statementReactive = connection.prepareStatement(ReactiveDatabase.selectById);
             PreparedStatement statementMaterial = connection.prepareStatement(SolutionMaterialDatabase.selectBySolution)) {
            FeedDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Раствор %d не найден.", id);
                throw new EntityNotFoundException(message);
            }
            Solution solution = SolutionDatabase.get(rs);
            // reactives
            SolutionReactiveDatabase.prepareSelectBySolution(statementSolutionReactive, id);
            ResultSet rsSolutionReactive = statementSolutionReactive.executeQuery();
            ArrayList<SolutionReactive> solutionReactives = new ArrayList<>();
            while (rsSolutionReactive.next()) {
                SolutionReactive solutionReactive = SolutionReactiveDatabase.get(rsSolutionReactive);
                ReactiveDatabase.prepareSelectById(statementReactive, solutionReactive.getReactive().getId());
                ResultSet rsReactive = statementReactive.executeQuery();
                rsReactive.next();
                Reactive reactive = ReactiveDatabase.get(rsReactive);
                solutionReactive.setReactive(reactive);
                solutionReactives.add(solutionReactive);
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
            return solution;
        }
    }

}
