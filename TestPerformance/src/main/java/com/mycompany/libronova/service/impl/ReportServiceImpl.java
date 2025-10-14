package com.mycompany.libronova.service.impl;

import com.mycompany.libronova.exceptions.DatabaseException;
import com.mycompany.libronova.infra.config.LoggingConfig;
import com.mycompany.libronova.infra.util.CSVExporter;
import com.mycompany.libronova.service.BookService;
import com.mycompany.libronova.service.LoanService;
import com.mycompany.libronova.service.ReportService;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implementation of ReportService with CSV export and logging capabilities.
 * 
 * @author Wilffren Muñoz
 */
public class ReportServiceImpl implements ReportService {
    
    private static final Logger LOGGER = LoggingConfig.getLogger(ReportServiceImpl.class);
    
    private final BookService bookService;
    private final LoanService loanService;
    
    public ReportServiceImpl(BookService bookService, LoanService loanService) {
        this.bookService = bookService;
        this.loanService = loanService;
    }
    
    @Override
    public String exportBookCatalog() throws DatabaseException, IOException {
        LOGGER.info("Starting book catalog export");
        
        try {
            var books = bookService.listAllBooks();
            String fileName = CSVExporter.exportBookCatalog(books, null);
            
            logUserActivity("SYSTEM", "EXPORT_BOOKS", "Exported " + books.size() + " books to " + fileName);
            LOGGER.info("Book catalog export completed successfully. File: " + fileName);
            
            return fileName;
        } catch (Exception e) {
            logSystemError("ReportService", "Failed to export book catalog", e);
            throw e;
        }
    }
    
    @Override
    public String exportOverdueLoans() throws DatabaseException, IOException {
        LOGGER.info("Starting overdue loans export");
        
        try {
            var overdueLoans = loanService.listOverdueLoans();
            String fileName = CSVExporter.exportOverdueLoans(overdueLoans, null);
            
            logUserActivity("SYSTEM", "EXPORT_OVERDUE_LOANS", "Exported " + overdueLoans.size() + " overdue loans to " + fileName);
            LOGGER.info("Overdue loans export completed successfully. File: " + fileName);
            
            return fileName;
        } catch (Exception e) {
            logSystemError("ReportService", "Failed to export overdue loans", e);
            throw e;
        }
    }
    
    @Override
    public String exportAllLoans() throws DatabaseException, IOException {
        LOGGER.info("Starting all loans export");
        
        try {
            var allLoans = loanService.listAllLoans();
            
            // Create a custom method for complete loan report
            String fileName = CSVExporter.exportOverdueLoans(allLoans, 
                "all_loans_" + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                ) + ".csv");
            
            logUserActivity("SYSTEM", "EXPORT_ALL_LOANS", "Exported " + allLoans.size() + " loans to " + fileName);
            LOGGER.info("All loans export completed successfully. File: " + fileName);
            
            return fileName;
        } catch (Exception e) {
            logSystemError("ReportService", "Failed to export all loans", e);
            throw e;
        }
    }
    
    @Override
    public void logUserActivity(String userId, String action, String details) {
        String logMessage = String.format("USER_ACTIVITY - User: %s, Action: %s, Details: %s", 
            userId != null ? userId : "ANONYMOUS", action, details);
        LOGGER.info(logMessage);
    }
    
    @Override
    public void logSystemError(String component, String error, Throwable exception) {
        String logMessage = String.format("SYSTEM_ERROR - Component: %s, Error: %s", component, error);
        
        if (exception != null) {
            LOGGER.severe(logMessage + " - Exception: " + exception.getMessage());
        } else {
            LOGGER.severe(logMessage);
        }
    }
}