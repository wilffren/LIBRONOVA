package com.mycompany.libronova.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when validation fails.
 * 
 * @author Wilffren Muñoz
 */
public class ValidationException extends LibroNovaException {
    
    private final List<String> errors;
    
    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }
    
    public ValidationException(List<String> errors) {
        super("Errores de validación: " + String.join(", ", errors));
        this.errors = new ArrayList<>(errors);
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
}