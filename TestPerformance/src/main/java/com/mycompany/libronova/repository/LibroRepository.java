package com.mycompany.libronova.repository;

import com.mycompany.libronova.domain.Libro;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.exceptions.ISBNDuplicadoException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Libro entity operations.
 * 
 * @author Wilffren Muñoz
 */
public interface LibroRepository {
    
    /**
     * Saves a new book to the database.
     * 
     * @param libro the book to save
     * @return the saved book with generated ID
     * @throws ISBNDuplicadoException if ISBN already exists
     * @throws DatabaseException if database operation fails
     */
    Libro save(Libro libro) throws ISBNDuplicadoException, DatabaseException;
    
    /**
     * Updates an existing book.
     * 
     * @param libro the book to update
     * @return the updated book
     * @throws DatabaseException if database operation fails
     */
    Libro update(Libro libro) throws DatabaseException;
    
    /**
     * Finds a book by its ID.
     * 
     * @param id the book ID
     * @return an Optional containing the book if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Libro> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the book ISBN
     * @return an Optional containing the book if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Libro> findByIsbn(String isbn) throws DatabaseException;
    
    /**
     * Retrieves all books from the database.
     * 
     * @return list of all books
     * @throws DatabaseException if database operation fails
     */
    List<Libro> findAll() throws DatabaseException;
    
    /**
     * Searches books by title (partial match).
     * 
     * @param titulo the title to search
     * @return list of matching books
     * @throws DatabaseException if database operation fails
     */
    List<Libro> findByTitulo(String titulo) throws DatabaseException;
    
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
