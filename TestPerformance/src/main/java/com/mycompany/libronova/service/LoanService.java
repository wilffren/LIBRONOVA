package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.exceptions.*;
import java.util.List;

/**
 * Service interface for loan business logic with transaction support.
 * 
 * @author Wilffren Muñoz
 */
public interface LoanService {
    
    /**
     * Creates a new loan with transaction support.
     * Validates member status and book availability.
     * Decrements book stock.
     * 
     * @param bookId the book ID
     * @param memberId the member ID
     * @param loanDays number of days for the loan
     * @return the created loan
     * @throws EntityNotFoundException if book or member not found
     * @throws InactiveMemberException if member is not active
     * @throws InsufficientStockException if book is not available
     * @throws DatabaseException if database operation fails
     */
    Loan createLoan(Long bookId, Long memberId, int loanDays) 
            throws EntityNotFoundException, InactiveMemberException, InsufficientStockException, DatabaseException;
    
    /**
     * Processes a book return with transaction support.
     * Updates loan status and increments book stock.
     * 
     * @param loanId the loan ID
     * @return the updated loan
     * @throws EntityNotFoundException if loan not found
     * @throws DatabaseException if database operation fails
     */
    Loan returnBook(Long loanId) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Finds a loan by ID.
     * 
     * @param id the loan ID
     * @return the loan
     * @throws EntityNotFoundException if loan not found
     * @throws DatabaseException if database operation fails
     */
    Loan findLoanById(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Lists all loans.
     * 
     * @return list of all loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> listAllLoans() throws DatabaseException;
    
    /**
     * Lists all active loans for a member.
     * 
     * @param memberId the member ID
     * @return list of active loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> listActiveLoansByMember(Long memberId) throws DatabaseException;
    
    /**
     * Lists all overdue loans.
     * 
     * @return list of overdue loans
     * @throws DatabaseException if database operation fails
     */
    List<Loan> listOverdueLoans() throws DatabaseException;
    
    /**
     * Calculates fine for an overdue loan.
     * 
     * @param loanId the loan ID
     * @return fine amount
     * @throws EntityNotFoundException if loan not found
     * @throws DatabaseException if database operation fails
     */
    double calculateFine(Long loanId) throws EntityNotFoundException, DatabaseException;
}