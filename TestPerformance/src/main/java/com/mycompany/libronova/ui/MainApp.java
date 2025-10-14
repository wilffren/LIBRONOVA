package com.mycompany.libronova.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.mycompany.libronova.infra.config.ConnectionDB;
import com.mycompany.libronova.infra.config.LoggingConfig;
import com.mycompany.libronova.repository.jdbc.*;
import com.mycompany.libronova.service.*;
import com.mycompany.libronova.service.impl.*;
import com.mycompany.libronova.domain.SystemUser;
import java.io.IOException;

/**
 * Main JavaFX Application for LibroNova.
 *
 * @author Wilffren Muñoz
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private SystemUser currentUser;

    // Services
    private BookService bookService;
    private MemberService memberService;
    private LoanService loanService;
    private ReportService reportService;
    private AuthenticationService authService;

    // Views
    private BookView bookView;
    private MemberView memberView;
    private LoanView loanView;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Initialize logging system
        LoggingConfig.initialize();
        LoggingConfig.logInfo(MainApp.class.getName(), "LibroNova application starting...");
        
        // Test database connection
        if (!testDatabaseConnection()) {
            LoggingConfig.logError(MainApp.class.getName(), "Failed to connect to database", null);
            showErrorAlert("Connection Error", 
                    "Could not connect to the database. Please check the configuration.");
            return;
        }
        
        LoggingConfig.logInfo(MainApp.class.getName(), "Database connection established successfully");
        
        // Initialize services
        initializeServices();
        LoggingConfig.logInfo(MainApp.class.getName(), "Services initialized successfully");
        
        // Show login screen first
        showLoginScreen();
    }

    /**
     * Tests database connection.
     */
    private boolean testDatabaseConnection() {
        ConnectionDB connectionDB = ConnectionDB.getInstance();
        return connectionDB.testConnection();
    }

    /**
     * Initializes all services.
     */
    private void initializeServices() {
        BookRepositoryJDBC bookRepo = new BookRepositoryJDBC();
        MemberRepositoryJDBC memberRepo = new MemberRepositoryJDBC();
        LoanRepositoryJDBC loanRepo = new LoanRepositoryJDBC();

        bookService = new BookServiceImpl(bookRepo);
        memberService = new MemberServiceImpl(memberRepo);
        loanService = new LoanServiceImpl(loanRepo, bookRepo, memberRepo);
        reportService = new ReportServiceImpl(bookService, loanService);
        authService = new AuthenticationServiceImpl();
    }

    /**
     * Initializes all views.
     */
    private void initializeViews() {
        try {
            LoggingConfig.logInfo(MainApp.class.getName(), "Initializing views...");
            
            if (bookService == null) {
                throw new IllegalStateException("BookService is null");
            }
            if (memberService == null) {
                throw new IllegalStateException("MemberService is null");
            }
            if (loanService == null) {
                throw new IllegalStateException("LoanService is null");
            }
            
            bookView = new BookView(bookService);
            LoggingConfig.logInfo(MainApp.class.getName(), "BookView initialized");
            
            memberView = new MemberView(memberService);
            LoggingConfig.logInfo(MainApp.class.getName(), "MemberView initialized");
            
            loanView = new LoanView(loanService, bookService, memberService);
            LoggingConfig.logInfo(MainApp.class.getName(), "LoanView initialized");
            
            LoggingConfig.logInfo(MainApp.class.getName(), "All views initialized successfully");
        } catch (Exception e) {
            LoggingConfig.logError(MainApp.class.getName(), "Failed to initialize views", e);
            throw e;
        }
    }

    /**
     * Shows the login screen.
     */
    private void showLoginScreen() {
        LoginView loginView = new LoginView(authService, (user) -> {
            // Callback when login is successful
            LoggingConfig.logInfo(MainApp.class.getName(), "Login successful for user: " + user.getUsername());
            showMainMenuWithUser(primaryStage, user);
        });
        loginView.show();
    }
    
    /**
     * Shows the main menu with authenticated user.
     */
    public void showMainMenuWithUser(Stage stage, SystemUser user) {
        this.primaryStage = stage;
        this.currentUser = user;
        
        // Initialize views after authentication
        initializeViews();
        LoggingConfig.logInfo(MainApp.class.getName(), "Views initialized for user: " + user.getUsername());
        
        // Show main menu
        showMainMenu();
        LoggingConfig.logInfo(MainApp.class.getName(), "LibroNova application started successfully for user: " + user.getUsername());
    }
    
    /**
     * Shows the main menu.
     */
    private void showMainMenu() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("LibroNova");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("Library Management System");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        // User info
        String userInfo = currentUser != null ? 
            "Logged in as: " + currentUser.getName() + " (" + currentUser.getRole() + ")" : 
            "No user logged in";
        Label userLabel = new Label(userInfo);
        userLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db; -fx-font-weight: bold;");

        // Menu buttons
        Button btnBooks = createMenuButton("Book Management");
        btnBooks.setOnAction(e -> {
            try {
                LoggingConfig.logInfo(MainApp.class.getName(), "Opening Book Management view");
                
                if (bookView == null) {
                    LoggingConfig.logError(MainApp.class.getName(), "BookView is null, reinitializing views", null);
                    initializeViews();
                }
                
                // Create independent stage, not owned by primary stage
                Stage newStage = new Stage();
                bookView.show(newStage);
                
                LoggingConfig.logInfo(MainApp.class.getName(), "Book Management view opened successfully");
            } catch (Exception ex) {
                LoggingConfig.logError(MainApp.class.getName(), "Error opening Book Management", ex);
                showErrorAlert("Error", "Failed to open Book Management: " + ex.getMessage());
            }
        });

        Button btnMembers = createMenuButton("Member Management");
        btnMembers.setOnAction(e -> {
            try {
                LoggingConfig.logInfo(MainApp.class.getName(), "Opening Member Management view");
                
                if (memberView == null) {
                    LoggingConfig.logError(MainApp.class.getName(), "MemberView is null, reinitializing views", null);
                    initializeViews();
                }
                
                // Create independent stage, not owned by primary stage
                Stage newStage = new Stage();
                memberView.show(newStage);
                
                LoggingConfig.logInfo(MainApp.class.getName(), "Member Management view opened successfully");
            } catch (Exception ex) {
                LoggingConfig.logError(MainApp.class.getName(), "Error opening Member Management", ex);
                showErrorAlert("Error", "Failed to open Member Management: " + ex.getMessage());
            }
        });

        Button btnLoans = createMenuButton("Loan Management");
        btnLoans.setOnAction(e -> {
            try {
                LoggingConfig.logInfo(MainApp.class.getName(), "Opening Loan Management view");
                
                if (loanView == null) {
                    LoggingConfig.logError(MainApp.class.getName(), "LoanView is null, reinitializing views", null);
                    initializeViews();
                }
                
                // Create independent stage, not owned by primary stage
                Stage newStage = new Stage();
                loanView.show(newStage);
                
                LoggingConfig.logInfo(MainApp.class.getName(), "Loan Management view opened successfully");
            } catch (Exception ex) {
                LoggingConfig.logError(MainApp.class.getName(), "Error opening Loan Management", ex);
                showErrorAlert("Error", "Failed to open Loan Management: " + ex.getMessage());
            }
        });
        
        Button btnReports = createMenuButton("Reports & Export");
        btnReports.setOnAction(e -> showReportsMenu());
        
        // User Management button (only for admins)
        Button btnUserMgmt = createMenuButton("User Management");
        btnUserMgmt.setOnAction(e -> showUserManagementDemo());
        // Enable only for admin users
        if (currentUser == null || !currentUser.isAdmin()) {
            btnUserMgmt.setDisable(true);
            btnUserMgmt.setStyle(btnUserMgmt.getStyle() + "-fx-background-color: #bdc3c7;");
        }
        
        Button btnLogout = createMenuButton("Logout");
        btnLogout.setStyle(
                "-fx-background-color: #f39c12;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 4;"
                + "-fx-border-color: transparent;"
                + "-fx-cursor: hand;"
                + "-fx-effect: none;"
        );
        btnLogout.setOnAction(e -> handleLogout());
        
        Button btnExit = createMenuButton("Exit");
        btnExit.setStyle(
                "-fx-background-color: #e74c3c;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 4;"
                + "-fx-border-color: transparent;"
                + "-fx-cursor: hand;"
                + "-fx-effect: none;"
        );
        btnExit.setOnAction(e -> primaryStage.close());

        // Layout
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel, userLabel);
        titleBox.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(15, btnBooks, btnMembers, btnLoans, btnReports, btnUserMgmt, btnLogout, btnExit);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        root.getChildren().addAll(titleBox, buttonBox);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("LibroNova - Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a styled menu button.
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(350);
        button.setPrefHeight(50);
        button.setStyle(
                "-fx-font-size: 16px; "
                + "-fx-background-color: #3498db; "
                + "-fx-text-fill: white; "
                + "-fx-background-radius: 5; "
                + "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e
                -> button.setStyle(button.getStyle() + "-fx-background-color: #2980b9;")
        );

        button.setOnMouseExited(e
                -> button.setStyle(button.getStyle().replace("-fx-background-color: #2980b9;",
                        "-fx-background-color: #3498db;"))
        );

        return button;
    }

    /**
     * Shows an error alert.
     */
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows the reports and export menu.
     */
    private void showReportsMenu() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Reports & Export");
        dialog.setHeaderText("Select an export option:");
        
        ButtonType btnBookCatalog = new ButtonType("Book Catalog CSV");
        ButtonType btnOverdueLoans = new ButtonType("Overdue Loans CSV");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getButtonTypes().setAll(btnBookCatalog, btnOverdueLoans, btnCancel);
        
        dialog.showAndWait().ifPresent(response -> {
            try {
                if (response == btnBookCatalog) {
                    String fileName = reportService.exportBookCatalog();
                    showInfoAlert("Export Successful", "Book catalog exported to: " + fileName);
                    reportService.logUserActivity("USER", "EXPORT_BOOKS_UI", "Book catalog exported via main menu");
                    
                } else if (response == btnOverdueLoans) {
                    String fileName = reportService.exportOverdueLoans();
                    showInfoAlert("Export Successful", "Overdue loans exported to: " + fileName);
                    reportService.logUserActivity("USER", "EXPORT_OVERDUE_UI", "Overdue loans exported via main menu");
                }
            } catch (Exception e) {
                reportService.logSystemError("MainApp", "Export failed", e);
                showErrorAlert("Export Failed", "Error during export: " + e.getMessage());
            }
        });
    }
    
    /**
     * Shows an info alert.
     */
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Handles user logout.
     */
    private void handleLogout() {
        if (authService != null && currentUser != null) {
            authService.logout();
            LoggingConfig.logInfo(MainApp.class.getName(), "User logged out: " + currentUser.getUsername());
        }
        
        currentUser = null;
        primaryStage.close();
        
        // Restart application with login screen
        showLoginScreen();
    }
    
    /**
     * Shows user management demonstration.
     * Demonstrates the decorator pattern and HTTP logging.
     */
    private void showUserManagementDemo() {
        if (currentUser == null || !currentUser.isAdmin()) {
            showErrorAlert("Access Denied", "Only administrators can access user management.");
            return;
        }
        
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("User Management Demo");
        dialog.setHeaderText("Demonstrate User Management Features");
        dialog.setContentText("Choose an operation to demonstrate:");
        
        ButtonType btnCreateUser = new ButtonType("Create User (Decorator)");
        ButtonType btnListUsers = new ButtonType("List All Users");
        ButtonType btnUpdateStatus = new ButtonType("Update User Status");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getButtonTypes().setAll(btnCreateUser, btnListUsers, btnUpdateStatus, btnCancel);
        
        dialog.showAndWait().ifPresent(response -> {
            try {
                if (response == btnCreateUser) {
                    demonstrateUserCreation();
                } else if (response == btnListUsers) {
                    demonstrateListUsers();
                } else if (response == btnUpdateStatus) {
                    demonstrateUpdateUserStatus();
                }
            } catch (Exception e) {
                LoggingConfig.logError(MainApp.class.getName(), "User management demo failed", e);
                showErrorAlert("Demo Failed", "Error during demonstration: " + e.getMessage());
            }
        });
    }
    
    /**
     * Demonstrates user creation with decorator pattern.
     */
    private void demonstrateUserCreation() {
        // Create a new user using the decorator pattern
        SystemUser newUser = authService.createWithDefaults(
            "Demo Assistant", 
            "demo@libronova.com", 
            "demo_user_" + System.currentTimeMillis(), 
            "password123"
        );
        
        if (newUser != null) {
            showInfoAlert("User Created", 
                "User created successfully with decorator defaults:\n" +
                "Name: " + newUser.getName() + "\n" +
                "Role: " + newUser.getRole() + " (default)\n" +
                "Status: " + newUser.getStatus() + " (default)\n" +
                "Created At: " + newUser.getCreatedAt() + " (default)\n\n" +
                "Check console for HTTP simulation logs!");
        } else {
            showErrorAlert("Creation Failed", "Failed to create user. Check console for details.");
        }
    }
    
    /**
     * Demonstrates listing all users.
     */
    private void demonstrateListUsers() {
        if (authService instanceof AuthenticationServiceImpl) {
            AuthenticationServiceImpl authImpl = (AuthenticationServiceImpl) authService;
            var users = authImpl.getAllUsers();
            
            StringBuilder userList = new StringBuilder("System Users (" + users.size() + " total):\n\n");
            users.values().forEach(user -> {
                userList.append("• ").append(user.getName())
                        .append(" (@").append(user.getUsername()).append(")")
                        .append(" - ").append(user.getRole())
                        .append(" - ").append(user.getStatus())
                        .append("\n");
            });
            
            userList.append("\nCheck console for HTTP simulation logs!");
            showInfoAlert("All Users", userList.toString());
        }
    }
    
    /**
     * Demonstrates updating user status.
     */
    private void demonstrateUpdateUserStatus() {
        // Update assistant user status as example
        SystemUser updatedUser = authService.updateUserStatus("assistant", 
            authService.getUserByUsername("assistant").getStatus() == com.mycompany.libronova.domain.UserStatus.ACTIVE ? 
            com.mycompany.libronova.domain.UserStatus.INACTIVE : com.mycompany.libronova.domain.UserStatus.ACTIVE);
        
        if (updatedUser != null) {
            showInfoAlert("Status Updated", 
                "User status updated successfully:\n" +
                "User: " + updatedUser.getName() + "\n" +
                "New Status: " + updatedUser.getStatus() + "\n\n" +
                "Check console for HTTP simulation logs!");
        } else {
            showErrorAlert("Update Failed", "Failed to update user status.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
