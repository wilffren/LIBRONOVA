package com.mycompany.libronova.domain;

import java.util.Objects;

/**
 * Abstract base class for all users in the system.
 * 
 * @author Wilffren Muñoz
 */
public abstract class User {
    
    protected Long id;
    protected String name;
    protected String email;
    protected UserRole role;
    
    protected User() {
    }
    
    protected User(String name, String email, UserRole role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}