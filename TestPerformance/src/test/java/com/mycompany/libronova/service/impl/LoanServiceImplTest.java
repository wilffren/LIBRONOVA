package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.domain.LoanStatus;
import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.domain.MemberStatus;
import com.mycompany.libronova.domain.UserRole;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.exceptions.EntityNotFoundException;
import com.mycompany.libronova.exceptions.InactiveMemberException;
import com.mycompany.libronova.exceptions.InsufficientStockException;
import com.mycompany.libronova.repository.BookRepository;
import com.mycompany.libronova.repository.LoanRepository;
import com.mycompany.libronova.repository.MemberRepository;
import com.mycompany.libronova.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoanServiceImpl focusing on fine calculation and loan management.
 * 
 * @author Wilffren Muñoz
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService Fine Calculation Tests")
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;
    
    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    private LoanServiceImpl loanService;
    private Book testBook;
    private Member testMember;
    private Loan testLoan;
    
    @BeforeEach
    void setUp() {
        loanService = new LoanServiceImpl(loanRepository, bookRepository, memberRepository);
        testBook = createTestBook();
        testMember = createTestMember();
        testLoan = createTestLoan();
    }
    
    private Book createTestBook() {
        Book book = new Book();
        book.setId(1L);
        book.setIsbn("978-0123456789");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setYear(Year.of(2023));
        book.setTotalStock(10);
        book.setAvailableStock(5);
        return book;
    }
    
    private Member createTestMember() {
        Member member = new Member();
        member.setId(1L);
        member.setName("Test Member");
        member.setEmail("test@example.com");
        member.setMemberNumber("M001");
        member.setStatus(MemberStatus.ACTIVE);
        member.setRole(UserRole.MEMBER);
        member.setRegistrationDate(LocalDate.now().minusMonths(1));
        return member;
    }
    
    private Loan createTestLoan() {
        Loan loan = new Loan(testBook, testMember, 14);
        loan.setId(1L);
        loan.setLoanDate(LocalDate.now().minusDays(20)); // 20 days ago
        loan.setExpectedReturnDate(LocalDate.now().minusDays(6)); // 6 days overdue
        loan.setStatus(LoanStatus.ACTIVE);
        return loan;
    }
    
    @Test
    @DisplayName("Should create loan successfully with valid parameters")
    void shouldCreateLoanSuccessfully() throws Exception {
        // Given
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        
        // When
        Loan result = loanService.createLoan(testBook.getId(), testMember.getId(), 14);
        
        // Then
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getBook().getId());
        assertEquals(testMember.getId(), result.getMember().getId());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        verify(bookRepository).update(testBook);
        verify(loanRepository).save(any(Loan.class));
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException when book does not exist")
    void shouldThrowEntityNotFoundExceptionWhenBookDoesNotExist() throws DatabaseException {
        // Given
        Long nonExistentBookId = 999L;
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());
        
        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> loanService.createLoan(nonExistentBookId, testMember.getId(), 14));
        
        assertEquals("Book", exception.getEntityType());
        assertEquals(nonExistentBookId, exception.getIdentifier());
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException when member does not exist")
    void shouldThrowEntityNotFoundExceptionWhenMemberDoesNotExist() throws DatabaseException {
        // Given
        Long nonExistentMemberId = 999L;
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(nonExistentMemberId)).thenReturn(Optional.empty());
        
        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> loanService.createLoan(testBook.getId(), nonExistentMemberId, 14));
        
        assertEquals("Member", exception.getEntityType());
        assertEquals(nonExistentMemberId, exception.getIdentifier());
    }
    
    @Test
    @DisplayName("Should throw InsufficientStockException when book is not available")
    void shouldThrowInsufficientStockExceptionWhenBookNotAvailable() throws DatabaseException {
        // Given
        testBook.setAvailableStock(0);
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        
        // When & Then
        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
            () -> loanService.createLoan(testBook.getId(), testMember.getId(), 14));
        
        assertEquals(testBook.getIsbn(), exception.getIsbn());
        assertEquals(0, exception.getAvailableStock());
    }
    
    @Test
    @DisplayName("Should throw InactiveMemberException when member is inactive")
    void shouldThrowInactiveMemberExceptionWhenMemberInactive() throws DatabaseException {
        // Given
        testMember.setStatus(MemberStatus.INACTIVE);
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        
        // When & Then
        InactiveMemberException exception = assertThrows(InactiveMemberException.class,
            () -> loanService.createLoan(testBook.getId(), testMember.getId(), 14));
        
        assertEquals(testMember.getMemberNumber(), exception.getMemberNumber());
    }
    
    @Test
    @DisplayName("Should return book successfully")
    void shouldReturnBookSuccessfully() throws Exception {
        // Given
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        when(loanRepository.update(any(Loan.class))).thenReturn(testLoan);
        
        // When
        Loan result = loanService.returnBook(testLoan.getId());
        
        // Then
        assertEquals(LoanStatus.RETURNED, result.getStatus());
        assertNotNull(result.getActualReturnDate());
        assertEquals(LocalDate.now(), result.getActualReturnDate());
        verify(bookRepository).update(testBook);
        verify(loanRepository).update(testLoan);
    }
    
    @Test
    @DisplayName("Should calculate fine correctly for overdue loans")
    void shouldCalculateFineCorrectlyForOverdueLoans() throws Exception {
        // Given: Loan is 6 days overdue (expected return was 6 days ago)
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        
        // When
        double fine = loanService.calculateFine(testLoan.getId());
        
        // Then
        assertEquals(6.0, fine, 0.01); // $1 per day * 6 days = $6.00
    }
    
    @Test
    @DisplayName("Should return zero fine for non-overdue loans")
    void shouldReturnZeroFineForNonOverdueLoans() throws Exception {
        // Given: Loan is not overdue
        testLoan.setExpectedReturnDate(LocalDate.now().plusDays(5)); // Due in 5 days
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        
        // When
        double fine = loanService.calculateFine(testLoan.getId());
        
        // Then
        assertEquals(0.0, fine, 0.01);
    }
    
    @Test
    @DisplayName("Should return zero fine for returned loans")
    void shouldReturnZeroFineForReturnedLoans() throws Exception {
        // Given: Loan is already returned
        testLoan.setStatus(LoanStatus.RETURNED);
        testLoan.setActualReturnDate(LocalDate.now().minusDays(1));
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        
        // When
        double fine = loanService.calculateFine(testLoan.getId());
        
        // Then
        assertEquals(0.0, fine, 0.01);
    }
    
    @Test
    @DisplayName("Should calculate fine correctly for different overdue periods")
    void shouldCalculateFineCorrectlyForDifferentOverduePeriods() throws Exception {
        // Test 1 day overdue
        testLoan.setExpectedReturnDate(LocalDate.now().minusDays(1));
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        
        double fine1Day = loanService.calculateFine(testLoan.getId());
        assertEquals(1.0, fine1Day, 0.01);
        
        // Test 30 days overdue
        testLoan.setExpectedReturnDate(LocalDate.now().minusDays(30));
        
        double fine30Days = loanService.calculateFine(testLoan.getId());
        assertEquals(30.0, fine30Days, 0.01);
        
        // Test 100 days overdue
        testLoan.setExpectedReturnDate(LocalDate.now().minusDays(100));
        
        double fine100Days = loanService.calculateFine(testLoan.getId());
        assertEquals(100.0, fine100Days, 0.01);
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException when calculating fine for non-existent loan")
    void shouldThrowEntityNotFoundExceptionWhenCalculatingFineForNonExistentLoan() throws DatabaseException {
        // Given
        Long nonExistentLoanId = 999L;
        when(loanRepository.findById(nonExistentLoanId)).thenReturn(Optional.empty());
        
        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> loanService.calculateFine(nonExistentLoanId));
        
        assertEquals("Loan", exception.getEntityType());
        assertEquals(nonExistentLoanId, exception.getIdentifier());
    }
    
    @Test
    @DisplayName("Should list all loans successfully")
    void shouldListAllLoansSuccessfully() throws DatabaseException {
        // Given
        List<Loan> expectedLoans = Arrays.asList(testLoan, createTestLoan());
        when(loanRepository.findAll()).thenReturn(expectedLoans);
        
        // When
        List<Loan> result = loanService.listAllLoans();
        
        // Then
        assertEquals(expectedLoans.size(), result.size());
        assertEquals(expectedLoans, result);
    }
    
    @Test
    @DisplayName("Should list active loans by member successfully")
    void shouldListActiveLoansByMemberSuccessfully() throws DatabaseException {
        // Given
        List<Loan> expectedLoans = Arrays.asList(testLoan);
        when(loanRepository.findActiveByMemberId(testMember.getId())).thenReturn(expectedLoans);
        
        // When
        List<Loan> result = loanService.listActiveLoansByMember(testMember.getId());
        
        // Then
        assertEquals(expectedLoans.size(), result.size());
        assertEquals(expectedLoans, result);
        assertTrue(result.stream().allMatch(loan -> loan.getStatus() == LoanStatus.ACTIVE));
    }
    
    @Test
    @DisplayName("Should list overdue loans successfully")
    void shouldListOverdueLoansSuccessfully() throws DatabaseException {
        // Given
        Loan overdueLoan = createTestLoan();
        overdueLoan.setExpectedReturnDate(LocalDate.now().minusDays(5));
        List<Loan> expectedOverdueLoans = Arrays.asList(overdueLoan);
        when(loanRepository.findOverdue()).thenReturn(expectedOverdueLoans);
        
        // When
        List<Loan> result = loanService.listOverdueLoans();
        
        // Then
        assertEquals(expectedOverdueLoans.size(), result.size());
        assertEquals(expectedOverdueLoans, result);
    }
    
    @Test
    @DisplayName("Should find loan by ID successfully")
    void shouldFindLoanByIdSuccessfully() throws Exception {
        // Given
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        
        // When
        Loan result = loanService.findLoanById(testLoan.getId());
        
        // Then
        assertNotNull(result);
        assertEquals(testLoan.getId(), result.getId());
        assertEquals(testLoan.getBook().getId(), result.getBook().getId());
        assertEquals(testLoan.getMember().getId(), result.getMember().getId());
    }
    
    @Test
    @DisplayName("Should throw EntityNotFoundException when finding non-existent loan by ID")
    void shouldThrowEntityNotFoundExceptionWhenFindingNonExistentLoanById() throws DatabaseException {
        // Given
        Long nonExistentId = 999L;
        when(loanRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> loanService.findLoanById(nonExistentId));
        
        assertEquals("Loan", exception.getEntityType());
        assertEquals(nonExistentId, exception.getIdentifier());
    }
    
    @Test
    @DisplayName("Should throw DatabaseException when trying to return non-active loan")
    void shouldThrowDatabaseExceptionWhenTryingToReturnNonActiveLoan() throws DatabaseException {
        // Given
        testLoan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        
        // When & Then
        DatabaseException exception = assertThrows(DatabaseException.class,
            () -> loanService.returnBook(testLoan.getId()));
        
        assertTrue(exception.getMessage().contains("Loan is not active"));
    }
    
    @Test
    @DisplayName("Should decrease book stock when creating loan")
    void shouldDecreaseBookStockWhenCreatingLoan() throws Exception {
        // Given
        int initialStock = testBook.getAvailableStock();
        when(bookRepository.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        
        // When
        loanService.createLoan(testBook.getId(), testMember.getId(), 14);
        
        // Then
        assertEquals(initialStock - 1, testBook.getAvailableStock());
        verify(bookRepository).update(testBook);
    }
    
    @Test
    @DisplayName("Should increase book stock when returning book")
    void shouldIncreaseBookStockWhenReturningBook() throws Exception {
        // Given
        int initialStock = testBook.getAvailableStock();
        when(loanRepository.findById(testLoan.getId())).thenReturn(Optional.of(testLoan));
        when(bookRepository.update(any(Book.class))).thenReturn(testBook);
        when(loanRepository.update(any(Loan.class))).thenReturn(testLoan);
        
        // When
        loanService.returnBook(testLoan.getId());
        
        // Then
        assertEquals(initialStock + 1, testBook.getAvailableStock());
        verify(bookRepository).update(testBook);
    }
}