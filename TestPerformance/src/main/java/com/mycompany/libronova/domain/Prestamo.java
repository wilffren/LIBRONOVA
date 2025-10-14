package com.mycompany.libronova.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Domain model representing a book loan.
 * 
 * @author Wilffren Muñoz
 */
public class Prestamo {
    
    private Long id;
    private Libro libro;
    private Socio socio;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionPrevista;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
    
    public Prestamo() {
    }
    
    public Prestamo(Libro libro, Socio socio, int diasPrestamo) {
        this.libro = libro;
        this.socio = socio;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucionPrevista = fechaPrestamo.plusDays(diasPrestamo);
        this.estado = EstadoPrestamo.ACTIVO;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
    
    public Socio getSocio() { return socio; }
    public void setSocio(Socio socio) { this.socio = socio; }
    
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { 
        this.fechaPrestamo = fechaPrestamo; 
    }
    
    public LocalDate getFechaDevolucionPrevista() { return fechaDevolucionPrevista; }
    public void setFechaDevolucionPrevista(LocalDate fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }
    
    public LocalDate getFechaDevolucionReal() { return fechaDevolucionReal; }
    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }
    
    public EstadoPrestamo getEstado() { return estado; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }
    
    /**
     * Checks if the loan is overdue.
     * 
     * @return true if loan is overdue
     */
    public boolean isVencido() {
        return estado == EstadoPrestamo.ACTIVO && 
               LocalDate.now().isAfter(fechaDevolucionPrevista);
    }
    
    /**
     * Calculates days overdue.
     * 
     * @return number of days overdue, 0 if not overdue
     */
    public long diasVencidos() {
        if (!isVencido()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaDevolucionPrevista, LocalDate.now());
    }
    
    @Override
    public String toString() {
        return String.format("Prestamo{id=%d, libro='%s', socio='%s', estado=%s}",
                id, libro.getTitulo(), socio.getNombre(), estado);
    }
}