package com.mycompany.libronova.service;

import com.mycompany.libronova.domain.SystemUser;
import com.mycompany.libronova.domain.UserRole;
import com.mycompany.libronova.domain.UserStatus;

/**
 * Interface for authentication service operations.
 * 
 * @author LibroNova Team
 */
public interface AuthenticationService {
    
    /**
     * Authenticates a user with username and password.
     */
    SystemUser authenticate(String username, String password);
    
    /**
     * Creates a new system user.
     */
    SystemUser createUser(String name, String email, String username, String password, UserRole role);
    
    /**
     * Gets the currently authenticated user.
     */
    SystemUser getCurrentUser();
    
    /**
     * Logs out the current user.
     */
    void logout();
    
    /**
     * Checks if user has admin role.
     */
    boolean isAdmin();
    
    /**
     * Checks if user has assistant role.
     */
    boolean isAssistant();
    
    /**
     * Updates user status.
     */
    SystemUser updateUserStatus(String username, UserStatus status);
    
    /**
     * Gets user by username.
     */
    SystemUser getUserByUsername(String username);
    
    /**
     * Deletes a user.
     */
    boolean deleteUser(String username);
    
    /**
     * Creates a user with default properties applied (decorator pattern).
     * Default properties: role: ASSISTANT, status: ACTIVE, createdAt: now()
     */
    SystemUser createWithDefaults(String name, String email, String username, String password);
    
    /**
     * Creates a user with specific role, but other defaults applied (decorator pattern).
     */
    SystemUser createWithDefaults(String name, String email, String username, String password, UserRole role);
}
