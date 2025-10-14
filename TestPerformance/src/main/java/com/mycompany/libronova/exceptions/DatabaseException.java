package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when a database operation fails.
 * 
 * @author Wilffren Muñoz
 */
public class DatabaseException extends LibroNovaException {
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
