package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.exceptions.*;
import java.util.List;

/**
 * Service interface for member business logic.
 * 
 * @author Wilffren Muñoz
 */
public interface MemberService {
    
    /**
     * Registers a new member in the system.
     * 
     * @param member the member to register
     * @return the registered member
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Member registerMember(Member member) throws ValidationException, DatabaseException;
    
    /**
     * Updates an existing member.
     * 
     * @param member the member to update
     * @return the updated member
     * @throws EntityNotFoundException if member not found
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Member updateMember(Member member) throws EntityNotFoundException, ValidationException, DatabaseException;
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return the member
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    Member findMemberById(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Finds a member by member number.
     * 
     * @param memberNumber the member number
     * @return the member
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    Member findMemberByNumber(String memberNumber) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Lists all members.
     * 
     * @return list of all members
     * @throws DatabaseException if database operation fails
     */
    List<Member> listAllMembers() throws DatabaseException;
    
    /**
     * Lists all active members.
     * 
     * @return list of active members
     * @throws DatabaseException if database operation fails
     */
    List<Member> listActiveMembers() throws DatabaseException;
    
    /**
     * Activates a member.
     * 
     * @param id the member ID
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    void activateMember(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Deactivates a member.
     * 
     * @param id the member ID
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    void deactivateMember(Long id) throws EntityNotFoundException, DatabaseException;
}