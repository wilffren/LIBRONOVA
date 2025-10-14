package com.mycompany.libronova.service;

import com.mycompany.libronova.exceptions.DatabaseException;
import java.io.IOException;

/**
 * Service interface for generating reports and exports.
 * 
 * @author Wilffren Muñoz
 */
public interface ReportService {
    
    /**
     * Exports the complete book catalog to CSV.
     * 
     * @return the filename of the exported file
     * @throws DatabaseException if database operation fails
     * @throws IOException if file operation fails
     */
    String exportBookCatalog() throws DatabaseException, IOException;
    
    /**
     * Exports overdue loans to CSV.
     * 
     * @return the filename of the exported file
     * @throws DatabaseException if database operation fails
     * @throws IOException if file operation fails
     */
    String exportOverdueLoans() throws DatabaseException, IOException;
    
    /**
     * Exports all loans to CSV.
     * 
     * @return the filename of the exported file
     * @throws DatabaseException if database operation fails
     * @throws IOException if file operation fails
     */
    String exportAllLoans() throws DatabaseException, IOException;
    
    /**
     * Logs user activity for audit purposes.
     * 
     * @param userId the user ID (can be null for system operations)
     * @param action the action performed
     * @param details additional details about the action
     */
    void logUserActivity(String userId, String action, String details);
    
    /**
     * Logs system errors for troubleshooting.
     * 
     * @param component the component where error occurred
     * @param error the error message
     * @param exception the exception (optional)
     */
    void logSystemError(String component, String error, Throwable exception);
}