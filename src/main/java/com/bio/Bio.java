package com.bio;

import com.bio.database.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class Bio {

    Server server;

    public static Properties properties;
    public static DatabaseConnection database;

    static Logger log = Logger.getLogger(Bio.class.getName());

    public static void main(String[] args) {
        Path config = Paths.get("config","application.properties");
        properties = new Properties();
        try (FileReader reader = new FileReader(config.toFile())) {
            properties.load(reader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("File application.properties found.");

        Properties connProps = new Properties();
        String url = properties.getProperty("db.url");
        connProps.put("user", properties.getProperty("db.user"));
        connProps.put("password", properties.getProperty("db.password"));
        try {
            database = new DatabaseConnection(url, connProps, false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        log.info("Database connection success.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                database.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));

        try {
            Connection connection = database.getConnection();
            try (Statement statement = connection.createStatement()) {
                OrganismDatabase.init(statement);
                ReactiveDatabase.init(statement);
                MaterialDatabase.init(statement);
                SolutionDatabase.init(statement);
                FeedDatabase.init(statement);
                SolutionReactiveDatabase.init(statement);
                SolutionMaterialDatabase.init(statement);
                SolutionRefDatabase.init(statement);
                ExperimentDatabase.init(statement);
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Bio bio = new Bio();
        bio.createServer();
        bio.startServer();

        log.info("Server started.");
    }

    void startServer() {
        server.start();
    }

    void createServer() {
        server = new Server();
        server.createContexts();
    }

}
