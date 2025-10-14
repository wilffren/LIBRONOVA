package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when attempting to register a book with duplicate ISBN.
 * 
 * @author Wilffren Muñoz
 */
public class DuplicateISBNException extends LibroNovaException {
    
    private final String isbn;
    
    public DuplicateISBNException(String isbn) {
        super(String.format("The ISBN '%s' is already registered in the system", isbn));
        this.isbn = isbn;
    }
    
    public String getIsbn() {
        return isbn;
    }
}