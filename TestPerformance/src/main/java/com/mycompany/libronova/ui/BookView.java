package com.mycompany.libronova.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.mycompany.libronova.domain.Book;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.infra.util.CSVExporter;
import com.mycompany.libronova.service.BookService;
import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX View for Book management.
 * 
 * @author Wilffren Muñoz
 */
public class BookView {
    
    private final BookService bookService;
    private Stage stage;
    private Stage parentStage;
    
    // UI Components
    private TableView<Book> bookTable;
    private ObservableList<Book> bookList;
    
    // Form fields
    private TextField txtIsbn;
    private TextField txtTitle;
    private TextField txtAuthor;
    private TextField txtPublisher;
    private TextField txtYear;
    private TextField txtAvailableStock;
    private TextField txtTotalStock;
    private TextField txtSearch;
    
    public BookView(BookService bookService) {
        this.bookService = bookService;
        this.bookList = FXCollections.observableArrayList();
    }
    
    public void show(Stage parentStage) {
        this.parentStage = parentStage;
        this.stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        
        createUI();
        loadBooks();
        
        stage.setTitle("Book Management");
        stage.setResizable(true);
        stage.show();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        
        // Top section - Title and search
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // Center section - Table
        bookTable = createBookTable();
        root.setCenter(bookTable);
        
        // Right section - Form
        VBox formSection = createFormSection();
        root.setRight(formSection);
        
        // Bottom section - Buttons
        HBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
    }
    
    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setStyle("-fx-background-color: #3498db;");
        
        Label titleLabel = new Label("Book Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        
        txtSearch = new TextField();
        txtSearch.setPromptText("Search by title...");
        txtSearch.setPrefWidth(300);
        
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> searchBooks());
        
        Button btnShowAll = new Button("Show All");
        btnShowAll.setOnAction(e -> loadBooks());
        
        searchBox.getChildren().addAll(searchLabel, txtSearch, btnSearch, btnShowAll);
        topSection.getChildren().addAll(titleLabel, searchBox);
        
        return topSection;
    }
    
    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();
        table.setItems(bookList);
        
        // Create columns
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(120);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(150);
        
        TableColumn<Book, String> publisherCol = new TableColumn<>("Publisher");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherCol.setPrefWidth(120);
        
        TableColumn<Book, Year> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearCol.setPrefWidth(80);
        
        TableColumn<Book, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableStock"));
        availableCol.setPrefWidth(80);
        
        TableColumn<Book, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalStock"));
        totalCol.setPrefWidth(80);
        
        table.getColumns().addAll(isbnCol, titleCol, authorCol, publisherCol, yearCol, availableCol, totalCol);
        
        // Handle row selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
        
        return table;
    }
    
    private VBox createFormSection() {
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(20));
        formSection.setPrefWidth(300);
        formSection.setStyle("-fx-background-color: #ecf0f1;");
        
        Label formTitle = new Label("Book Details");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Form fields
        txtIsbn = new TextField();
        txtTitle = new TextField();
        txtAuthor = new TextField();
        txtPublisher = new TextField();
        txtYear = new TextField();
        txtAvailableStock = new TextField();
        txtTotalStock = new TextField();
        
        // Add form rows
        formSection.getChildren().addAll(
            formTitle,
            new Label("ISBN:"), txtIsbn,
            new Label("Title:"), txtTitle,
            new Label("Author:"), txtAuthor,
            new Label("Publisher:"), txtPublisher,
            new Label("Year:"), txtYear,
            new Label("Available Stock:"), txtAvailableStock,
            new Label("Total Stock:"), txtTotalStock
        );
        
        return formSection;
    }
    
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(10);
        bottomSection.setPadding(new Insets(20));
        bottomSection.setAlignment(Pos.CENTER);
        
        Button btnAdd = new Button("Add Book");
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAdd.setOnAction(e -> addBook());
        
        Button btnUpdate = new Button("Update Book");
        btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnUpdate.setOnAction(e -> updateBook());
        
        Button btnDelete = new Button("Delete Book");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> deleteBook());
        
        Button btnClear = new Button("Clear Form");
        btnClear.setOnAction(e -> clearForm());
        
        Button btnExportCSV = new Button("Export to CSV");
        btnExportCSV.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        btnExportCSV.setOnAction(e -> exportBooksToCSV());
        
        Button btnBack = new Button("Back to Main Menu");
        btnBack.setOnAction(e -> {
            stage.close();
            // If parent stage exists and is not showing, show it
            if (parentStage != null && !parentStage.isShowing()) {
                parentStage.show();
            }
        });
        
        bottomSection.getChildren().addAll(btnAdd, btnUpdate, btnDelete, btnClear, btnExportCSV, btnBack);
        
        return bottomSection;
    }
    
    private void loadBooks() {
        try {
            List<Book> books = bookService.listAllBooks();
            bookList.clear();
            bookList.addAll(books);
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load books: " + e.getMessage());
        }
    }
    
    private void searchBooks() {
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            loadBooks();
            return;
        }
        
        try {
            List<Book> books = bookService.findBooksByTitle(searchTerm);
            bookList.clear();
            bookList.addAll(books);
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to search books: " + e.getMessage());
        }
    }
    
    private void addBook() {
        try {
            Book book = createBookFromForm();
            bookService.registerBook(book);
            loadBooks();
            clearForm();
            showInfo("Success", "Book added successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to add book: " + e.getMessage());
        }
    }
    
    private void updateBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarning("No Selection", "Please select a book to update.");
            return;
        }
        
        try {
            Book book = createBookFromForm();
            book.setId(selectedBook.getId());
            bookService.updateBook(book);
            loadBooks();
            clearForm();
            showInfo("Success", "Book updated successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to update book: " + e.getMessage());
        }
    }
    
    private void deleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showWarning("No Selection", "Please select a book to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Book");
        alert.setContentText("Are you sure you want to delete this book?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookService.deleteBook(selectedBook.getId());
                loadBooks();
                clearForm();
                showInfo("Success", "Book deleted successfully!");
            } catch (Exception e) {
                showError("Error", "Failed to delete book: " + e.getMessage());
            }
        }
    }
    
    private Book createBookFromForm() {
        return new Book(
            txtIsbn.getText().trim(),
            txtTitle.getText().trim(),
            txtAuthor.getText().trim(),
            txtPublisher.getText().trim(),
            Year.of(Integer.parseInt(txtYear.getText().trim())),
            Integer.parseInt(txtAvailableStock.getText().trim()),
            Integer.parseInt(txtTotalStock.getText().trim())
        );
    }
    
    private void populateForm(Book book) {
        txtIsbn.setText(book.getIsbn());
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtPublisher.setText(book.getPublisher());
        txtYear.setText(book.getYear().toString());
        txtAvailableStock.setText(book.getAvailableStock().toString());
        txtTotalStock.setText(book.getTotalStock().toString());
    }
    
    private void clearForm() {
        txtIsbn.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtPublisher.clear();
        txtYear.clear();
        txtAvailableStock.clear();
        txtTotalStock.clear();
        bookTable.getSelectionModel().clearSelection();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void exportBooksToCSV() {
        try {
            List<Book> books = bookService.listAllBooks();
            if (books.isEmpty()) {
                showWarning("No Data", "No books available to export.");
                return;
            }
            
            String fileName = CSVExporter.exportBookCatalog(books, null);
            showInfo("Export Successful", "Book catalog exported successfully to: " + fileName);
            
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to retrieve books for export: " + e.getMessage());
        } catch (IOException e) {
            showError("Export Error", "Failed to export books to CSV: " + e.getMessage());
        }
    }
}
