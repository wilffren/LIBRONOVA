package com.mycompany.libronova.repository;

import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Member entity operations.
 * 
 * @author Wilffren Muñoz
 */
public interface MemberRepository {
    
    /**
     * Saves a new member to the database.
     * 
     * @param member the member to save
     * @return the saved member with generated ID
     * @throws DatabaseException if database operation fails
     */
    Member save(Member member) throws DatabaseException;
    
    /**
     * Updates an existing member.
     * 
     * @param member the member to update
     * @return the updated member
     * @throws DatabaseException if database operation fails
     */
    Member update(Member member) throws DatabaseException;
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return an Optional containing the member if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Member> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a member by member number.
     * 
     * @param memberNumber the member number
     * @return an Optional containing the member if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Member> findByMemberNumber(String memberNumber) throws DatabaseException;
    
    /**
     * Finds a member by email.
     * 
     * @param email the member email
     * @return an Optional containing the member if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Member> findByEmail(String email) throws DatabaseException;
    
    /**
     * Retrieves all members from the database.
     * 
     * @return list of all members
     * @throws DatabaseException if database operation fails
     */
    List<Member> findAll() throws DatabaseException;
    
    /**
     * Retrieves all active members.
     * 
     * @return list of active members
     * @throws DatabaseException if database operation fails
     */
    List<Member> findAllActive() throws DatabaseException;
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the member ID
     * @return true if deleted successfully
     * @throws DatabaseException if database operation fails
     */
    boolean deleteById(Long id) throws DatabaseException;
}