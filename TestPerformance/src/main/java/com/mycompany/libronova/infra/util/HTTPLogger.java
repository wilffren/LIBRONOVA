package com.mycompany.libronova.infra.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;

/**
 * HTTP Logger utility for simulating HTTP calls and logging them to console.
 * This simulates REST API calls for user management operations.
 * 
 * @author LibroNova Team
 */
public class HTTPLogger {
    
    private static final Logger logger = Logger.getLogger(HTTPLogger.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs a simulated GET request.
     */
    public static void logGET(String endpoint, String description) {
        logRequest("GET", endpoint, null, description);
    }
    
    /**
     * Logs a simulated GET request with parameters.
     */
    public static void logGET(String endpoint, Map<String, String> params, String description) {
        logRequest("GET", endpoint, params, description);
    }
    
    /**
     * Logs a simulated POST request.
     */
    public static void logPOST(String endpoint, String description) {
        logRequest("POST", endpoint, null, description);
    }
    
    /**
     * Logs a simulated POST request with body data.
     */
    public static void logPOST(String endpoint, Map<String, String> body, String description) {
        logRequest("POST", endpoint, body, description);
    }
    
    /**
     * Logs a simulated PATCH request.
     */
    public static void logPATCH(String endpoint, String description) {
        logRequest("PATCH", endpoint, null, description);
    }
    
    /**
     * Logs a simulated PATCH request with body data.
     */
    public static void logPATCH(String endpoint, Map<String, String> body, String description) {
        logRequest("PATCH", endpoint, body, description);
    }
    
    /**
     * Logs a simulated DELETE request.
     */
    public static void logDELETE(String endpoint, String description) {
        logRequest("DELETE", endpoint, null, description);
    }
    
    /**
     * Logs a simulated DELETE request with parameters.
     */
    public static void logDELETE(String endpoint, Map<String, String> params, String description) {
        logRequest("DELETE", endpoint, params, description);
    }
    
    /**
     * Generic method to log HTTP requests.
     */
    private static void logRequest(String method, String endpoint, Map<String, String> data, String description) {
        String timestamp = LocalDateTime.now().format(formatter);
        StringBuilder logMessage = new StringBuilder();
        
        logMessage.append("\n========================================\n");
        logMessage.append("HTTP REQUEST SIMULATION\n");
        logMessage.append("========================================\n");
        logMessage.append("Timestamp: ").append(timestamp).append("\n");
        logMessage.append("Method: ").append(method).append("\n");
        logMessage.append("Endpoint: ").append(endpoint).append("\n");
        logMessage.append("Description: ").append(description).append("\n");
        
        if (data != null && !data.isEmpty()) {
            logMessage.append("Data: ").append(formatData(data)).append("\n");
        }
        
        logMessage.append("Status: ").append(generateSimulatedStatus(method)).append("\n");
        logMessage.append("========================================\n");
        
        // Log to console
        System.out.println(logMessage.toString());
        
        // Also log using Java logging
        logger.info(String.format("[HTTP-SIM] %s %s - %s", method, endpoint, description));
    }
    
    /**
     * Formats data for display.
     */
    private static String formatData(Map<String, String> data) {
        if (data == null || data.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{ ");
        data.forEach((key, value) -> {
            if (key.toLowerCase().contains("password")) {
                sb.append(key).append(": [HIDDEN], ");
            } else {
                sb.append(key).append(": ").append(value).append(", ");
            }
        });
        
        // Remove last comma and space
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(" }");
        
        return sb.toString();
    }
    
    /**
     * Generates simulated HTTP status based on method.
     */
    private static String generateSimulatedStatus(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return "200 OK";
            case "POST":
                return "201 Created";
            case "PATCH":
                return "200 OK";
            case "DELETE":
                return "204 No Content";
            default:
                return "200 OK";
        }
    }
    
    /**
     * Logs authentication attempts.
     */
    public static void logAuthenticationAttempt(String username, boolean success) {
        String timestamp = LocalDateTime.now().format(formatter);
        String status = success ? "SUCCESS" : "FAILED";
        
        String logMessage = String.format(
            "\n[AUTH] %s - Authentication attempt for user '%s': %s\n",
            timestamp, username, status
        );
        
        System.out.println(logMessage);
        logger.info(String.format("[AUTH] User '%s' authentication %s", username, status.toLowerCase()));
    }
    
    /**
     * Logs user session events.
     */
    public static void logUserSession(String username, String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        String logMessage = String.format(
            "\n[SESSION] %s - User '%s': %s\n",
            timestamp, username, action
        );
        
        System.out.println(logMessage);
        logger.info(String.format("[SESSION] User '%s': %s", username, action));
    }
}
