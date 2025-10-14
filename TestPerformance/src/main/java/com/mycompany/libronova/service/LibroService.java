package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.Libro;
import com.mycompany.libronova.exceptions.*;
import java.util.List;

/**
 * Service interface for book business logic.
 * 
 * @author Wilffren Muñoz
 */
public interface LibroService {
    
    /**
     * Registers a new book in the system.
     * 
     * @param libro the book to register
     * @return the registered book
     * @throws ISBNDuplicadoException if ISBN already exists
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Libro registrarLibro(Libro libro) throws ISBNDuplicadoException, ValidationException, DatabaseException;
    
    /**
     * Updates an existing book.
     * 
     * @param libro the book to update
     * @return the updated book
     * @throws EntityNotFoundException if book not found
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Libro actualizarLibro(Libro libro) throws EntityNotFoundException, ValidationException, DatabaseException;
    
    /**
     * Finds a book by its ID.
     * 
     * @param id the book ID
     * @return the book
     * @throws EntityNotFoundException if book not found
     * @throws DatabaseException if database operation fails
     */
    Libro buscarLibroPorId(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN
     * @return the book
     * @throws EntityNotFoundException if book not found
     * @throws DatabaseException if database operation fails
     */
    Libro buscarLibroPorIsbn(String isbn) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Lists all books in the catalog.
     * 
     * @return list of all books
     * @throws DatabaseException if database operation fails
     */
    List<Libro> listarTodosLosLibros() throws DatabaseException;
    
    /**
     * Searches books by title.
     * 
     * @param titulo the title to search
     * @return list of matching books
     * @throws DatabaseException if database operation fails
     */
    List<Libro> buscarLibrosPorTitulo(String titulo) throws DatabaseException;
    
    /**
     * Deletes a book from the system.
     * 
     * @param id the book ID
     * @throws EntityNotFoundException if book not found
     * @throws DatabaseException if database operation fails
     */
    void eliminarLibro(Long id) throws EntityNotFoundException, DatabaseException;
}
