package com.mycompany.libronova.service.decorator;

import com.mycompany.libronova.domain.SystemUser;
import com.mycompany.libronova.domain.UserRole;

/**
 * Decorator interface for user creation operations.
 * Applies the decorator pattern to add default properties to user creation.
 * 
 * @author LibroNova Team
 */
public interface UserCreationDecorator {
    
    /**
     * Creates a user with default properties applied.
     * Default properties: role: ASSISTANT, status: ACTIVE, createdAt: now()
     */
    SystemUser createWithDefaults(String name, String email, String username, String password);
    
    /**
     * Creates a user with specific role, but other defaults applied.
     */
    SystemUser createWithDefaults(String name, String email, String username, String password, UserRole role);
}