package com.mycompany.libronova.domain;

import java.time.LocalDate;

/**
 * Domain model representing a library member.
 * 
 * @author Wilffren Muñoz
 */
public class Socio extends Usuario {
    
    private EstadoSocio estado;
    private LocalDate fechaRegistro;
    private String numeroSocio;
    
    public Socio() {
        super();
        this.rol = RolUsuario.SOCIO;
    }
    
    public Socio(String nombre, String email, String numeroSocio) {
        super(nombre, email, RolUsuario.SOCIO);
        this.numeroSocio = numeroSocio;
        this.estado = EstadoSocio.ACTIVO;
        this.fechaRegistro = LocalDate.now();
    }
    
    // Getters and Setters
    public EstadoSocio getEstado() { return estado; }
    public void setEstado(EstadoSocio estado) { this.estado = estado; }
    
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { 
        this.fechaRegistro = fechaRegistro; 
    }
    
    public String getNumeroSocio() { return numeroSocio; }
    public void setNumeroSocio(String numeroSocio) { this.numeroSocio = numeroSocio; }
    
    /**
     * Checks if the member is active and can borrow books.
     * 
     * @return true if member is active
     */
    public boolean isActivo() {
        return estado == EstadoSocio.ACTIVO;
    }
    
    @Override
    public String toString() {
        return String.format("Socio{id=%d, nombre='%s', numeroSocio='%s', estado=%s}",
                id, nombre, numeroSocio, estado);
    }
}