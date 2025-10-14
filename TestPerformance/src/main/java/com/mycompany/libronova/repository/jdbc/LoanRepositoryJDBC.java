package com.mycompany.libronova.repository.jdbc;

import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.domain.LoanStatus;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.LoanRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of LoanRepository.
 * 
 * @author Wilffren Muñoz
 */
public class LoanRepositoryJDBC implements LoanRepository {
    
    private static final Logger LOGGER = Logger.getLogger(LoanRepositoryJDBC.class.getName());
    private final ConnectionDB connectionDB;
    
    public LoanRepositoryJDBC() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Loan save(Loan loan) throws DatabaseException {
        String sql = "INSERT INTO loans (book_id, member_id, loan_date, expected_return_date, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, loan.getBook().getId());
            stmt.setLong(2, loan.getMember().getId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getExpectedReturnDate()));
            stmt.setString(5, loan.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Could not save the loan");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getLong(1));
                }
            }
            
            LOGGER.info("Loan saved: " + loan.getId());
            return loan;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error saving loan", ex);
            throw new DatabaseException("Error saving loan", ex);
        }
    }
    
    @Override
    public Loan update(Loan loan) throws DatabaseException {
        String sql = "UPDATE loans SET status = ?, actual_return_date = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, loan.getStatus().name());
            
            if (loan.getActualReturnDate() != null) {
                stmt.setDate(2, Date.valueOf(loan.getActualReturnDate()));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            
            stmt.setLong(3, loan.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Loan not found for update");
            }
            
            LOGGER.info("Loan updated: " + loan.getId());
            return loan;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating loan", ex);
            throw new DatabaseException("Error updating loan", ex);
        }
    }
    
    @Override
    public Optional<Loan> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding loan by ID", ex);
            throw new DatabaseException("Error finding loan by ID", ex);
        }
    }
    
    @Override
    public List<Loan> findAll() throws DatabaseException {
        String sql = "SELECT * FROM loans ORDER BY loan_date DESC";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            
            return loans;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error listing all loans", ex);
            throw new DatabaseException("Error listing all loans", ex);
        }
    }
    
    @Override
    public List<Loan> findActiveByMemberId(Long memberId) throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE member_id = ? AND status = 'ACTIVE'";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, memberId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding active loans by member", ex);
            throw new DatabaseException("Error finding active loans by member", ex);
        }
    }
    
    @Override
    public List<Loan> findActiveByBookId(Long bookId) throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE book_id = ? AND status = 'ACTIVE'";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }
            
            return loans;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding active loans by book", ex);
            throw new DatabaseException("Error finding active loans by book", ex);
        }
    }
    
    @Override
    public List<Loan> findOverdue() throws DatabaseException {
        String sql = "SELECT * FROM loans WHERE status = 'ACTIVE' AND expected_return_date < CURDATE()";
        List<Loan> loans = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
            
            return loans;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding overdue loans", ex);
            throw new DatabaseException("Error finding overdue loans", ex);
        }
    }
    
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getLong("id"));
        // Note: In a complete implementation, we would need to fetch Book and Member objects
        // For simplicity, we're only setting basic properties
        loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
        loan.setExpectedReturnDate(rs.getDate("expected_return_date").toLocalDate());
        
        Date actualReturnDate = rs.getDate("actual_return_date");
        if (actualReturnDate != null) {
            loan.setActualReturnDate(actualReturnDate.toLocalDate());
        }
        
        loan.setStatus(LoanStatus.valueOf(rs.getString("status")));
        return loan;
    }
}