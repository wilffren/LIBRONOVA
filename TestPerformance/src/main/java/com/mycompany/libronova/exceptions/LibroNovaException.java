package com.mycompany.libronova.exceptions;

/**
 * Base exception for LibroNova application.
 * 
 * @author Wilffren Muñoz
 */
public class LibroNovaException extends Exception {
    
    public LibroNovaException(String message) {
        super(message);
    }
    
    public LibroNovaException(String message, Throwable cause) {
        super(message, cause);
    }
}