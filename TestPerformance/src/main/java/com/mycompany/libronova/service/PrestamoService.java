package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.Prestamo;
import com.mycompany.libronova.exceptions.*;
import java.util.List;

/**
 * Service interface for loan business logic with transaction support.
 * 
 * @author Wilffren Muñoz
 */
public interface PrestamoService {
    
    /**
     * Creates a new loan with transaction support.
     * Validates member status and book availability.
     * Decrements book stock.
     * 
     * @param libroId the book ID
     * @param socioId the member ID
     * @param diasPrestamo number of days for the loan
     * @return the created loan
     * @throws EntityNotFoundException if book or member not found
     * @throws SocioInactivoException if member is not active
     * @throws StockInsuficienteException if book is not available
     * @throws DatabaseException if database operation fails
     */
    Prestamo crearPrestamo(Long libroId, Long socioId, int diasPrestamo) 
            throws EntityNotFoundException, SocioInactivoException, StockInsuficienteException, DatabaseException;
    
    /**
     * Processes a book return with transaction support.
     * Updates loan status and increments book stock.
     * 
     * @param prestamoId the loan ID
     * @return the updated loan
     * @throws EntityNotFoundException if loan not found
     * @throws DatabaseException if database operation fails
     */
    Prestamo devolverLibro(Long prestamoId) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Finds a loan by ID.
     * 
     * @param id the loan ID
     * @return the loan
     * @throws EntityNotFoundException if loan not found
     * @throws DatabaseException if database operation fails
     */
    Prestamo buscarPrestamoPorId(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Lists all loans.
     * 
     * @return list of all loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> listarTodosPrestamos() throws DatabaseException;
    
    /**
     * Lists all active loans for a member.
     * 
     * @param socioId the member ID
     * @return list of active loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> listarPrestamosActivosPorSocio(Long socioId) throws DatabaseException;
    
    /**
     * Lists all overdue loans.
     * 
     * @return list of overdue loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> listarPrestamosVencidos() throws DatabaseException;
    
    /**
     * Calculates fine for an overdue loan.
     * 
     * @param prestamoId the loan ID
     * @return fine amount
     * @throws EntityNotFoundException if loan not found
     * @throws DatabaseException if database operation fails
     */
    double calcularMulta(Long prestamoId) throws EntityNotFoundException, DatabaseException;
}