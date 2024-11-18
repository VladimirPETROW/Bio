package com.bio;

import com.bio.database.*;
import com.bio.entity.Info;

import java.io.*;
import java.net.*;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bio {

    public static Server server;

    public static Properties properties;
    public static DatabaseConnection database;

    public static Program program;

    static String databaseVersionKey = "version";

    public static FileLock lock;

    static Logger log = Logger.getLogger(Bio.class.getName());

    public static void main(String[] args) {
        program = new Program();
        log.info("Current version: " + program.version);

        Path config = Paths.get("config","application.properties");
        properties = new Properties();
        try (FileReader reader = new FileReader(config.toFile())) {
            properties.load(reader);
            log.info("File application.properties found.");
        } catch (FileNotFoundException e) {
            log.info("File application.properties not found. Using default properties.");
            //throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String portableProperty = Bio.properties.getProperty("portable", "true");
        boolean portable = Boolean.parseBoolean(portableProperty);

        program.portable = portable;

        if (portable) {
            Path pathUrl = Paths.get("work", "url");
            if (pathUrl.toFile().exists()) {
                log.info("Browse url path found.");
                try {
                    String browseUrl = Files.readString(pathUrl);

                    log.info(String.format("Browse url \"%s\".", browseUrl));

                    URI uriBrowse = new URI(browseUrl);
                    URLConnection connection = uriBrowse.toURL().openConnection();
                    connection.connect();

                    log.info("Browse url connection success.");

                    Path pathHtml = Paths.get("work", "index.html");
                    //String command = String.format("open \"http://%s:%d\"", bio.server.host, bio.server.port);
                    if (pathHtml.toFile().exists()) {
                        log.info("Browse html path found.");

                        String command = String.format("cmd /c start %s", pathHtml);
                        //log.info(String.format("Execute command \"%s\".", command));
                        Runtime runtime = Runtime.getRuntime();
                        try {
                            Process process = runtime.exec(command);
                            /*
                            int result = process.waitFor();
                            if (result != 0) {
                                //log.info(String.format("Command exit value %d.", result));
                                System.exit(1);
                            }
                            */
                            //Desktop.getDesktop().browse(URI.create(browse));
                            System.exit(0);
                        } catch (IOException e) {
                            //bio.stopServer();
                            //throw new RuntimeException(e);
                            log.log(Level.SEVERE, "", e);
                            System.exit(1);
                        }/* catch (InterruptedException e) {
                            //throw new RuntimeException(e);
                            log.log(Level.SEVERE, "", e);
                            System.exit(1);
                        }*/
                    }
                } catch (URISyntaxException e) {
                    //throw new RuntimeException(e);
                    log.info("Browse url invalid syntax.");
                } catch (MalformedURLException e) {
                    //throw new RuntimeException(e);
                    log.info("Browse url malformed.");
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                    log.info("Browse execution failed.");
                }
            }
        }

        Properties connProps = new Properties();
        String url = properties.getProperty("db.url");
        if (url == null) {
            url = "jdbc:h2:file:./data/bio;FILE_LOCK=FILE;WRITE_DELAY=0";
            try {
                java.util.Date backupDate = null;
                File fileDatabase = new File("./data/bio.mv.db");
                String script = "./data/backup.sql";
                if (!fileDatabase.exists()) {
                    File fileScript = new File(script);
                    if (fileScript.exists()) {
                        backupDate = new java.util.Date(fileScript.lastModified());
                    }
                }

                database = new DatabaseConnection(url, connProps, false);
                if (backupDate != null) {
                    log.info(String.format("Database from script '%s' as of %tc.", script, backupDate));
                    Connection connection = database.getConnection();
                    try (PreparedStatement statement = connection.prepareStatement("RUNSCRIPT FROM ?")) {
                        statement.setString(1, script);
                        statement.execute();
                    }
                }

                int delay = 1; // seconds
                Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
                    if (!fileDatabase.exists()) {
                        try {
                            Connection connection = database.getConnection();
                            database.setCorrupted(true);
                            try (PreparedStatement statement = connection.prepareStatement("SCRIPT TO ?")) {
                                statement.setString(1, script);
                                statement.execute();
                                connection.close();
                                throw new RuntimeException(String.format("Database scripted to '%s'.", script));
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (SQLException e) {
                            database.setCorrupted(true);
                            throw new RuntimeException(e);
                        }
                    }
                }, 0, delay, TimeUnit.SECONDS);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String user = properties.getProperty("db.user");
            if (user != null) {
                connProps.put("user", user);
            }
            String password = properties.getProperty("db.password");
            if (password != null) {
                connProps.put("password", password);
            }
            try {
                database = new DatabaseConnection(url, connProps, false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
                InfoDatabase.init(statement);
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

            Info infoVersion = InfoDatabase.select(connection, databaseVersionKey);
            if (infoVersion == null) {
                infoVersion = new Info(databaseVersionKey, program.version);
                InfoDatabase.insert(connection, infoVersion);
                connection.commit();
            }
            else {
                String databaseVersion = infoVersion.getContent();
                if (!databaseVersion.equals(program.version)) {
                    log.info(String.format("Database version %s not equals program version %s.", databaseVersion, program.version));
                    //TODO update procedure

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        createServer();
        startServer();

        log.info("Server started.");

        if (portable) {
            Path pathUrl = Paths.get("work", "url");
            Path pathHtml = Paths.get("work", "index.html");
            String browseUrl = String.format("http://%s:%d", server.host, server.port);
            String browseHtml = String.format("<script>window.location = \"%s\";</script>", browseUrl);
            try (FileWriter writerUrl = new FileWriter(pathUrl.toFile());
                 FileWriter writerHtml = new FileWriter(pathHtml.toFile())) {
                writerUrl.write(browseUrl);
                writerHtml.write(browseHtml);
            } catch (IOException e) {
                //throw new RuntimeException(e);
                log.log(Level.SEVERE, "", e);
                System.exit(1);
            }

            //String command = String.format("open \"http://%s:%d\"", bio.server.host, bio.server.port);
            String command = String.format("cmd /c start %s", pathHtml);
            //log.info(String.format("Execute command \"%s\".", command));
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec(command);
                /*
                int result = process.waitFor();
                if (result != 0) {
                    //log.info(String.format("Command exit value %d.", result));
                    System.exit(1);
                }
                */
                //Desktop.getDesktop().browse(URI.create(browse));
            } catch (IOException e) {
                //bio.stopServer();
                //throw new RuntimeException(e);
                log.log(Level.SEVERE, "", e);
                System.exit(1);
            }/* catch (InterruptedException e) {
                //throw new RuntimeException(e);
                log.log(Level.SEVERE, "", e);
                System.exit(1);
            }*/
        }

    }

    static void startServer() {
        server.start();
    }

    public static void stopServer() {
        server.stop();
    }

    static void createServer() {
        server = new Server();
        server.createContexts();
    }

}
