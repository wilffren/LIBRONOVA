package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.domain.EstadoSocio;
import com.mycompany.libronova.domain.Socio;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.repository.SocioRepository;
import com.mycompany.libronova.service.SocioService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of SocioService with business validation.
 * 
 * @author Wilffren Muñoz
 */
public class SocioServiceImpl implements SocioService {
    
    private static final Logger LOGGER = Logger.getLogger(SocioServiceImpl.class.getName());
    private final SocioRepository socioRepository;
    
    public SocioServiceImpl(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }
    
    @Override
    public Socio registrarSocio(Socio socio) throws ValidationException, DatabaseException {
        validarSocio(socio);
        
        // Check if email already exists
        if (socioRepository.findByEmail(socio.getEmail()).isPresent()) {
            throw new ValidationException("El email ya está registrado");
        }
        
        // Check if member number already exists
        if (socioRepository.findByNumeroSocio(socio.getNumeroSocio()).isPresent()) {
            throw new ValidationException("El número de socio ya está registrado");
        }
        
        LOGGER.info("Registrando socio: " + socio.getNumeroSocio());
        return socioRepository.save(socio);
    }
    
    @Override
    public Socio actualizarSocio(Socio socio) throws EntityNotFoundException, ValidationException, DatabaseException {
        validarSocio(socio);
        
        if (socio.getId() == null) {
            throw new ValidationException("El ID del socio es requerido para actualizar");
        }
        
        // Verify member exists
        socioRepository.findById(socio.getId())
                .orElseThrow(() -> new EntityNotFoundException("Socio", socio.getId()));
        
        LOGGER.info("Actualizando socio: " + socio.getId());
        return socioRepository.update(socio);
    }
    
    @Override
    public Socio buscarSocioPorId(Long id) throws EntityNotFoundException, DatabaseException {
        return socioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Socio", id));
    }
    
    @Override
    public Socio buscarSocioPorNumero(String numeroSocio) throws EntityNotFoundException, DatabaseException {
        return socioRepository.findByNumeroSocio(numeroSocio)
                .orElseThrow(() -> new EntityNotFoundException("Socio", numeroSocio));
    }
    
    @Override
    public List<Socio> listarTodosLosSocios() throws DatabaseException {
        return socioRepository.findAll();
    }
    
    @Override
    public List<Socio> listarSociosActivos() throws DatabaseException {
        return socioRepository.findAllActivos();
    }
    
    @Override
    public void activarSocio(Long id) throws EntityNotFoundException, DatabaseException {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Socio", id));
        
        socio.setEstado(EstadoSocio.ACTIVO);
        socioRepository.update(socio);
        
        LOGGER.info("Socio activado: " + id);
    }
    
    @Override
    public void desactivarSocio(Long id) throws EntityNotFoundException, DatabaseException {
        Socio socio = socioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Socio", id));
        
        socio.setEstado(EstadoSocio.INACTIVO);
        socioRepository.update(socio);
        
        LOGGER.info("Socio desactivado: " + id);
    }
    
    /**
     * Validates member data.
     */
    private void validarSocio(Socio socio) throws ValidationException {
        List<String> errors = new ArrayList<>();
        
        if (socio.getNombre() == null || socio.getNombre().trim().isEmpty()) {
            errors.add("El nombre es requerido");
        }
        
        if (socio.getEmail() == null || socio.getEmail().trim().isEmpty()) {
            errors.add("El email es requerido");
        } else if (!socio.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("El email no es válido");
        }
        
        if (socio.getNumeroSocio() == null || socio.getNumeroSocio().trim().isEmpty()) {
            errors.add("El número de socio es requerido");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}