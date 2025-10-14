package com.mycompany.libronova.repository.jdbc;

import com.mycompany.libronova.domain.Libro;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.exceptions.ISBNDuplicadoException;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.LibroRepository;
import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of LibroRepository.
 * 
 * @author Wilffren Muñoz
 */
public class LibroRepositoryJDBC implements LibroRepository {
    
    private static final Logger LOGGER = Logger.getLogger(LibroRepositoryJDBC.class.getName());
    private final ConnectionDB connectionDB;
    
    public LibroRepositoryJDBC() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Libro save(Libro libro) throws ISBNDuplicadoException, DatabaseException {
        if (existsByIsbn(libro.getIsbn())) {
            throw new ISBNDuplicadoException(libro.getIsbn());
        }
        
        String sql = "INSERT INTO libros (isbn, titulo, autor, editorial, anio, stock_disponible, stock_total) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, libro.getIsbn());
            stmt.setString(2, libro.getTitulo());
            stmt.setString(3, libro.getAutor());
            stmt.setString(4, libro.getEditorial());
            stmt.setInt(5, libro.getAnio().getValue());
            stmt.setInt(6, libro.getStockDisponible());
            stmt.setInt(7, libro.getStockTotal());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("No se pudo guardar el libro");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    libro.setId(generatedKeys.getLong(1));
                }
            }
            
            LOGGER.info("Libro guardado: " + libro.getIsbn());
            return libro;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al guardar libro", ex);
            throw new DatabaseException("Error al guardar libro", ex);
        }
    }
    
    @Override
    public Libro update(Libro libro) throws DatabaseException {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, editorial = ?, anio = ?, " +
                     "stock_disponible = ?, stock_total = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setInt(4, libro.getAnio().getValue());
            stmt.setInt(5, libro.getStockDisponible());
            stmt.setInt(6, libro.getStockTotal());
            stmt.setLong(7, libro.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Libro no encontrado para actualizar");
            }
            
            LOGGER.info("Libro actualizado: " + libro.getId());
            return libro;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al actualizar libro", ex);
            throw new DatabaseException("Error al actualizar libro", ex);
        }
    }
    
    @Override
    public Optional<Libro> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM libros WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibro(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar libro por ID", ex);
            throw new DatabaseException("Error al buscar libro por ID", ex);
        }
    }
    
    @Override
    public Optional<Libro> findByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibro(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar libro por ISBN", ex);
            throw new DatabaseException("Error al buscar libro por ISBN", ex);
        }
    }
    
    @Override
    public List<Libro> findAll() throws DatabaseException {
        String sql = "SELECT * FROM libros ORDER BY titulo";
        List<Libro> libros = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                libros.add(mapResultSetToLibro(rs));
            }
            
            return libros;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al listar todos los libros", ex);
            throw new DatabaseException("Error al listar todos los libros", ex);
        }
    }
    
    @Override
    public List<Libro> findByTitulo(String titulo) throws DatabaseException {
        String sql = "SELECT * FROM libros WHERE titulo LIKE ? ORDER BY titulo";
        List<Libro> libros = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titulo + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapResultSetToLibro(rs));
                }
            }
            
            return libros;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar libros por título", ex);
            throw new DatabaseException("Error al buscar libros por título", ex);
        }
    }
    
    @Override
    public boolean deleteById(Long id) throws DatabaseException {
        String sql = "DELETE FROM libros WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            LOGGER.info("Libro eliminado: " + id);
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al eliminar libro", ex);
            throw new DatabaseException("Error al eliminar libro", ex);
        }
    }
    
    @Override
    public boolean existsByIsbn(String isbn) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM libros WHERE isbn = ?";
        
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
            LOGGER.log(Level.SEVERE, "Error al verificar existencia de ISBN", ex);
            throw new DatabaseException("Error al verificar existencia de ISBN", ex);
        }
    }
    
    /**
     * Maps a ResultSet row to a Libro object.
     * 
     * @param rs the ResultSet
     * @return a Libro object
     * @throws SQLException if mapping fails
     */
    private Libro mapResultSetToLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getLong("id"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setAnio(Year.of(rs.getInt("anio")));
        libro.setStockDisponible(rs.getInt("stock_disponible"));
        libro.setStockTotal(rs.getInt("stock_total"));
        return libro;
    }
}