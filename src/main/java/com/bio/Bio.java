package com.bio;

import com.bio.database.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class Bio {

    Server server;

    public static Connection database;

    static Logger log = Logger.getLogger(Bio.class.getName());

    public static void main(String[] args) {
        String config = "config\\application.properties";
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(config)) {
            properties.load(reader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("File config.properties found.");

        Properties connProps = new Properties();
        String url = properties.getProperty("db.url");
        connProps.put("user", properties.getProperty("db.user"));
        connProps.put("password", properties.getProperty("db.password"));
        try {
            database = DriverManager.getConnection(url, connProps);
            database.setAutoCommit(false);
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

        try (Statement statement = database.createStatement()) {
            statement.execute(OrganismDatabase.createTable);
            statement.execute(ReactiveDatabase.createTable);
            statement.execute(MaterialDatabase.createTable);
            statement.execute(FeedDatabase.createTable);
            statement.execute(FeedReactiveDatabase.createTable);
            statement.execute(FeedMaterialDatabase.createTable);
            statement.execute(FeedItemDatabase.createTable);
            statement.execute(ExperimentDatabase.createTable);
            database.commit();
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
