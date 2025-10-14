package com.mycompany.libronova.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Domain model representing a book loan.
 * 
 * @author Wilffren Muñoz
 */
public class Loan {
    
    private Long id;
    private Book book;
    private Member member;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private LoanStatus status;
    
    public Loan() {
    }
    
    public Loan(Book book, Member member, int loanDays) {
        this.book = book;
        this.member = member;
        this.loanDate = LocalDate.now();
        this.expectedReturnDate = loanDate.plusDays(loanDays);
        this.status = LoanStatus.ACTIVE;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { 
        this.loanDate = loanDate; 
    }
    
    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }
    
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
    
    /**
     * Checks if the loan is overdue.
     * 
     * @return true if loan is overdue
     */
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && 
               LocalDate.now().isAfter(expectedReturnDate);
    }
    
    /**
     * Calculates days overdue.
     * 
     * @return number of days overdue, 0 if not overdue
     */
    public long overdueDays() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(expectedReturnDate, LocalDate.now());
    }
    
    @Override
    public String toString() {
        return String.format("Loan{id=%d, book='%s', member='%s', status=%s}",
                id, book.getTitle(), member.getName(), status);
    }
}