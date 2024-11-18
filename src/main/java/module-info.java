module com.bio {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.logging;
    requires java.sql;
    requires jdk.httpserver;

    exports com.bio;
    exports com.bio.value;
    exports com.bio.entity;
    exports com.bio.json;
}