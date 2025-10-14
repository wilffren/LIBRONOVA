package com.mycompany.libronova.repository;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.exceptions.DuplicateISBNException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity operations.
 * 
 * @author Wilffren Muñoz
 */
public interface BookRepository {
    
    /**
     * Saves a new book to the database.
     * 
     * @param book the book to save
     * @return the saved book with generated ID
     * @throws DuplicateISBNException if ISBN already exists
     * @throws DatabaseException if database operation fails
     */
    Book save(Book book) throws DuplicateISBNException, DatabaseException;
    
    /**
     * Updates an existing book.
     * 
     * @param book the book to update
     * @return the updated book
     * @throws DatabaseException if database operation fails
     */
    Book update(Book book) throws DatabaseException;
    
    /**
     * Finds a book by its ID.
     * 
     * @param id the book ID
     * @return an Optional containing the book if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Book> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the book ISBN
     * @return an Optional containing the book if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Book> findByIsbn(String isbn) throws DatabaseException;
    
    /**
     * Retrieves all books from the database.
     * 
     * @return list of all books
     * @throws DatabaseException if database operation fails
     */
    List<Book> findAll() throws DatabaseException;
    
    /**
     * Searches books by title (partial match).
     * 
     * @param title the title to search
     * @return list of matching books
     * @throws DatabaseException if database operation fails
     */
    List<Book> findByTitle(String title) throws DatabaseException;
    
    /**
     * Deletes a book by its ID.
     * 
     * @param id the book ID
     * @return true if deleted successfully
     * @throws DatabaseException if database operation fails
     */
    boolean deleteById(Long id) throws DatabaseException;
    
    /**
     * Checks if an ISBN already exists.
     * 
     * @param isbn the ISBN to check
     * @return true if ISBN exists
     * @throws DatabaseException if database operation fails
     */
    boolean existsByIsbn(String isbn) throws DatabaseException;
}