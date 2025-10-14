package com.mycompany.libronova.repository.jdbc;

import com.mycompany.libronova.domain.*;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.PrestamoRepository;
import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of PrestamoRepository.
 * 
 * @author Wilffren Muñoz
 */
public class PrestamoRepositoryJDBC implements PrestamoRepository {
    
    private static final Logger LOGGER = Logger.getLogger(PrestamoRepositoryJDBC.class.getName());
    private final ConnectionDB connectionDB;
    
    public PrestamoRepositoryJDBC() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Prestamo save(Prestamo prestamo) throws DatabaseException {
        String sql = "INSERT INTO prestamos (libro_id, socio_id, fecha_prestamo, " +
                     "fecha_devolucion_prevista, estado) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, prestamo.getLibro().getId());
            stmt.setLong(2, prestamo.getSocio().getId());
            stmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            stmt.setDate(4, Date.valueOf(prestamo.getFechaDevolucionPrevista()));
            stmt.setString(5, prestamo.getEstado().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("No se pudo guardar el préstamo");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prestamo.setId(generatedKeys.getLong(1));
                }
            }
            
            LOGGER.info("Préstamo guardado: " + prestamo.getId());
            return prestamo;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al guardar préstamo", ex);
            throw new DatabaseException("Error al guardar préstamo", ex);
        }
    }
    
    @Override
    public Prestamo update(Prestamo prestamo) throws DatabaseException {
        String sql = "UPDATE prestamos SET fecha_devolucion_real = ?, estado = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (prestamo.getFechaDevolucionReal() != null) {
                stmt.setDate(1, Date.valueOf(prestamo.getFechaDevolucionReal()));
            } else {
                stmt.setNull(1, Types.DATE);
            }
            stmt.setString(2, prestamo.getEstado().name());
            stmt.setLong(3, prestamo.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Préstamo no encontrado para actualizar");
            }
            
            LOGGER.info("Préstamo actualizado: " + prestamo.getId());
            return prestamo;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al actualizar préstamo", ex);
            throw new DatabaseException("Error al actualizar préstamo", ex);
        }
    }
    
    @Override
    public Optional<Prestamo> findById(Long id) throws DatabaseException {
        String sql = "SELECT p.*, l.*, s.* FROM prestamos p " +
                     "JOIN libros l ON p.libro_id = l.id " +
                     "JOIN socios s ON p.socio_id = s.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPrestamo(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamo por ID", ex);
            throw new DatabaseException("Error al buscar préstamo por ID", ex);
        }
    }
    
    @Override
    public List<Prestamo> findAll() throws DatabaseException {
        String sql = "SELECT p.*, l.*, s.* FROM prestamos p " +
                     "JOIN libros l ON p.libro_id = l.id " +
                     "JOIN socios s ON p.socio_id = s.id " +
                     "ORDER BY p.fecha_prestamo DESC";
        
        return executeQueryAndMapList(sql);
    }
    
    @Override
    public List<Prestamo> findActivosBySocioId(Long socioId) throws DatabaseException {
        String sql = "SELECT p.*, l.*, s.* FROM prestamos p " +
                     "JOIN libros l ON p.libro_id = l.id " +
                     "JOIN socios s ON p.socio_id = s.id " +
                     "WHERE p.socio_id = ? AND p.estado = 'ACTIVO'";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, socioId);
            return executeQueryAndMapList(stmt);
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamos activos por socio", ex);
            throw new DatabaseException("Error al buscar préstamos activos por socio", ex);
        }
    }
    
    @Override
    public List<Prestamo> findActivosByLibroId(Long libroId) throws DatabaseException {
        String sql = "SELECT p.*, l.*, s.* FROM prestamos p " +
                     "JOIN libros l ON p.libro_id = l.id " +
                     "JOIN socios s ON p.socio_id = s.id " +
                     "WHERE p.libro_id = ? AND p.estado = 'ACTIVO'";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, libroId);
            return executeQueryAndMapList(stmt);
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamos activos por libro", ex);
            throw new DatabaseException("Error al buscar préstamos activos por libro", ex);
        }
    }
    
    @Override
    public List<Prestamo> findVencidos() throws DatabaseException {
        String sql = "SELECT p.*, l.*, s.* FROM prestamos p " +
                     "JOIN libros l ON p.libro_id = l.id " +
                     "JOIN socios s ON p.socio_id = s.id " +
                     "WHERE p.estado = 'ACTIVO' AND p.fecha_devolucion_prevista < CURDATE()";
        
        return executeQueryAndMapList(sql);
    }
    
    private List<Prestamo> executeQueryAndMapList(String sql) throws DatabaseException {
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
            
            return prestamos;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al ejecutar consulta de préstamos", ex);
            throw new DatabaseException("Error al ejecutar consulta de préstamos", ex);
        }
    }
    
    private List<Prestamo> executeQueryAndMapList(PreparedStatement stmt) throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }
        
        return prestamos;
    }
    
    /**
     * Maps a ResultSet row to a Prestamo object with full Libro and Socio.
     */
    private Prestamo mapResultSetToPrestamo(ResultSet rs) throws SQLException {
        Prestamo prestamo = new Prestamo();
        prestamo.setId(rs.getLong("p.id"));
        prestamo.setFechaPrestamo(rs.getDate("p.fecha_prestamo").toLocalDate());
        prestamo.setFechaDevolucionPrevista(rs.getDate("p.fecha_devolucion_prevista").toLocalDate());
        
        Date fechaDevReal = rs.getDate("p.fecha_devolucion_real");
        if (fechaDevReal != null) {
            prestamo.setFechaDevolucionReal(fechaDevReal.toLocalDate());
        }
        
        prestamo.setEstado(EstadoPrestamo.valueOf(rs.getString("p.estado")));
        
        // Map Libro
        Libro libro = new Libro();
        libro.setId(rs.getLong("l.id"));
        libro.setIsbn(rs.getString("l.isbn"));
        libro.setTitulo(rs.getString("l.titulo"));
        libro.setAutor(rs.getString("l.autor"));
        libro.setEditorial(rs.getString("l.editorial"));
        libro.setAnio(Year.of(rs.getInt("l.anio")));
        libro.setStockDisponible(rs.getInt("l.stock_disponible"));
        libro.setStockTotal(rs.getInt("l.stock_total"));
        prestamo.setLibro(libro);
        
        // Map Socio
        Socio socio = new Socio();
        socio.setId(rs.getLong("s.id"));
        socio.setNombre(rs.getString("s.nombre"));
        socio.setEmail(rs.getString("s.email"));
        socio.setNumeroSocio(rs.getString("s.numero_socio"));
        socio.setEstado(EstadoSocio.valueOf(rs.getString("s.estado")));
        socio.setFechaRegistro(rs.getDate("s.fecha_registro").toLocalDate());
        prestamo.setSocio(socio);
        
        return prestamo;
    }
}