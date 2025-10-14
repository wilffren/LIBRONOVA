package com.mycompany.libronova.repository;

import com.mycompany.libronova.domain.Socio;
import com.mycompany.libronova.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Socio entity operations.
 * 
 * @author Wilffren Muñoz
 */
public interface SocioRepository {
    
    /**
     * Saves a new member to the database.
     * 
     * @param socio the member to save
     * @return the saved member with generated ID
     * @throws DatabaseException if database operation fails
     */
    Socio save(Socio socio) throws DatabaseException;
    
    /**
     * Updates an existing member.
     * 
     * @param socio the member to update
     * @return the updated member
     * @throws DatabaseException if database operation fails
     */
    Socio update(Socio socio) throws DatabaseException;
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return an Optional containing the member if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Socio> findById(Long id) throws DatabaseException;
    
    /**
     * Finds a member by member number.
     * 
     * @param numeroSocio the member number
     * @return an Optional containing the member if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Socio> findByNumeroSocio(String numeroSocio) throws DatabaseException;
    
    /**
     * Finds a member by email.
     * 
     * @param email the member email
     * @return an Optional containing the member if found
     * @throws DatabaseException if database operation fails
     */
    Optional<Socio> findByEmail(String email) throws DatabaseException;
    
    /**
     * Retrieves all members from the database.
     * 
     * @return list of all members
     * @throws DatabaseException if database operation fails
     */
    List<Socio> findAll() throws DatabaseException;
    
    /**
     * Retrieves all active members.
     * 
     * @return list of active members
     * @throws DatabaseException if database operation fails
     */
    List<Socio> findAllActivos() throws DatabaseException;
    
    /**
     * Deletes a member by ID.
     * 
     * @param id the member ID
     * @return true if deleted successfully
     * @throws DatabaseException if database operation fails
     */
    boolean deleteById(Long id) throws DatabaseException;
}