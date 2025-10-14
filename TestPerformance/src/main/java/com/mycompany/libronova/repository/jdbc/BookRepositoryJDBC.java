package com.mycompany.libronova.repository.jdbc;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.exceptions.DuplicateISBNException;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.BookRepository;
import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of BookRepository.
 * 
 * @author Wilffren Muñoz
 */
public class BookRepositoryJDBC implements BookRepository {
    
    private static final Logger LOGGER = Logger.getLogger(BookRepositoryJDBC.class.getName());
    private final ConnectionDB connectionDB;
    
    public BookRepositoryJDBC() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Book save(Book book) throws DuplicateISBNException, DatabaseException {
        if (existsByIsbn(book.getIsbn())) {
            throw new DuplicateISBNException(book.getIsbn());
        }
        
        String sql = "INSERT INTO books (isbn, title, author, publisher, year, available_stock, total_stock) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYear().getValue());
            stmt.setInt(6, book.getAvailableStock());
            stmt.setInt(7, book.getTotalStock());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Could not save the book");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getLong(1));
                }
            }
            
            LOGGER.info("Book saved: " + book.getIsbn());
            return book;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error saving book", ex);
            throw new DatabaseException("Error saving book", ex);
        }
    }
    
    @Override
    public Book update(Book book) throws DatabaseException {
        String sql = "UPDATE books SET title = ?, author = ?, publisher = ?, year = ?, " +
                     "available_stock = ?, total_stock = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setInt(4, book.getYear().getValue());
            stmt.setInt(5, book.getAvailableStock());
            stmt.setInt(6, book.getTotalStock());
            stmt.setLong(7, book.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Book not found for update");
            }
            
            LOGGER.info("Book updated: " + book.getId());
            return book;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating book", ex);
            throw new DatabaseException("Error updating book", ex);
        }
    }
    
    @Override
    public Optional<Book> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding book by ID", ex);
            throw new DatabaseException("Error finding book by ID", ex);
        }
    }
    
    @Override
    public Optional<Book> findByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding book by ISBN", ex);
            throw new DatabaseException("Error finding book by ISBN", ex);
        }
    }
    
    @Override
    public List<Book> findAll() throws DatabaseException {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
            
            return books;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error listing all books", ex);
            throw new DatabaseException("Error listing all books", ex);
        }
    }
    
    @Override
    public List<Book> findByTitle(String title) throws DatabaseException {
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
            
            return books;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding books by title", ex);
            throw new DatabaseException("Error finding books by title", ex);
        }
    }
    
    @Override
    public boolean deleteById(Long id) throws DatabaseException {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            LOGGER.info("Book deleted: " + id);
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting book", ex);
            throw new DatabaseException("Error deleting book", ex);
        }
    }
    
    @Override
    public boolean existsByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
            return false;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking ISBN existence", ex);
            throw new DatabaseException("Error checking ISBN existence", ex);
        }
    }
    
    /**
     * Maps a ResultSet row to a Book object.
     * 
     * @param rs the ResultSet
     * @return a Book object
     * @throws SQLException if mapping fails
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setYear(Year.of(rs.getInt("year")));
        book.setAvailableStock(rs.getInt("available_stock"));
        book.setTotalStock(rs.getInt("total_stock"));
        return book;
    }
}