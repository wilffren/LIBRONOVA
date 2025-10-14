package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.*;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.LibroRepository;
import com.mycompany.libronova.repository.PrestamoRepository;
import com.mycompany.libronova.repository.SocioRepository;
import com.mycompany.libronova.service.PrestamoService;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of PrestamoService with transaction support.
 * 
 * @author Wilffren Muñoz
 */
public class PrestamoServiceImpl implements PrestamoService {
    
    private static final Logger LOGGER = Logger.getLogger(PrestamoServiceImpl.class.getName());
    private static final double MULTA_POR_DIA = 2.0; // $2 per day overdue
    
    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;
    private final ConnectionDB connectionDB;
    
    public PrestamoServiceImpl(PrestamoRepository prestamoRepository,
                               LibroRepository libroRepository,
                               SocioRepository socioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.socioRepository = socioRepository;
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Prestamo crearPrestamo(Long libroId, Long socioId, int diasPrestamo) 
            throws EntityNotFoundException, SocioInactivoException, StockInsuficienteException, DatabaseException {
        
        Connection conn = null;
        try {
            conn = connectionDB.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Validate and get book
            Libro libro = libroRepository.findById(libroId)
                    .orElseThrow(() -> new EntityNotFoundException("Libro", libroId));
            
            // Validate and get member
            Socio socio = socioRepository.findById(socioId)
                    .orElseThrow(() -> new EntityNotFoundException("Socio", socioId));
            
            // Business validations
            if (!socio.isActivo()) {
                throw new SocioInactivoException(socio.getNumeroSocio());
            }
            
            if (!libro.isDisponible()) {
                throw new StockInsuficienteException(libro.getIsbn(), libro.getStockDisponible());
            }
            
            // Create loan
            Prestamo prestamo = new Prestamo(libro, socio, diasPrestamo);
            prestamo = prestamoRepository.save(prestamo);
            
            // Update book stock
            libro.setStockDisponible(libro.getStockDisponible() - 1);
            libroRepository.update(libro);
            
            conn.commit(); // Commit transaction
            LOGGER.info(String.format("Préstamo creado: ID=%d, Libro=%s, Socio=%s", 
                    prestamo.getId(), libro.getTitulo(), socio.getNombre()));
            
            return prestamo;
            
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Transaction rolled back");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", e);
                }
            }
            LOGGER.log(Level.SEVERE, "Error creating loan", ex);
            throw new DatabaseException("Error al crear préstamo", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }
    
    @Override
    public Prestamo devolverLibro(Long prestamoId) throws EntityNotFoundException, DatabaseException {
        Connection conn = null;
        try {
            conn = connectionDB.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Get loan
            Prestamo prestamo = prestamoRepository.findById(prestamoId)
                    .orElseThrow(() -> new EntityNotFoundException("Préstamo", prestamoId));
            
            if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
                throw new DatabaseException("El préstamo no está activo");
            }
            
            // Update loan
            prestamo.setFechaDevolucionReal(LocalDate.now());
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            prestamo = prestamoRepository.update(prestamo);
            
            // Update book stock
            Libro libro = prestamo.getLibro();
            libro.setStockDisponible(libro.getStockDisponible() + 1);
            libroRepository.update(libro);
            
            conn.commit(); // Commit transaction
            LOGGER.info(String.format("Libro devuelto: Préstamo ID=%d", prestamoId));
            
            return prestamo;
            
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Transaction rolled back");
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", e);
                }
            }
            LOGGER.log(Level.SEVERE, "Error returning book", ex);
            throw new DatabaseException("Error al devolver libro", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }
    
    @Override
    public Prestamo buscarPrestamoPorId(Long id) throws EntityNotFoundException, DatabaseException {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Préstamo", id));
    }
    
    @Override
    public List<Prestamo> listarTodosPrestamos() throws DatabaseException {
        return prestamoRepository.findAll();
    }
    
    @Override
    public List<Prestamo> listarPrestamosActivosPorSocio(Long socioId) throws DatabaseException {
        return prestamoRepository.findActivosBySocioId(socioId);
    }
    
    @Override
    public List<Prestamo> listarPrestamosVencidos() throws DatabaseException {
        return prestamoRepository.findVencidos();
    }
    
    @Override
    public double calcularMulta(Long prestamoId) throws EntityNotFoundException, DatabaseException {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new EntityNotFoundException("Préstamo", prestamoId));
        
        long diasVencidos = prestamo.diasVencidos();
        return diasVencidos * MULTA_POR_DIA;
    }
}