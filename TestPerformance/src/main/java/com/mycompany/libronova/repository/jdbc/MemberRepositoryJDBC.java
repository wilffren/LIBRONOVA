package com.mycompany.libronova.repository.jdbc;

import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.domain.MemberStatus;
import com.mycompany.libronova.domain.UserRole;
import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.repository.MemberRepository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JDBC implementation of MemberRepository.
 * 
 * @author Wilffren Muñoz
 */
public class MemberRepositoryJDBC implements MemberRepository {
    
    private static final Logger LOGGER = Logger.getLogger(MemberRepositoryJDBC.class.getName());
    private final ConnectionDB connectionDB;
    
    public MemberRepositoryJDBC() {
        this.connectionDB = ConnectionDB.getInstance();
    }
    
    @Override
    public Member save(Member member) throws DatabaseException {
        String sql = "INSERT INTO members (name, email, member_number, status, registration_date) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getMemberNumber());
            stmt.setString(4, member.getStatus().name());
            stmt.setDate(5, Date.valueOf(member.getRegistrationDate()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Could not save the member");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getLong(1));
                }
            }
            
            LOGGER.info("Member saved: " + member.getMemberNumber());
            return member;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error saving member", ex);
            throw new DatabaseException("Error saving member", ex);
        }
    }
    
    @Override
    public Member update(Member member) throws DatabaseException {
        String sql = "UPDATE members SET name = ?, email = ?, status = ? WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getStatus().name());
            stmt.setLong(4, member.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DatabaseException("Member not found for update");
            }
            
            LOGGER.info("Member updated: " + member.getId());
            return member;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating member", ex);
            throw new DatabaseException("Error updating member", ex);
        }
    }
    
    @Override
    public Optional<Member> findById(Long id) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding member by ID", ex);
            throw new DatabaseException("Error finding member by ID", ex);
        }
    }
    
    @Override
    public Optional<Member> findByMemberNumber(String memberNumber) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE member_number = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, memberNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding member by number", ex);
            throw new DatabaseException("Error finding member by number", ex);
        }
    }
    
    @Override
    public Optional<Member> findByEmail(String email) throws DatabaseException {
        String sql = "SELECT * FROM members WHERE email = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding member by email", ex);
            throw new DatabaseException("Error finding member by email", ex);
        }
    }
    
    @Override
    public List<Member> findAll() throws DatabaseException {
        String sql = "SELECT * FROM members ORDER BY name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
            return members;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error listing all members", ex);
            throw new DatabaseException("Error listing all members", ex);
        }
    }
    
    @Override
    public List<Member> findAllActive() throws DatabaseException {
        String sql = "SELECT * FROM members WHERE status = 'ACTIVE' ORDER BY name";
        List<Member> members = new ArrayList<>();
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
            
            return members;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error listing active members", ex);
            throw new DatabaseException("Error listing active members", ex);
        }
    }
    
    @Override
    public boolean deleteById(Long id) throws DatabaseException {
        String sql = "DELETE FROM members WHERE id = ?";
        
        try (Connection conn = connectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            LOGGER.info("Member deleted: " + id);
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting member", ex);
            throw new DatabaseException("Error deleting member", ex);
        }
    }
    
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setMemberNumber(rs.getString("member_number"));
        member.setStatus(MemberStatus.valueOf(rs.getString("status")));
        member.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        member.setRole(UserRole.MEMBER);
        return member;
    }
}