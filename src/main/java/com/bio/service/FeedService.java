package com.bio.service;

import com.bio.Bio;
import com.bio.database.FeedDatabase;
import com.bio.entity.Feed;
import com.bio.entity.Solution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedService {

    public static boolean existByName(String name) throws SQLException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FeedDatabase.selectByName)) {
            FeedDatabase.prepareSelectByName(statement, name);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        }
    }

    public static Feed getById(Long id) throws SQLException, EntityNotFoundException {
        Connection connection = Bio.database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FeedDatabase.selectById)) {
            FeedDatabase.prepareSelectById(statement, id);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                String message = String.format("Питательная среда %d не найдена.", id);
                throw new EntityNotFoundException(message);
            }
            Feed feed = FeedDatabase.get(rs);
            // solution
            Long solutionId = feed.getSolution().getId();
            Solution solution = SolutionService.getById(solutionId);
            feed.setSolution(solution);
            return feed;
        }
    }

}
