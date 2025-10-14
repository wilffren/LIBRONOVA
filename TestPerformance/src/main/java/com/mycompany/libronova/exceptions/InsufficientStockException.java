package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when there's insufficient stock for a loan.
 * 
 * @author Wilffren Muñoz
 */
public class InsufficientStockException extends LibroNovaException {
    
    private final String isbn;
    private final int availableStock;
    
    public InsufficientStockException(String isbn, int availableStock) {
        super(String.format("Insufficient stock for the book with ISBN '%s'. Available: %d", 
                isbn, availableStock));
        this.isbn = isbn;
        this.availableStock = availableStock;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public int getAvailableStock() {
        return availableStock;
    }
}