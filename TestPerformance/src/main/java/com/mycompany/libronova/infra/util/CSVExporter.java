package com.mycompany.libronova.infra.util;

import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.infra.config.LoggingConfig;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for exporting data to CSV format.
 * Provides methods to export book catalog and overdue loans.
 * 
 * @author Wilffren Muñoz
 */
public class CSVExporter {
    
    private static final Logger LOGGER = LoggingConfig.getLogger(CSVExporter.class);
    private static final String CSV_SEPARATOR = ",";
    private static final String LINE_SEPARATOR = "\n";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Exports the book catalog to a CSV file.
     * 
     * @param books list of books to export
     * @param customFileName optional custom filename (null for auto-generated)
     * @return the filename of the exported file
     * @throws IOException if file operation fails
     */
    public static String exportBookCatalog(List<Book> books, String customFileName) throws IOException {
        String fileName = customFileName != null ? customFileName : 
                "book_catalog_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".csv";
        
        LOGGER.info("Starting book catalog export to: " + fileName);
        
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("ISBN" + CSV_SEPARATOR + 
                        "Title" + CSV_SEPARATOR + 
                        "Author" + CSV_SEPARATOR + 
                        "Publisher" + CSV_SEPARATOR + 
                        "Year" + CSV_SEPARATOR + 
                        "Available Stock" + CSV_SEPARATOR + 
                        "Total Stock" + LINE_SEPARATOR);
            
            // Write book data
            for (Book book : books) {
                writer.write(escapeCSV(book.getIsbn()) + CSV_SEPARATOR +
                           escapeCSV(book.getTitle()) + CSV_SEPARATOR +
                           escapeCSV(book.getAuthor()) + CSV_SEPARATOR +
                           escapeCSV(book.getPublisher()) + CSV_SEPARATOR +
                           book.getYear().getValue() + CSV_SEPARATOR +
                           book.getAvailableStock() + CSV_SEPARATOR +
                           book.getTotalStock() + LINE_SEPARATOR);
            }
            
            LOGGER.info("Book catalog exported successfully. Records: " + books.size());
        } catch (IOException e) {
            LOGGER.severe("Failed to export book catalog: " + e.getMessage());
            throw e;
        }
        
        return fileName;
    }
    
    /**
     * Exports overdue loans to a CSV file.
     * 
     * @param overdueLoans list of overdue loans to export
     * @param customFileName optional custom filename (null for auto-generated)
     * @return the filename of the exported file
     * @throws IOException if file operation fails
     */
    public static String exportOverdueLoans(List<Loan> overdueLoans, String customFileName) throws IOException {
        String fileName = customFileName != null ? customFileName :
                "overdue_loans_" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + ".csv";
        
        LOGGER.info("Starting overdue loans export to: " + fileName);
        
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("Loan ID" + CSV_SEPARATOR +
                        "Book Title" + CSV_SEPARATOR +
                        "Book ISBN" + CSV_SEPARATOR +
                        "Member Name" + CSV_SEPARATOR +
                        "Member Number" + CSV_SEPARATOR +
                        "Loan Date" + CSV_SEPARATOR +
                        "Expected Return Date" + CSV_SEPARATOR +
                        "Days Overdue" + CSV_SEPARATOR +
                        "Status" + LINE_SEPARATOR);
            
            // Write overdue loan data
            for (Loan loan : overdueLoans) {
                String bookTitle = loan.getBook() != null ? loan.getBook().getTitle() : "N/A";
                String bookIsbn = loan.getBook() != null ? loan.getBook().getIsbn() : "N/A";
                String memberName = loan.getMember() != null ? loan.getMember().getName() : "N/A";
                String memberNumber = loan.getMember() != null ? loan.getMember().getMemberNumber() : "N/A";
                
                writer.write(loan.getId() + CSV_SEPARATOR +
                           escapeCSV(bookTitle) + CSV_SEPARATOR +
                           escapeCSV(bookIsbn) + CSV_SEPARATOR +
                           escapeCSV(memberName) + CSV_SEPARATOR +
                           escapeCSV(memberNumber) + CSV_SEPARATOR +
                           (loan.getLoanDate() != null ? loan.getLoanDate().format(DATE_FORMAT) : "N/A") + CSV_SEPARATOR +
                           (loan.getExpectedReturnDate() != null ? loan.getExpectedReturnDate().format(DATE_FORMAT) : "N/A") + CSV_SEPARATOR +
                           loan.overdueDays() + CSV_SEPARATOR +
                           loan.getStatus() + LINE_SEPARATOR);
            }
            
            LOGGER.info("Overdue loans exported successfully. Records: " + overdueLoans.size());
        } catch (IOException e) {
            LOGGER.severe("Failed to export overdue loans: " + e.getMessage());
            throw e;
        }
        
        return fileName;
    }
    
    /**
     * Escapes CSV special characters in a string.
     * 
     * @param value the string value to escape
     * @return escaped string safe for CSV
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        // If the value contains comma, quote, or newline, wrap it in quotes
        if (value.contains(CSV_SEPARATOR) || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            // Escape quotes by doubling them
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }
}