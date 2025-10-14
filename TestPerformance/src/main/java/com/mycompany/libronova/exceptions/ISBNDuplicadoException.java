package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when attempting to register a book with duplicate ISBN.
 * 
 * @author Wilffren Muñoz
 */
public class ISBNDuplicadoException extends LibroNovaException {
    
    private final String isbn;
    
    public ISBNDuplicadoException(String isbn) {
        super(String.format("El ISBN '%s' ya está registrado en el sistema", isbn));
        this.isbn = isbn;
    }
    
    public String getIsbn() {
        return isbn;
    }
}