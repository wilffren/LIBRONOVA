package com.mycompany.libronova.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.mycompany.libronova.infra.config.ConnectionDB;
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
    private LibroService libroService;
    private SocioService socioService;
    private PrestamoService prestamoService;
    
    // Views
    private LibroView libroView;
    private SocioView socioView;
    private PrestamoView prestamoView;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Test database connection
        if (!testDatabaseConnection()) {
            showErrorAlert("Error de Conexión", 
                    "No se pudo conectar a la base de datos. Verifique la configuración.");
            return;
        }
        
        // Initialize services
        initializeServices();
        
        // Initialize views
        initializeViews();
        
        // Show main menu
        showMainMenu();
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
        LibroRepositoryJDBC libroRepo = new LibroRepositoryJDBC();
        SocioRepositoryJDBC socioRepo = new SocioRepositoryJDBC();
        PrestamoRepositoryJDBC prestamoRepo = new PrestamoRepositoryJDBC();
        
        libroService = new LibroServiceImpl(libroRepo);
        socioService = new SocioServiceImpl(socioRepo);
        prestamoService = new PrestamoServiceImpl(prestamoRepo, libroRepo, socioRepo);
    }
    
    /**
     * Initializes all views.
     */
    private void initializeViews() {
        libroView = new LibroView(libroService);
        socioView = new SocioView(socioService);
        prestamoView = new PrestamoView(prestamoService, libroService, socioService);
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
        
        Label subtitleLabel = new Label("Sistema de Gestión de Bibliotecas");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        // Menu buttons
        Button btnLibros = createMenuButton("Gestión de Libros");
        btnLibros.setOnAction(e -> {
            primaryStage.hide();
            libroView.show(primaryStage);
        });
        
        Button btnSocios = createMenuButton("Gestión de Socios");
        btnSocios.setOnAction(e -> {
            primaryStage.hide();
            socioView.show(primaryStage);
        });
        
        Button btnPrestamos = createMenuButton("Gestión de Préstamos");
        btnPrestamos.setOnAction(e -> {
            primaryStage.hide();
            prestamoView.show(primaryStage);
        });
        
        Button btnSalir = createMenuButton("Salir");
        btnSalir.setStyle(btnSalir.getStyle() + "-fx-background-color: #e74c3c;");
        btnSalir.setOnAction(e -> primaryStage.close());
        
        // Layout
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox buttonBox = new VBox(15, btnLibros, btnSocios, btnPrestamos, btnSalir);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        root.getChildren().addAll(titleBox, buttonBox);
        
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("LibroNova - Sistema de Gestión de Bibliotecas");
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
            "-fx-font-size: 16px; " +
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e -> 
            button.setStyle(button.getStyle() + "-fx-background-color: #2980b9;")
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(button.getStyle().replace("-fx-background-color: #2980b9;", 
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