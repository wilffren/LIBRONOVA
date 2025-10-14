package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.exceptions.*;
import java.util.List;

/**
 * Service interface for book business logic.
 * 
 * @author Wilffren Muñoz
 */
public interface BookService {
    
    /**
     * Registers a new book in the system.
     * 
     * @param book the book to register
     * @return the registered book
     * @throws DuplicateISBNException if ISBN already exists
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Book registerBook(Book book) throws DuplicateISBNException, ValidationException, DatabaseException;
    
    /**
     * Updates an existing book.
     * 
     * @param book the book to update
     * @return the updated book
     * @throws EntityNotFoundException if book not found
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Book updateBook(Book book) throws EntityNotFoundException, ValidationException, DatabaseException;
    
    /**
     * Finds a book by its ID.
     * 
     * @param id the book ID
     * @return the book
     * @throws EntityNotFoundException if book not found
     * @throws DatabaseException if database operation fails
     */
    Book findBookById(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN
     * @return the book
     * @throws EntityNotFoundException if book not found
     * @throws DatabaseException if database operation fails
     */
    Book findBookByIsbn(String isbn) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Lists all books in the catalog.
     * 
     * @return list of all books
     * @throws DatabaseException if database operation fails
     */
    List<Book> listAllBooks() throws DatabaseException;
    
    /**
     * Searches books by title.
     * 
     * @param title the title to search
     * @return list of matching books
     * @throws DatabaseException if database operation fails
     */
    List<Book> findBooksByTitle(String title) throws DatabaseException;
    
    /**
     * Deletes a book from the system.
     * 
     * @param id the book ID
     * @throws EntityNotFoundException if book not found
     * @throws DatabaseException if database operation fails
     */
    void deleteBook(Long id) throws EntityNotFoundException, DatabaseException;
}