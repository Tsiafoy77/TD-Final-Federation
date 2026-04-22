package com.federation.agriculture.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig() {
        this.url = "jdbc:postgresql://localhost:5432/federation_db";
        this.username = "postgres";
        this.password = "lukadoncic";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}