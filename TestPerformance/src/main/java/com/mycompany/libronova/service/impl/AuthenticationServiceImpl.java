package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.SystemUser;
import com.mycompany.libronova.domain.UserRole;
import com.mycompany.libronova.domain.UserStatus;
import com.mycompany.libronova.infra.util.HTTPLogger;
import com.mycompany.libronova.service.AuthenticationService;
import com.mycompany.libronova.service.decorator.UserCreationDecorator;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of AuthenticationService with HTTP logging and decorator pattern.
 * This implementation uses an in-memory storage for demonstration purposes.
 * In a real application, this would integrate with a database.
 * 
 * @author LibroNova Team
 */
public class AuthenticationServiceImpl implements AuthenticationService, UserCreationDecorator {
    
    // In-memory user storage (for demonstration)
    private final Map<String, SystemUser> users = new ConcurrentHashMap<>();
    private SystemUser currentUser;
    
    public AuthenticationServiceImpl() {
        // Initialize with default admin user
        createDefaultUsers();
    }
    
    /**
     * Creates default users for system initialization.
     */
    private void createDefaultUsers() {
        // Create default admin user
        SystemUser admin = new SystemUser("Administrator", "admin@libronova.com", "admin", "admin123", UserRole.ADMIN, "SYSTEM");
        admin.setId(1L);
        users.put("admin", admin);
        
        // Create default assistant user
        SystemUser assistant = new SystemUser("Assistant User", "assistant@libronova.com", "assistant", "assistant123", UserRole.ASSISTANT, "SYSTEM");
        assistant.setId(2L);
        users.put("assistant", assistant);
        
        HTTPLogger.logPOST("/api/users/init", "System initialization with default users");
    }
    
    @Override
    public SystemUser authenticate(String username, String password) {
        // Log authentication attempt
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", username);
        loginData.put("password", password);
        HTTPLogger.logPOST("/api/auth/login", loginData, "User authentication attempt");
        
        SystemUser user = users.get(username);
        boolean success = user != null && user.validateCredentials(username, password);
        
        // Log authentication result
        HTTPLogger.logAuthenticationAttempt(username, success);
        
        if (success) {
            currentUser = user;
            HTTPLogger.logUserSession(username, "LOGIN");
            return user;
        }
        
        return null;
    }
    
    @Override
    public SystemUser createUser(String name, String email, String username, String password, UserRole role) {
        // Check if user already exists
        if (users.containsKey(username)) {
            HTTPLogger.logPOST("/api/users", "User creation failed - user already exists: " + username);
            return null;
        }
        
        // Create user with provided role
        SystemUser newUser = new SystemUser(name, email, username, password, role, getCurrentUserIdentifier());
        newUser.setId((long) (users.size() + 1));
        users.put(username, newUser);
        
        // Log user creation
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", role.toString());
        userData.put("password", password);
        HTTPLogger.logPOST("/api/users", userData, "New user created with role: " + role);
        
        return newUser;
    }
    
    @Override
    public SystemUser createWithDefaults(String name, String email, String username, String password) {
        return createWithDefaults(name, email, username, password, UserRole.ASSISTANT);
    }
    
    @Override
    public SystemUser createWithDefaults(String name, String email, String username, String password, UserRole role) {
        // Apply decorator pattern: add default properties
        // Default properties: role: ASSISTANT (if not specified), status: ACTIVE, createdAt: now()
        
        HTTPLogger.logPOST("/api/users/with-defaults", "Creating user with default properties applied");
        
        if (users.containsKey(username)) {
            HTTPLogger.logPOST("/api/users/with-defaults", "User creation failed - user already exists: " + username);
            return null;
        }
        
        // Create user with defaults applied through decorator
        SystemUser newUser = new SystemUser(name, email, username, password, role, getCurrentUserIdentifier());
        newUser.setId((long) (users.size() + 1));
        // Status is already set to ACTIVE by default in constructor
        // createdAt is already set to now() by default in constructor
        
        users.put(username, newUser);
        
        // Log user creation with decorator details
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", role.toString());
        userData.put("status", "ACTIVE"); // Default applied
        userData.put("createdAt", LocalDateTime.now().toString()); // Default applied
        userData.put("password", password);
        HTTPLogger.logPOST("/api/users/with-defaults", userData, "User created with decorator defaults applied");
        
        return newUser;
    }
    
    @Override
    public SystemUser getCurrentUser() {
        return currentUser;
    }
    
    @Override
    public void logout() {
        if (currentUser != null) {
            String username = currentUser.getUsername();
            HTTPLogger.logPOST("/api/auth/logout", "User logout");
            HTTPLogger.logUserSession(username, "LOGOUT");
            currentUser = null;
        }
    }
    
    @Override
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    @Override
    public boolean isAssistant() {
        return currentUser != null && currentUser.isAssistant();
    }
    
    @Override
    public SystemUser updateUserStatus(String username, UserStatus status) {
        SystemUser user = users.get(username);
        if (user != null) {
            UserStatus oldStatus = user.getStatus();
            user.setStatus(status);
            
            Map<String, String> updateData = new HashMap<>();
            updateData.put("username", username);
            updateData.put("oldStatus", oldStatus.toString());
            updateData.put("newStatus", status.toString());
            HTTPLogger.logPATCH("/api/users/" + username + "/status", updateData, "User status updated");
            
            return user;
        }
        
        HTTPLogger.logPATCH("/api/users/" + username + "/status", "User status update failed - user not found");
        return null;
    }
    
    @Override
    public SystemUser getUserByUsername(String username) {
        HTTPLogger.logGET("/api/users/" + username, "Retrieve user by username");
        return users.get(username);
    }
    
    @Override
    public boolean deleteUser(String username) {
        boolean existed = users.containsKey(username);
        if (existed) {
            users.remove(username);
            HTTPLogger.logDELETE("/api/users/" + username, "User deleted successfully");
        } else {
            HTTPLogger.logDELETE("/api/users/" + username, "User deletion failed - user not found");
        }
        return existed;
    }
    
    /**
     * Gets the current user identifier for audit purposes.
     */
    private String getCurrentUserIdentifier() {
        return currentUser != null ? currentUser.getUsername() : "SYSTEM";
    }
    
    /**
     * Gets all users (for admin purposes).
     */
    public Map<String, SystemUser> getAllUsers() {
        HTTPLogger.logGET("/api/users", "Retrieve all users");
        return new HashMap<>(users);
    }
    
    /**
     * Gets user count.
     */
    public int getUserCount() {
        return users.size();
    }
}
