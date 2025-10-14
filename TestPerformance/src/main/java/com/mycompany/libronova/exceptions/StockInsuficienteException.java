package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when there's insufficient stock for a loan.
 * 
 * @author Wilffren Muñoz
 */
public class StockInsuficienteException extends LibroNovaException {
    
    private final String isbn;
    private final int stockDisponible;
    
    public StockInsuficienteException(String isbn, int stockDisponible) {
        super(String.format("Insufficient stock for the book with ISBN '%s'. Available: %d", 
                isbn, stockDisponible));
        this.isbn = isbn;
        this.stockDisponible = stockDisponible;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public int getStockDisponible() {
        return stockDisponible;
    }
}
