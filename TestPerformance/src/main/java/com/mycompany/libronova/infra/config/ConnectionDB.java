package com.mycompany.libronova.infra.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class for managing database connections.
 * Loads configuration from config.properties file.
 * 
 * @author Wilffren Muñoz
 * @version 1.0
 */
public class ConnectionDB {
    
    private static ConnectionDB instance;
    private static final Logger LOGGER = Logger.getLogger(ConnectionDB.class.getName());
    
    private String url;
    private String user;
    private String password;
    
    /**
     * Private constructor to prevent instantiation.
     * Loads database configuration from properties file.
     */
    private ConnectionDB() {
        loadProperties();
    }
    
    /**
     * Gets the singleton instance of ConnectionDB.
     * 
     * @return the singleton instance
     */
    public static synchronized ConnectionDB getInstance() {
        if (instance == null) {
            instance = new ConnectionDB();
        }
        return instance;
    }
    
    /**
     * Loads database configuration from config.properties file.
     */
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input == null) {
                LOGGER.warning("config.properties not found, using defaults");
                setDefaultProperties();
                return;
            }
            
            props.load(input);
            this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/libronova");
            this.user = props.getProperty("db.user", "root");
            this.password = props.getProperty("db.password", "Qwe.123*");
            
            LOGGER.info("Database configuration loaded successfully");
            
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading properties file", ex);
            setDefaultProperties();
        }
    }
    
    /**
     * Sets default database properties.
     */
    private void setDefaultProperties() {
        this.url = "jdbc:mysql://localhost:3306/libronova";
        this.user = "root";
        this.password = "Qwe.123*";
    }
    
    /**
     * Creates and returns a new database connection.
     * 
     * @return a Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "MySQL Driver not found", ex);
            throw new SQLException("Database driver not found", ex);
        }
    }
    
    /**
     * Tests the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Connection test failed", ex);
            return false;
        }
    }
}