package com.mycompany.libronova.ui;

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
import com.mycompany.libronova.domain.Loan;
import com.mycompany.libronova.domain.LoanStatus;
import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.infra.util.CSVExporter;
import com.mycompany.libronova.service.BookService;
import com.mycompany.libronova.service.LoanService;
import com.mycompany.libronova.service.MemberService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX View for Loan management.
 * 
 * @author Wilffren Muñoz
 */
public class LoanView {
    
    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;
    private Stage stage;
    private Stage parentStage;
    
    // UI Components
    private TableView<Loan> loanTable;
    private ObservableList<Loan> loanList;
    
    // Form fields
    private ComboBox<Book> cboBook;
    private ComboBox<Member> cboMember;
    private TextField txtLoanDays;
    private Label lblSelectedLoan;
    
    public LoanView(LoanService loanService, BookService bookService, MemberService memberService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.memberService = memberService;
        this.loanList = FXCollections.observableArrayList();
    }
    
    public void show(Stage parentStage) {
        this.parentStage = parentStage;
        this.stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        
        createUI();
        loadData();
        
        stage.setTitle("Loan Management");
        stage.setResizable(true);
        stage.show();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        
        // Top section - Title
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // Center section - Table
        loanTable = createLoanTable();
        root.setCenter(loanTable);
        
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
        topSection.setStyle("-fx-background-color: #e74c3c;");
        
        Label titleLabel = new Label("Loan Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Button btnShowAll = new Button("Show All Loans");
        btnShowAll.setOnAction(e -> loadLoans());
        
        Button btnShowActive = new Button("Show Active");
        btnShowActive.setOnAction(e -> loadActiveLoans());
        
        Button btnShowOverdue = new Button("Show Overdue");
        btnShowOverdue.setOnAction(e -> loadOverdueLoans());
        
        filterBox.getChildren().addAll(btnShowAll, btnShowActive, btnShowOverdue);
        topSection.getChildren().addAll(titleLabel, filterBox);
        
        return topSection;
    }
    
    private TableView<Loan> createLoanTable() {
        TableView<Loan> table = new TableView<>();
        table.setItems(loanList);
        
        // Create columns - Note: In a real implementation, you'd need to handle the nested objects properly
        TableColumn<Loan, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        
        TableColumn<Loan, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getBook() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBook().getTitle());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        bookCol.setPrefWidth(200);
        
        TableColumn<Loan, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getMember() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMember().getName());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        memberCol.setPrefWidth(150);
        
        TableColumn<Loan, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        loanDateCol.setPrefWidth(100);
        
        TableColumn<Loan, LocalDate> expectedReturnCol = new TableColumn<>("Expected Return");
        expectedReturnCol.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        expectedReturnCol.setPrefWidth(120);
        
        TableColumn<Loan, LocalDate> actualReturnCol = new TableColumn<>("Actual Return");
        actualReturnCol.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        actualReturnCol.setPrefWidth(110);
        
        TableColumn<Loan, LoanStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        table.getColumns().addAll(idCol, bookCol, memberCol, loanDateCol, expectedReturnCol, actualReturnCol, statusCol);
        
        // Handle row selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateSelectedLoanLabel(newSelection);
            }
        });
        
        return table;
    }
    
    private VBox createFormSection() {
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(20));
        formSection.setPrefWidth(300);
        formSection.setStyle("-fx-background-color: #ecf0f1;");
        
        Label formTitle = new Label("Create New Loan");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Form fields
        cboBook = new ComboBox<>();
        cboBook.setPrefWidth(280);
        cboBook.setPromptText("Select a book...");
        // Custom cell factory to display book title and author
        cboBook.setCellFactory(listView -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                } else {
                    setText(book.getTitle() + " - " + book.getAuthor() + " (Stock: " + book.getAvailableStock() + ")");
                }
            }
        });
        cboBook.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText("Select a book...");
                } else {
                    setText(book.getTitle() + " - " + book.getAuthor());
                }
            }
        });
        
        cboMember = new ComboBox<>();
        cboMember.setPrefWidth(280);
        cboMember.setPromptText("Select a member...");
        // Custom cell factory to display member name and number
        cboMember.setCellFactory(listView -> new ListCell<Member>() {
            @Override
            protected void updateItem(Member member, boolean empty) {
                super.updateItem(member, empty);
                if (empty || member == null) {
                    setText(null);
                } else {
                    setText(member.getName() + " (" + member.getMemberNumber() + ")");
                }
            }
        });
        cboMember.setButtonCell(new ListCell<Member>() {
            @Override
            protected void updateItem(Member member, boolean empty) {
                super.updateItem(member, empty);
                if (empty || member == null) {
                    setText("Select a member...");
                } else {
                    setText(member.getName() + " (" + member.getMemberNumber() + ")");
                }
            }
        });
        
        txtLoanDays = new TextField("14"); // Default 14 days
        txtLoanDays.setPrefWidth(280);
        
        lblSelectedLoan = new Label("No loan selected");
        lblSelectedLoan.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Add form rows
        formSection.getChildren().addAll(
            formTitle,
            new Label("Book:"), cboBook,
            new Label("Member:"), cboMember,
            new Label("Loan Days:"), txtLoanDays,
            new Separator(),
            new Label("Selected Loan:"),
            lblSelectedLoan
        );
        
        return formSection;
    }
    
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(10);
        bottomSection.setPadding(new Insets(20));
        bottomSection.setAlignment(Pos.CENTER);
        
        Button btnCreateLoan = new Button("Create Loan");
        btnCreateLoan.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnCreateLoan.setOnAction(e -> createLoan());
        
        Button btnReturnBook = new Button("Return Book");
        btnReturnBook.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnReturnBook.setOnAction(e -> returnBook());
        
        Button btnCalculateFine = new Button("Calculate Fine");
        btnCalculateFine.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnCalculateFine.setOnAction(e -> calculateFine());
        
        Button btnClear = new Button("Clear Form");
        btnClear.setOnAction(e -> clearForm());
        
        Button btnExportOverdue = new Button("Export Overdue");
        btnExportOverdue.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        btnExportOverdue.setOnAction(e -> exportOverdueLoans());
        
        Button btnBack = new Button("Back to Main Menu");
        btnBack.setOnAction(e -> {
            stage.close();
        });
        
        bottomSection.getChildren().addAll(btnCreateLoan, btnReturnBook, btnCalculateFine, btnClear, btnExportOverdue, btnBack);
        
        return bottomSection;
    }
    
    private void loadData() {
        loadBooks();
        loadMembers();
        loadLoans();
    }
    
    private void loadBooks() {
        try {
            List<Book> books = bookService.listAllBooks();
            if (books.isEmpty()) {
                cboBook.setItems(FXCollections.observableArrayList());
                cboBook.setPromptText("No books available");
            } else {
                cboBook.setItems(FXCollections.observableArrayList(books.stream()
                    .filter(book -> book.getAvailableStock() > 0) // Only show books with available stock
                    .toList()));
                cboBook.setPromptText("Select a book...");
            }
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load books: " + e.getMessage());
        }
    }
    
    private void loadMembers() {
        try {
            List<Member> members = memberService.listActiveMembers();
            if (members.isEmpty()) {
                cboMember.setItems(FXCollections.observableArrayList());
                cboMember.setPromptText("No active members available");
            } else {
                cboMember.setItems(FXCollections.observableArrayList(members));
                cboMember.setPromptText("Select a member...");
            }
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load members: " + e.getMessage());
        }
    }
    
    private void loadLoans() {
        try {
            List<Loan> loans = loanService.listAllLoans();
            loanList.clear();
            loanList.addAll(loans);
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load loans: " + e.getMessage());
        }
    }
    
    private void loadActiveLoans() {
        try {
            List<Loan> loans = loanService.listAllLoans();
            loanList.clear();
            loans.stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .forEach(loanList::add);
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load active loans: " + e.getMessage());
        }
    }
    
    private void loadOverdueLoans() {
        try {
            List<Loan> loans = loanService.listOverdueLoans();
            loanList.clear();
            loanList.addAll(loans);
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load overdue loans: " + e.getMessage());
        }
    }
    
    private void createLoan() {
        Book selectedBook = cboBook.getValue();
        Member selectedMember = cboMember.getValue();
        
        if (selectedBook == null) {
            showWarning("Book Selection Required", "Please select a book for the loan.");
            return;
        }
        
        if (selectedMember == null) {
            showWarning("Member Selection Required", "Please select a member for the loan.");
            return;
        }
        
        if (selectedBook.getAvailableStock() <= 0) {
            showWarning("Book Unavailable", "The selected book is not available (no stock).");
            return;
        }
        
        try {
            String loanDaysText = txtLoanDays.getText().trim();
            if (loanDaysText.isEmpty()) {
                showError("Invalid Input", "Please enter the number of loan days.");
                return;
            }
            
            int loanDays = Integer.parseInt(loanDaysText);
            if (loanDays <= 0 || loanDays > 365) {
                showError("Invalid Input", "Loan days must be between 1 and 365.");
                return;
            }
            
            loanService.createLoan(selectedBook.getId(), selectedMember.getId(), loanDays);
            loadLoans();
            loadBooks(); // Refresh books to show updated stock
            clearForm();
            showInfo("Success", "Loan created successfully!\nBook: " + selectedBook.getTitle() + 
                    "\nMember: " + selectedMember.getName() + " (" + selectedMember.getMemberNumber() + ")");
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter a valid number for loan days.");
        } catch (Exception e) {
            showError("Error", "Failed to create loan: " + e.getMessage());
        }
    }
    
    private void returnBook() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            showWarning("No Loan Selected", "Please select a loan from the table to return the book.");
            return;
        }
        
        if (selectedLoan.getStatus() != LoanStatus.ACTIVE) {
            showWarning("Cannot Return", "Only active loans can be returned. This loan is already: " + selectedLoan.getStatus());
            return;
        }
        
        // Confirm return
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Book Return");
        confirmDialog.setHeaderText("Return Book");
        String bookTitle = selectedLoan.getBook() != null ? selectedLoan.getBook().getTitle() : "Unknown";
        String memberName = selectedLoan.getMember() != null ? selectedLoan.getMember().getName() : "Unknown";
        confirmDialog.setContentText("Are you sure you want to return this book?\n\nBook: " + bookTitle + 
                                    "\nMember: " + memberName + 
                                    "\nLoan Date: " + selectedLoan.getLoanDate());
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    loanService.returnBook(selectedLoan.getId());
                    loadLoans();
                    loadBooks(); // Refresh books to show updated stock
                    clearForm();
                    
                    String successMessage = "Book returned successfully!\n\nBook: " + bookTitle + 
                                          "\nMember: " + memberName + 
                                          "\nReturn Date: " + java.time.LocalDate.now();
                    
                    // Check if it was overdue
                    if (selectedLoan.isOverdue()) {
                        long overdueDays = selectedLoan.overdueDays();
                        successMessage += "\n\n⚠️ Note: This book was " + overdueDays + " day(s) overdue.";
                    }
                    
                    showInfo("Return Successful", successMessage);
                } catch (Exception e) {
                    showError("Return Failed", "Failed to return book: " + e.getMessage());
                }
            }
        });
    }
    
    private void calculateFine() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        if (selectedLoan == null) {
            showWarning("No Selection", "Please select a loan to calculate fine.");
            return;
        }
        
        try {
            double fine = loanService.calculateFine(selectedLoan.getId());
            if (fine > 0) {
                showInfo("Fine Calculation", String.format("Fine amount: $%.2f", fine));
            } else {
                showInfo("Fine Calculation", "No fine applicable for this loan.");
            }
        } catch (Exception e) {
            showError("Error", "Failed to calculate fine: " + e.getMessage());
        }
    }
    
    private void updateSelectedLoanLabel(Loan loan) {
        String bookTitle = loan.getBook() != null ? loan.getBook().getTitle() : "N/A";
        String memberName = loan.getMember() != null ? loan.getMember().getName() : "N/A";
        lblSelectedLoan.setText(String.format("ID: %d - %s (by %s)", loan.getId(), bookTitle, memberName));
    }
    
    private void clearForm() {
        cboBook.setValue(null);
        cboMember.setValue(null);
        txtLoanDays.setText("14");
        loanTable.getSelectionModel().clearSelection();
        lblSelectedLoan.setText("No loan selected");
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
    
    private void exportOverdueLoans() {
        try {
            List<Loan> overdueLoans = loanService.listOverdueLoans();
            if (overdueLoans.isEmpty()) {
                showInfo("No Data", "No overdue loans found to export.");
                return;
            }
            
            String fileName = CSVExporter.exportOverdueLoans(overdueLoans, null);
            showInfo("Export Successful", "Overdue loans exported successfully to: " + fileName + "\nTotal records: " + overdueLoans.size());
            
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to retrieve overdue loans for export: " + e.getMessage());
        } catch (IOException e) {
            showError("Export Error", "Failed to export overdue loans to CSV: " + e.getMessage());
        }
    }
}
