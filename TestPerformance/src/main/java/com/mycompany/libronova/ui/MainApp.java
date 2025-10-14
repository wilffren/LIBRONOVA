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

/**
 * Main JavaFX Application for LibroNova.
 *
 * @author Wilffren Muñoz
 */
public class MainApp extends Application {

    private Stage primaryStage;

    // Services
    private BookService bookService;
    private MemberService memberService;
    private LoanService loanService;

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
        
        // Initialize views
        initializeViews();
        LoggingConfig.logInfo(MainApp.class.getName(), "Views initialized successfully");
        
        // Show main menu
        showMainMenu();
        LoggingConfig.logInfo(MainApp.class.getName(), "LibroNova application started successfully");
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
    }

    /**
     * Initializes all views.
     */
    private void initializeViews() {
        bookView = new BookView(bookService);
        memberView = new MemberView(memberService);
        loanView = new LoanView(loanService, bookService, memberService);
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

        // Menu buttons
        Button btnBooks = createMenuButton("Book Management");
        btnBooks.setOnAction(e -> {
            primaryStage.hide();
            bookView.show(primaryStage);
        });

        Button btnMembers = createMenuButton("Member Management");
        btnMembers.setOnAction(e -> {
            primaryStage.hide();
            memberView.show(primaryStage);
        });

        Button btnLoans = createMenuButton("Loan Management");
        btnLoans.setOnAction(e -> {
            primaryStage.hide();
            loanView.show(primaryStage);
        });

        Button btnExit = new Button("Exit");
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
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(15, btnBooks, btnMembers, btnLoans, btnExit);
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

    public static void main(String[] args) {
        launch(args);
    }
}
