package com.mycompany.libronova.domain;

import java.time.Year;
import java.util.Objects;

/**
 * Domain model representing a book in the library system.
 * 
 * @author Wilffren Muñoz
 */
public class Book {
    
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private Year year;
    private Integer availableStock;
    private Integer totalStock;
    
    public Book() {
    }
    
    public Book(String isbn, String title, String author, String publisher, 
                 Year year, Integer availableStock, Integer totalStock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.year = year;
        this.availableStock = availableStock;
        this.totalStock = totalStock;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }
    
    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { 
        this.availableStock = availableStock; 
    }
    
    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
    
    /**
     * Checks if the book is available for loan.
     * 
     * @return true if stock is available
     */
    public boolean isAvailable() {
        return availableStock != null && availableStock > 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
    
    @Override
    public String toString() {
        return String.format("Book{id=%d, isbn='%s', title='%s', author='%s', stock=%d/%d}",
                id, isbn, title, author, availableStock, totalStock);
    }
}