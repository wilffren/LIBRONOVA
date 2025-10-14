package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.Socio;
import com.mycompany.libronova.exceptions.*;
import java.util.List;

/**
 * Service interface for member business logic.
 * 
 * @author Wilffren Muñoz
 */
public interface SocioService {
    
    /**
     * Registers a new member in the system.
     * 
     * @param socio the member to register
     * @return the registered member
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Socio registrarSocio(Socio socio) throws ValidationException, DatabaseException;
    
    /**
     * Updates an existing member.
     * 
     * @param socio the member to update
     * @return the updated member
     * @throws EntityNotFoundException if member not found
     * @throws ValidationException if validation fails
     * @throws DatabaseException if database operation fails
     */
    Socio actualizarSocio(Socio socio) throws EntityNotFoundException, ValidationException, DatabaseException;
    
    /**
     * Finds a member by ID.
     * 
     * @param id the member ID
     * @return the member
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    Socio buscarSocioPorId(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Finds a member by member number.
     * 
     * @param numeroSocio the member number
     * @return the member
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    Socio buscarSocioPorNumero(String numeroSocio) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Lists all members.
     * 
     * @return list of all members
     * @throws DatabaseException if database operation fails
     */
    List<Socio> listarTodosLosSocios() throws DatabaseException;
    
    /**
     * Lists all active members.
     * 
     * @return list of active members
     * @throws DatabaseException if database operation fails
     */
    List<Socio> listarSociosActivos() throws DatabaseException;
    
    /**
     * Activates a member.
     * 
     * @param id the member ID
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    void activarSocio(Long id) throws EntityNotFoundException, DatabaseException;
    
    /**
     * Deactivates a member.
     * 
     * @param id the member ID
     * @throws EntityNotFoundException if member not found
     * @throws DatabaseException if database operation fails
     */
    void desactivarSocio(Long id) throws EntityNotFoundException, DatabaseException;
}