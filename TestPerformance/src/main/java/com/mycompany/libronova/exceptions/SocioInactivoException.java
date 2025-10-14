package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when an inactive member attempts to borrow a book.
 * 
 * @author Wilffren Muñoz
 */
public class SocioInactivoException extends LibroNovaException {
    
    private final String numeroSocio;
    
    public SocioInactivoException(String numeroSocio) {
        super(String.format("El socio con número '%s' no está activo", numeroSocio));
        this.numeroSocio = numeroSocio;
    }
    
    public String getNumeroSocio() {
        return numeroSocio;
    }
}