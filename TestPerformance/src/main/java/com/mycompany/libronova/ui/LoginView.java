package com.mycompany.libronova.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.mycompany.libronova.domain.SystemUser;
import com.mycompany.libronova.service.AuthenticationService;
import java.util.function.Consumer;

/**
 * Login View for user authentication.
 * 
 * @author LibroNova Team
 */
public class LoginView {
    
    private final AuthenticationService authService;
    private final Consumer<SystemUser> onLoginSuccess;
    private Stage loginStage;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label statusLabel;
    private Button loginButton;
    private Button exitButton;
    
    public LoginView(AuthenticationService authService, Consumer<SystemUser> onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
    }
    
    /**
     * Shows the login window.
     */
    public void show() {
        loginStage = new Stage();
        loginStage.setTitle("LibroNova - Login");
        loginStage.setResizable(false);
        
        VBox root = createLoginForm();
        Scene scene = new Scene(root, 400, 300);
        
        loginStage.setScene(scene);
        loginStage.show();
        
        // Set focus on username field
        usernameField.requestFocus();
    }
    
    /**
     * Creates the login form UI.
     */
    private VBox createLoginForm() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f9fa;");
        
        // Title
        Label titleLabel = new Label("LibroNova Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitleLabel = new Label("Please enter your credentials");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Form fields
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        
        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(200);
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(200);
        
        // Add form fields to grid
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);
        
        // Status label for messages
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        statusLabel.setVisible(false);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        loginButton = new Button("Login");
        loginButton.setPrefWidth(100);
        loginButton.setStyle(
            "-fx-background-color: #3498db; "
            + "-fx-text-fill: white; "
            + "-fx-font-weight: bold; "
            + "-fx-background-radius: 4; "
            + "-fx-cursor: hand;"
        );
        loginButton.setOnAction(e -> handleLogin());
        
        exitButton = new Button("Exit");
        exitButton.setPrefWidth(100);
        exitButton.setStyle(
            "-fx-background-color: #95a5a6; "
            + "-fx-text-fill: white; "
            + "-fx-font-weight: bold; "
            + "-fx-background-radius: 4; "
            + "-fx-cursor: hand;"
        );
        exitButton.setOnAction(e -> System.exit(0));
        
        buttonBox.getChildren().addAll(loginButton, exitButton);
        
        // Default credentials info
        Label infoLabel = new Label("Default credentials:\nAdmin: admin / admin123\nAssistant: assistant / assistant123");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        
        // Add components to root
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(titleBox, formGrid, statusLabel, buttonBox, infoLabel);
        
        // Handle Enter key press
        passwordField.setOnAction(e -> handleLogin());
        
        return root;
    }
    
    /**
     * Handles login attempt.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Reset status
        statusLabel.setVisible(false);
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password", true);
            return;
        }
        
        // Disable login button during authentication
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");
        
        // Attempt authentication
        SystemUser authenticatedUser = authService.authenticate(username, password);
        
        if (authenticatedUser != null) {
            showStatus("Login successful! Welcome " + authenticatedUser.getName(), false);
            
            // Close login window after short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Brief success message display
                    javafx.application.Platform.runLater(() -> {
                        loginStage.close();
                        showMainApplication(authenticatedUser);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
        } else {
            showStatus("Invalid username or password", true);
            // Re-enable login button
            loginButton.setDisable(false);
            loginButton.setText("Login");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }
    
    /**
     * Shows status messages.
     */
    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(
            "-fx-text-fill: " + (isError ? "#e74c3c" : "#27ae60") + "; -fx-font-size: 12px;"
        );
        statusLabel.setVisible(true);
    }
    
    /**
     * Shows the main application after successful login.
     */
    private void showMainApplication(SystemUser user) {
        try {
            if (onLoginSuccess != null) {
                onLoginSuccess.accept(user);
            } else {
                showErrorAlert("Application Error", "No login success handler configured");
            }
        } catch (Exception e) {
            showErrorAlert("Application Error", "Failed to start main application: " + e.getMessage());
        }
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
     * Closes the login window.
     */
    public void close() {
        if (loginStage != null) {
            loginStage.close();
        }
    }
}
