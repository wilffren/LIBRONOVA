package com.mycompany.libronova.domain;

import java.time.Year;
import java.util.Objects;

/**
 * Domain model representing a book in the library system.
 * 
 * @author Wilffren Muñoz
 */
public class Libro {
    
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private String editorial;
    private Year anio;
    private Integer stockDisponible;
    private Integer stockTotal;
    
    public Libro() {
    }
    
    public Libro(String isbn, String titulo, String autor, String editorial, 
                 Year anio, Integer stockDisponible, Integer stockTotal) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anio = anio;
        this.stockDisponible = stockDisponible;
        this.stockTotal = stockTotal;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    
    public Year getAnio() { return anio; }
    public void setAnio(Year anio) { this.anio = anio; }
    
    public Integer getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(Integer stockDisponible) { 
        this.stockDisponible = stockDisponible; 
    }
    
    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
    
    /**
     * Checks if the book is available for loan.
     * 
     * @return true if stock is available
     */
    public boolean isDisponible() {
        return stockDisponible != null && stockDisponible > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return Objects.equals(isbn, libro.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    @Override
    public String toString() {
        return String.format("Libro{id=%d, isbn='%s', titulo='%s', autor='%s', stock=%d/%d}",
                id, isbn, titulo, autor, stockDisponible, stockTotal);
    }
}