package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.Libro;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.repository.LibroRepository;
import com.mycompany.libronova.service.LibroService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of LibroService with business validation.
 * 
 * @author Wilffren Muñoz
 */
public class LibroServiceImpl implements LibroService {
    
    private static final Logger LOGGER = Logger.getLogger(LibroServiceImpl.class.getName());
    private final LibroRepository libroRepository;
    
    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }
    
    @Override
    public Libro registrarLibro(Libro libro) throws ISBNDuplicadoException, ValidationException, DatabaseException {
        validarLibro(libro);
        
        if (libro.getStockDisponible() > libro.getStockTotal()) {
            throw new ValidationException("El stock disponible no puede ser mayor al stock total");
        }
        
        LOGGER.info("Registrando libro: " + libro.getIsbn());
        return libroRepository.save(libro);
    }
    
    @Override
    public Libro actualizarLibro(Libro libro) throws EntityNotFoundException, ValidationException, DatabaseException {
        validarLibro(libro);
        
        if (libro.getId() == null) {
            throw new ValidationException("El ID del libro es requerido para actualizar");
        }
        
        // Verify book exists
        libroRepository.findById(libro.getId())
                .orElseThrow(() -> new EntityNotFoundException("Libro", libro.getId()));
        
        if (libro.getStockDisponible() > libro.getStockTotal()) {
            throw new ValidationException("El stock disponible no puede ser mayor al stock total");
        }
        
        LOGGER.info("Actualizando libro: " + libro.getId());
        return libroRepository.update(libro);
    }
    
    @Override
    public Libro buscarLibroPorId(Long id) throws EntityNotFoundException, DatabaseException {
        return libroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Libro", id));
    }
    
    @Override
    public Libro buscarLibroPorIsbn(String isbn) throws EntityNotFoundException, DatabaseException {
        return libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Libro", isbn));
    }
    
    @Override
    public List<Libro> listarTodosLosLibros() throws DatabaseException {
        return libroRepository.findAll();
    }
    
    @Override
    public List<Libro> buscarLibrosPorTitulo(String titulo) throws DatabaseException {
        if (titulo == null || titulo.trim().isEmpty()) {
            try {
                throw new ValidationException("El título de búsqueda no puede estar vacío");
            } catch (ValidationException ex) {
                System.getLogger(LibroServiceImpl.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        return libroRepository.findByTitulo(titulo);
    }
    
    @Override
    public void eliminarLibro(Long id) throws EntityNotFoundException, DatabaseException {
        // Verify book exists
        libroRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Libro", id));
        
        boolean deleted = libroRepository.deleteById(id);
        if (!deleted) {
            throw new DatabaseException("No se pudo eliminar el libro");
        }
        
        LOGGER.info("Libro eliminado: " + id);
    }
    
    /**
     * Validates book data.
     */
    private void validarLibro(Libro libro) throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        if (libro.getIsbn() == null || libro.getIsbn().trim().isEmpty()) {
            errors.add("El ISBN es requerido");
        }
        
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            errors.add("El título es requerido");
        }
        
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            errors.add("El autor es requerido");
        }
        
        if (libro.getStockDisponible() == null || libro.getStockDisponible() < 0) {
            errors.add("El stock disponible debe ser mayor o igual a 0");
        }
        
        if (libro.getStockTotal() == null || libro.getStockTotal() <= 0) {
            errors.add("El stock total debe ser mayor a 0");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
