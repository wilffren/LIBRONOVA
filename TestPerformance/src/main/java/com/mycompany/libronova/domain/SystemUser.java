package com.mycompany.libronova.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * SystemUser entity for authentication and user management.
 * Extends User with authentication-specific properties.
 * 
 * @author LibroNova Team
 */
public class SystemUser extends User {
    
    private String username;
    private String password; // In a real system, this should be hashed
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    
    public SystemUser() {
        super();
    }
    
    public SystemUser(String name, String email, String username, String password, UserRole role) {
        super(name, email, role);
        this.username = username;
        this.password = password;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public SystemUser(String name, String email, String username, String password, UserRole role, String createdBy) {
        this(name, email, username, password, role);
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    /**
     * Validates user credentials.
     */
    public boolean validateCredentials(String username, String password) {
        return this.username != null && 
               this.password != null && 
               this.username.equals(username) && 
               this.password.equals(password) &&
               this.status == UserStatus.ACTIVE;
    }
    
    /**
     * Checks if user has admin privileges.
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
    
    /**
     * Checks if user has assistant privileges.
     */
    public boolean isAssistant() {
        return this.role == UserRole.ASSISTANT;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemUser that = (SystemUser) o;
        return Objects.equals(username, that.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
    
    @Override
    public String toString() {
        return "SystemUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}