package com.mycompany.libronova.repository;

import com.mycompany.libronova.domain.Prestamo;
import com.mycompany.libronova.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Prestamo entity operations.
 * 
 * @author Wilffren Muñoz
 */
public interface PrestamoRepository {
    
    /**
     * Saves a new loan to the database.
     * 
     * @param prestamo the loan to save
     * @return the saved loan with generated ID
     * @throws DatabaseException if database operation fails
     */
    Prestamo save(Prestamo prestamo) throws DatabaseException;
    
    /**
     * Updates an existing loan.
     * 
     * @param prestamo the loan to update
     * @return the updated loan
     * @throws DatabaseException if database operation fails
     */
    Prestamo update(Prestamo prestamo) throws DatabaseException;
    
    /**
     * Finds a loan by ID.
     * 
     * @param id the loan ID
     * @return an Optional containing the loan if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Prestamo> findById(Long id) throws DatabaseException;
    
    /**
     * Retrieves all loans from the database.
     * 
     * @return list of all loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> findAll() throws DatabaseException;
    
    /**
     * Finds all active loans for a specific member.
     * 
     * @param socioId the member ID
     * @return list of active loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> findActivosBySocioId(Long socioId) throws DatabaseException;
    
    /**
     * Finds all active loans for a specific book.
     * 
     * @param libroId the book ID
     * @return list of active loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> findActivosByLibroId(Long libroId) throws DatabaseException;
    
    /**
     * Finds all overdue loans.
     * 
     * @return list of overdue loans
     * @throws DatabaseException if database operation fails
     */
    List<Prestamo> findVencidos() throws DatabaseException;
}