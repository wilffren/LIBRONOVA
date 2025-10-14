package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.infra.config.LoggingConfig;
import com.mycompany.libronova.repository.BookRepository;
import com.mycompany.libronova.service.BookService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of BookService with business validation.
 * 
 * @author Wilffren Muñoz
 */
public class BookServiceImpl implements BookService {
    
    private static final Logger LOGGER = LoggingConfig.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;
    
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Override
    public Book registerBook(Book book) throws DuplicateISBNException, ValidationException, DatabaseException {
        try {
            validateBook(book);
            
            if (book.getAvailableStock() > book.getTotalStock()) {
                throw new ValidationException("Available stock cannot be greater than total stock");
            }
            
            LOGGER.info("Registering book: " + book.getIsbn());
            Book savedBook = bookRepository.save(book);
            LOGGER.info("Book registered successfully: " + savedBook.getId());
            return savedBook;
        } catch (Exception e) {
            LOGGER.severe("Failed to register book: " + book.getIsbn() + ". Error: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Book updateBook(Book book) throws EntityNotFoundException, ValidationException, DatabaseException {
        validateBook(book);
        
        if (book.getId() == null) {
            throw new ValidationException("Book ID is required for update");
        }
        
        // Verify book exists
        bookRepository.findById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book", book.getId()));
        
        if (book.getAvailableStock() > book.getTotalStock()) {
            throw new ValidationException("Available stock cannot be greater than total stock");
        }
        
        LOGGER.info("Updating book: " + book.getId());
        return bookRepository.update(book);
    }
    
    @Override
    public Book findBookById(Long id) throws EntityNotFoundException, DatabaseException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));
    }
    
    @Override
    public Book findBookByIsbn(String isbn) throws EntityNotFoundException, DatabaseException {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Book", isbn));
    }
    
    @Override
    public List<Book> listAllBooks() throws DatabaseException {
        return bookRepository.findAll();
    }
    
    @Override
    public List<Book> findBooksByTitle(String title) throws DatabaseException {
        if (title == null || title.trim().isEmpty()) {
            try {
                throw new ValidationException("Search title cannot be empty");
            } catch (ValidationException ex) {
                System.getLogger(BookServiceImpl.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        return bookRepository.findByTitle(title);
    }
    
    @Override
    public void deleteBook(Long id) throws EntityNotFoundException, DatabaseException {
        // Verify book exists
        bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book", id));
        
        boolean deleted = bookRepository.deleteById(id);
        if (!deleted) {
            throw new DatabaseException("Could not delete the book");
        }
        
        LOGGER.info("Book deleted: " + id);
    }
    
    /**
     * Validates book data.
     */
    private void validateBook(Book book) throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            errors.add("ISBN is required");
        }
        
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            errors.add("Title is required");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            errors.add("Author is required");
        }
        
        if (book.getAvailableStock() == null || book.getAvailableStock() < 0) {
            errors.add("Available stock must be greater than or equal to 0");
        }
        
        if (book.getTotalStock() == null || book.getTotalStock() <= 0) {
            errors.add("Total stock must be greater than 0");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}