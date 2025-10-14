package com.mycompany.libronova.repository.jdbc;

import com.mycompany.libronova.domain.EstadoSocio;
import com.mycompany.libronova.domain.RolUsuario;
import com.mycompany.libronova.domain.Socio;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.SocioRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of SocioRepository.
 * 
 * @author Wilffren Muñoz
 */
public class SocioRepositoryJDBC implements SocioRepository {
    
    private static final Logger LOGGER = Logger.getLogger(SocioRepositoryJDBC.class.getName());
    private final ConnectionDB connectionDB;
    
    public SocioRepositoryJDBC() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Socio save(Socio socio) throws DatabaseException {
        String sql = "INSERT INTO socios (nombre, email, numero_socio, estado, fecha_registro) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, socio.getNombre());
            stmt.setString(2, socio.getEmail());
            stmt.setString(3, socio.getNumeroSocio());
            stmt.setString(4, socio.getEstado().name());
            stmt.setDate(5, Date.valueOf(socio.getFechaRegistro()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("No se pudo guardar el socio");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    socio.setId(generatedKeys.getLong(1));
                }
            }
            
            LOGGER.info("Socio guardado: " + socio.getNumeroSocio());
            return socio;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al guardar socio", ex);
            throw new DatabaseException("Error al guardar socio", ex);
        }
    }
    
    @Override
    public Socio update(Socio socio) throws DatabaseException {
        String sql = "UPDATE socios SET nombre = ?, email = ?, estado = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, socio.getNombre());
            stmt.setString(2, socio.getEmail());
            stmt.setString(3, socio.getEstado().name());
            stmt.setLong(4, socio.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Socio no encontrado para actualizar");
            }
            
            LOGGER.info("Socio actualizado: " + socio.getId());
            return socio;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al actualizar socio", ex);
            throw new DatabaseException("Error al actualizar socio", ex);
        }
    }
    
    @Override
    public Optional<Socio> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM socios WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocio(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar socio por ID", ex);
            throw new DatabaseException("Error al buscar socio por ID", ex);
        }
    }
    
    @Override
    public Optional<Socio> findByNumeroSocio(String numeroSocio) throws DatabaseException {
        String sql = "SELECT * FROM socios WHERE numero_socio = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, numeroSocio);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocio(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar socio por número", ex);
            throw new DatabaseException("Error al buscar socio por número", ex);
        }
    }
    
    @Override
    public Optional<Socio> findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM socios WHERE email = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSocio(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al buscar socio por email", ex);
            throw new DatabaseException("Error al buscar socio por email", ex);
        }
    }
    
    @Override
    public List<Socio> findAll() throws DatabaseException {
        String sql = "SELECT * FROM socios ORDER BY nombre";
        List<Socio> socios = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                socios.add(mapResultSetToSocio(rs));
            }
            
            return socios;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al listar todos los socios", ex);
            throw new DatabaseException("Error al listar todos los socios", ex);
        }
    }
    
    @Override
    public List<Socio> findAllActivos() throws DatabaseException {
        String sql = "SELECT * FROM socios WHERE estado = 'ACTIVO' ORDER BY nombre";
        List<Socio> socios = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                socios.add(mapResultSetToSocio(rs));
            }
            
            return socios;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al listar socios activos", ex);
            throw new DatabaseException("Error al listar socios activos", ex);
        }
    }
    
    @Override
    public boolean deleteById(Long id) throws DatabaseException {
        String sql = "DELETE FROM socios WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            LOGGER.info("Socio eliminado: " + id);
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al eliminar socio", ex);
            throw new DatabaseException("Error al eliminar socio", ex);
        }
    }
    
    /**
     * Maps a ResultSet row to a Socio object.
     */
    private Socio mapResultSetToSocio(ResultSet rs) throws SQLException {
        Socio socio = new Socio();
        socio.setId(rs.getLong("id"));
        socio.setNombre(rs.getString("nombre"));
        socio.setEmail(rs.getString("email"));
        socio.setRol(RolUsuario.SOCIO);
        socio.setNumeroSocio(rs.getString("numero_socio"));
        socio.setEstado(EstadoSocio.valueOf(rs.getString("estado")));
        socio.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
        return socio;
    }
}