package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.domain.LoanStatus;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.repository.BookRepository;
import com.mycompany.libronova.repository.LoanRepository;
import com.mycompany.libronova.repository.MemberRepository;
import com.mycompany.libronova.service.LoanService;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of LoanService with business validation and transaction support.
 * 
 * @author Wilffren Muñoz
 */
public class LoanServiceImpl implements LoanService {
    
    private static final Logger LOGGER = Logger.getLogger(LoanServiceImpl.class.getName());
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    
    // Fine calculation constants
    private static final double DAILY_FINE_RATE = 1.0; // $1 per day
    
    public LoanServiceImpl(LoanRepository loanRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }
    
    @Override
    public Loan createLoan(Long bookId, Long memberId, int loanDays) 
            throws EntityNotFoundException, InactiveMemberException, InsufficientStockException, DatabaseException {
        
        // Verify book exists and is available
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book", bookId));
        
        if (!book.isAvailable()) {
            throw new InsufficientStockException(book.getIsbn(), book.getAvailableStock());
        }
        
        // Verify member exists and is active
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        
        if (!member.isActive()) {
            throw new InactiveMemberException(member.getMemberNumber());
        }
        
        // Create loan
        Loan loan = new Loan(book, member, loanDays);
        
        // Decrement book stock
        book.setAvailableStock(book.getAvailableStock() - 1);
        bookRepository.update(book);
        
        // Save loan
        loan = loanRepository.save(loan);
        
        LOGGER.info("Loan created: " + loan.getId());
        return loan;
    }
    
    @Override
    public Loan returnBook(Long loanId) throws EntityNotFoundException, DatabaseException {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan", loanId));
        
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new DatabaseException("Loan is not active");
        }
        
        // Update loan status
        loan.setStatus(LoanStatus.RETURNED);
        loan.setActualReturnDate(java.time.LocalDate.now());
        
        // Increment book stock
        var book = loan.getBook();
        book.setAvailableStock(book.getAvailableStock() + 1);
        bookRepository.update(book);
        
        // Update loan
        loan = loanRepository.update(loan);
        
        LOGGER.info("Book returned for loan: " + loanId);
        return loan;
    }
    
    @Override
    public Loan findLoanById(Long id) throws EntityNotFoundException, DatabaseException {
        return loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan", id));
    }
    
    @Override
    public List<Loan> listAllLoans() throws DatabaseException {
        return loanRepository.findAll();
    }
    
    @Override
    public List<Loan> listActiveLoansByMember(Long memberId) throws DatabaseException {
        return loanRepository.findActiveByMemberId(memberId);
    }
    
    @Override
    public List<Loan> listOverdueLoans() throws DatabaseException {
        return loanRepository.findOverdue();
    }
    
    @Override
    public double calculateFine(Long loanId) throws EntityNotFoundException, DatabaseException {
        Loan loan = findLoanById(loanId);
        
        if (!loan.isOverdue()) {
            return 0.0;
        }
        
        long overdueDays = loan.overdueDays();
        return overdueDays * DAILY_FINE_RATE;
    }
}