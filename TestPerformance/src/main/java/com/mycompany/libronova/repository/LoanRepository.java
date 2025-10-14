package com.mycompany.libronova.repository;

import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Loan entity operations.
 * 
 * @author Wilffren Muñoz
 */
public interface LoanRepository {
    
    /**
     * Saves a new loan to the database.
     * 
     * @param loan the loan to save
     * @return the saved loan with generated ID
     * @throws DatabaseException if database operation fails
     */
    Loan save(Loan loan) throws DatabaseException;
    
    /**
     * Updates an existing loan.
     * 
     * @param loan the loan to update
     * @return the updated loan
     * @throws DatabaseException if database operation fails
     */
    Loan update(Loan loan) throws DatabaseException;
    
    /**
     * Finds a loan by ID.
     * 
     * @param id the loan ID
     * @return an Optional containing the loan if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Loan> findById(Long id) throws DatabaseException;
    
    /**
     * Retrieves all loans from the database.
     * 
     * @return list of all loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> findAll() throws DatabaseException;
    
    /**
     * Finds all active loans for a specific member.
     * 
     * @param memberId the member ID
     * @return list of active loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> findActiveByMemberId(Long memberId) throws DatabaseException;
    
    /**
     * Finds all active loans for a specific book.
     * 
     * @param bookId the book ID
     * @return list of active loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> findActiveByBookId(Long bookId) throws DatabaseException;
    
    /**
     * Finds all overdue loans.
     * 
     * @return list of overdue loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> findOverdue() throws DatabaseException;
}