package com.mycompany.libronova.domain;

import java.util.Objects;

/**
 * Abstract base class for all users in the system.
 * 
 * @author Wilffren Muñoz
 */
public abstract class Usuario {
    
    protected Long id;
    protected String nombre;
    protected String email;
    protected RolUsuario rol;
    
    protected Usuario() {
    }
    
    protected Usuario(String nombre, String email, RolUsuario rol) {
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(email, usuario.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
