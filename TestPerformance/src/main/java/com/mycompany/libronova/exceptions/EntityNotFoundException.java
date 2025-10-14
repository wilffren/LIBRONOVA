package com.mycompany.libronova.exceptions;

/**
 * Exception thrown when an entity is not found in the database.
 * 
 * @author Wilffren Muñoz
 */
public class EntityNotFoundException extends LibroNovaException {
    
    private final String entityType;
    private final Object identifier;
    
    public EntityNotFoundException(String entityType, Object identifier) {
        super(String.format("%s con identificador '%s' no encontrado", 
                entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Object getIdentifier() {
        return identifier;
    }
}