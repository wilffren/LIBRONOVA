package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.exceptions.DuplicateISBNException;
import com.mycompany.libronova.exceptions.EntityNotFoundException;
import com.mycompany.libronova.exceptions.InsufficientStockException;
import com.mycompany.libronova.exceptions.ValidationException;
import com.mycompany.libronova.repository.BookRepository;
import com.mycompany.libronova.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookServiceImpl focusing on stock validation and business logic.
 * 
 * @author Wilffren Muñoz
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Stock Validation Tests")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    
    private BookServiceImpl bookService;
    private Book testBook;
    
    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepository);
        testBook = createTestBook();
    }
    
    private Book createTestBook() {
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("978-0123456789");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setYear(Year.of(2023));
        book.setTotalStock(10);
        book.setAvailableStock(5);
        return book;
    }
    
    @Test
    @DisplayName("Should register book successfully with valid stock")
    void shouldRegisterBookSuccessfully() throws Exception {
        // Given
        Book newBook = createTestBook();
        newBook.setId(null); // New book without ID
        newBook.setIsbn("978-0987654321");
        
        when(bookRepository.findByIsbn(newBook.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);
        
        // When
        Book result = bookService.registerBook(newBook);
        
        // Then
        assertNotNull(result);
        assertEquals(newBook.getIsbn(), result.getIsbn());
        assertEquals(newBook.getTotalStock(), result.getAvailableStock());
        verify(bookRepository).findByIsbn(newBook.getIsbn());
        verify(bookRepository).save(newBook);
    }
    
    @Test
    @DisplayName("Should throw ValidationException when total stock is zero")
    void shouldThrowValidationExceptionWhenTotalStockIsZero() throws DatabaseException, DuplicateISBNException {
        // Given
        Book invalidBook = createTestBook();
        invalidBook.setTotalStock(0);
        
        // When & Then
        assertThrows(ValidationException.class, 
            () -> bookService.registerBook(invalidBook));
        
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should throw ValidationException when total stock is negative")
    void shouldThrowValidationExceptionWhenTotalStockIsNegative() throws DatabaseException, DuplicateISBNException {
        // Given
        Book invalidBook = createTestBook();
        invalidBook.setTotalStock(-5);
        
        // When & Then
        assertThrows(ValidationException.class, 
            () -> bookService.registerBook(invalidBook));
    }
    
    @Test
    @DisplayName("Should throw DuplicateISBNException when ISBN already exists")
    void shouldThrowDuplicateISBNExceptionWhenISBNExists() throws DatabaseException, ValidationException, DuplicateISBNException {
        // Given
        Book duplicateBook = createTestBook();
        duplicateBook.setId(null);
        
        when(bookRepository.findByIsbn(duplicateBook.getIsbn())).thenReturn(Optional.of(testBook));
        
        // When & Then
        DuplicateISBNException exception = assertThrows(DuplicateISBNException.class,
            () -> bookService.registerBook(duplicateBook));
        
        assertEquals(duplicateBook.getIsbn(), exception.getIsbn());
        verify(bookRepository).findByIsbn(duplicateBook.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    @DisplayName("Should update book successfully")
    void shouldUpdateBookSuccessfully() throws Exception {
        // Given
        testBook.setTotalStock(15);
        testBook.setAvailableStock(15);
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        
        // When
        Book result = bookService.updateBook(testBook);
        
        // Then
        assertEquals(15, result.getTotalStock());
        assertEquals(15, result.getAvailableStock());
        verify(bookRepository).update(testBook);
    }
    
    @Test
    @DisplayName("Should find book by ID successfully")
    void shouldFindBookByIdSuccessfully() throws Exception {
        // Given
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        
        // When
        Book result = bookService.findBookById(testBook.getId());
        
        // Then
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getIsbn(), result.getIsbn());
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException when finding non-existent book")
    void shouldThrowEntityNotFoundExceptionWhenFindingNonExistentBook() throws DatabaseException {
        // Given
        Long nonExistentId = 999L;
        when(bookRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> bookService.findBookById(nonExistentId));
        
        assertEquals("Book", exception.getEntityType());
        assertEquals(nonExistentId, exception.getIdentifier());
    }
    
    @Test
    @DisplayName("Should find all books successfully")
    void shouldFindAllBooksSuccessfully() throws DatabaseException {
        // Given
        List<Book> expectedBooks = Arrays.asList(testBook, createTestBook());
        when(bookRepository.findAll()).thenReturn(expectedBooks);
        
        // When
        List<Book> result = bookService.listAllBooks();
        
        // Then
        assertEquals(expectedBooks.size(), result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should search books by title successfully")
    void shouldSearchBooksByTitleSuccessfully() throws DatabaseException {
        // Given
        String searchTitle = "Test";
        List<Book> expectedBooks = Arrays.asList(testBook);
        when(bookRepository.findByTitle(searchTitle)).thenReturn(expectedBooks);
        
        // When
        List<Book> result = bookService.findBooksByTitle(searchTitle);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
        assertTrue(result.get(0).getTitle().contains("Test"));
    }
    
    @Test
    @DisplayName("Should validate book fields correctly")
    void shouldValidateBookFieldsCorrectly() throws DatabaseException, ValidationException, DuplicateISBNException {
        // Given
        Book invalidBook = new Book();
        
        // Test null ISBN - should throw ValidationException
        invalidBook.setIsbn(null);
        assertThrows(ValidationException.class,
            () -> bookService.registerBook(invalidBook));
    }
}