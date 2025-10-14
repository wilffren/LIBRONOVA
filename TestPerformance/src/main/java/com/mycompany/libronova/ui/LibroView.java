package com.mycompany.libronova.ui;

import com.mycompany.libronova.domain.Libro;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.service.LibroService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.Year;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX view for book management.
 * 
 * @author Wilffren Muñoz
 */
public class LibroView {
    
    private static final Logger LOGGER = Logger.getLogger(LibroView.class.getName());
    private final LibroService libroService;
    
    private TableView<Libro> tableView;
    private ObservableList<Libro> libros;
    
    // Form fields
    private TextField txtIsbn;
    private TextField txtTitulo;
    private TextField txtAutor;
    private TextField txtEditorial;
    private TextField txtAnio;
    private TextField txtStockDisponible;
    private TextField txtStockTotal;
    private TextField txtBuscar;
    
    public LibroView(LibroService libroService) {
        this.libroService = libroService;
        this.libros = FXCollections.observableArrayList();
    }
    
    /**
     * Shows the book management view.
     */
    public void show(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Gestión de Libros");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Top: Search bar
        root.setTop(createSearchBar());
        
        // Center: Table
        root.setCenter(createTableView());
        
        // Right: Form
        root.setRight(createFormPanel());
        
        // Bottom: Buttons
        root.setBottom(createButtonPanel(stage, parentStage));
        
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
        
        // Load data
        loadLibros();
    }
    
    /**
     * Creates the search bar.
     */
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER_LEFT);
        
        Label lblBuscar = new Label("Buscar:");
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Ingrese título del libro...");
        txtBuscar.setPrefWidth(300);
        
        Button btnBuscar = new Button("🔍 Buscar");
        btnBuscar.setOnAction(e -> buscarLibros());
        
        Button btnMostrarTodos = new Button("📋 Mostrar Todos");
        btnMostrarTodos.setOnAction(e -> loadLibros());
        
        searchBar.getChildren().addAll(lblBuscar, txtBuscar, btnBuscar, btnMostrarTodos);
        return searchBar;
    }
    
    /**
     * Creates the table view.
     */
    private VBox createTableView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        Label title = new Label("Catálogo de Libros");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        tableView = new TableView<>();
        tableView.setItems(libros);
        
        TableColumn<Libro, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Libro, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colIsbn.setPrefWidth(130);
        
        TableColumn<Libro, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(200);
        
        TableColumn<Libro, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAutor.setPrefWidth(150);
        
        TableColumn<Libro, String> colEditorial = new TableColumn<>("Editorial");
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colEditorial.setPrefWidth(120);
        
        TableColumn<Libro, Year> colAnio = new TableColumn<>("Año");
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colAnio.setPrefWidth(70);
        
        TableColumn<Libro, Integer> colStock = new TableColumn<>("Stock Disp.");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stockDisponible"));
        colStock.setPrefWidth(90);
        
        TableColumn<Libro, Integer> colStockTotal = new TableColumn<>("Stock Total");
        colStockTotal.setCellValueFactory(new PropertyValueFactory<>("stockTotal"));
        colStockTotal.setPrefWidth(90);
        
        tableView.getColumns().addAll(colId, colIsbn, colTitulo, colAutor, 
                colEditorial, colAnio, colStock, colStockTotal);
        
        // Selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFormWithLibro(newSelection);
                    }
                });
        
        box.getChildren().addAll(title, tableView);
        return box;
    }
    
    /**
     * Creates the form panel.
     */
    private VBox createFormPanel() {
        VBox formPanel = new VBox(10);
        formPanel.setPadding(new Insets(10));
        formPanel.setPrefWidth(350);
        formPanel.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        
        Label title = new Label("Formulario de Libro");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        txtIsbn = new TextField();
        txtTitulo = new TextField();
        txtAutor = new TextField();
        txtEditorial = new TextField();
        txtAnio = new TextField();
        txtStockDisponible = new TextField();
        txtStockTotal = new TextField();
        
        txtIsbn.setPromptText("ISBN (único)");
        txtTitulo.setPromptText("Título del libro");
        txtAutor.setPromptText("Autor");
        txtEditorial.setPromptText("Editorial");
        txtAnio.setPromptText("Año (ej: 2024)");
        txtStockDisponible.setPromptText("Stock disponible");
        txtStockTotal.setPromptText("Stock total");
        
        formPanel.getChildren().addAll(
                title,
                new Label("ISBN:"), txtIsbn,
                new Label("Título:"), txtTitulo,
                new Label("Autor:"), txtAutor,
                new Label("Editorial:"), txtEditorial,
                new Label("Año:"), txtAnio,
                new Label("Stock Disponible:"), txtStockDisponible,
                new Label("Stock Total:"), txtStockTotal
        );
        
        return formPanel;
    }
    
    /**
     * Creates the button panel.
     */
    private HBox createButtonPanel(Stage currentStage, Stage parentStage) {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setAlignment(Pos.CENTER);
        
        Button btnNuevo = new Button("➕ Nuevo");
        btnNuevo.setOnAction(e -> limpiarFormulario());
        
        Button btnGuardar = new Button("💾 Guardar");
        btnGuardar.setOnAction(e -> guardarLibro());
        
        Button btnActualizar = new Button("✏️ Actualizar");
        btnActualizar.setOnAction(e -> actualizarLibro());
        
        Button btnEliminar = new Button("🗑️ Eliminar");
        btnEliminar.setOnAction(e -> eliminarLibro());
        
        Button btnVolver = new Button("↩️ Volver");
        btnVolver.setOnAction(e -> {
            currentStage.close();
            parentStage.show();
        });
        
        buttonPanel.getChildren().addAll(btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnVolver);
        return buttonPanel;
    }
    
    /**
     * Loads all books from database.
     */
    private void loadLibros() {
        try {
            libros.clear();
            libros.addAll(libroService.listarTodosLosLibros());
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error loading books", ex);
            showError("Error al cargar libros: " + ex.getMessage());
        }
    }
    
    /**
     * Searches books by title.
     */
    private void buscarLibros() {
        String titulo = txtBuscar.getText().trim();
        if (titulo.isEmpty()) {
            loadLibros();
            return;
        }
        
        try {
            libros.clear();
            libros.addAll(libroService.buscarLibrosPorTitulo(titulo));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error searching books", ex);
            showError("Error al buscar libros: " + ex.getMessage());
        }
    }
    
    /**
     * Saves a new book.
     */
    private void guardarLibro() {
        try {
            Libro libro = createLibroFromForm();
            libro = libroService.registrarLibro(libro);
            libros.add(libro);
            showSuccess("Libro guardado exitosamente");
            limpiarFormulario();
        } catch (ISBNDuplicadoException ex) {
            showError("El ISBN ya está registrado");
        } catch (ValidationException ex) {
            showError("Errores de validación:\n" + String.join("\n", ex.getErrors()));
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error saving book", ex);
            showError("Error al guardar libro: " + ex.getMessage());
        }
    }
    
    /**
     * Updates selected book.
     */
    private void actualizarLibro() {
        Libro selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un libro para actualizar");
            return;
        }
        
        try {
            Libro libro = createLibroFromForm();
            libro.setId(selected.getId());
            libro = libroService.actualizarLibro(libro);
            
            int index = libros.indexOf(selected);
            libros.set(index, libro);
            
            showSuccess("Libro actualizado exitosamente");
            limpiarFormulario();
        } catch (EntityNotFoundException ex) {
            showError("Libro no encontrado");
        } catch (ValidationException ex) {
            showError("Errores de validación:\n" + String.join("\n", ex.getErrors()));
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error updating book", ex);
            showError("Error al actualizar libro: " + ex.getMessage());
        }
    }
    
    /**
     * Deletes selected book.
     */
    private void eliminarLibro() {
        Libro selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un libro para eliminar");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este libro?");
        confirm.setContentText(selected.getTitulo());
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    libroService.eliminarLibro(selected.getId());
                    libros.remove(selected);
                    showSuccess("Libro eliminado exitosamente");
                    limpiarFormulario();
                } catch (EntityNotFoundException ex) {
                    showError("Libro no encontrado");
                } catch (DatabaseException ex) {
                    LOGGER.log(Level.SEVERE, "Error deleting book", ex);
                    showError("Error al eliminar libro: " + ex.getMessage());
                }
            }
        });
    }
    
    /**
     * Creates a Libro object from form fields.
     */
    private Libro createLibroFromForm() throws ValidationException {
        try {
            Libro libro = new Libro();
            libro.setIsbn(txtIsbn.getText().trim());
            libro.setTitulo(txtTitulo.getText().trim());
            libro.setAutor(txtAutor.getText().trim());
            libro.setEditorial(txtEditorial.getText().trim());
            libro.setAnio(Year.of(Integer.parseInt(txtAnio.getText().trim())));
            libro.setStockDisponible(Integer.parseInt(txtStockDisponible.getText().trim()));
            libro.setStockTotal(Integer.parseInt(txtStockTotal.getText().trim()));
            return libro;
        } catch (NumberFormatException ex) {
            throw new ValidationException("Año y stocks deben ser números válidos");
        }
    }
    
    /**
     * Fills form with selected book data.
     */
    private void fillFormWithLibro(Libro libro) {
        txtIsbn.setText(libro.getIsbn());
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtEditorial.setText(libro.getEditorial());
        txtAnio.setText(String.valueOf(libro.getAnio().getValue()));
        txtStockDisponible.setText(String.valueOf(libro.getStockDisponible()));
        txtStockTotal.setText(String.valueOf(libro.getStockTotal()));
    }
    
    /**
     * Clears all form fields.
     */
    private void limpiarFormulario() {
        txtIsbn.clear();
        txtTitulo.clear();
        txtAutor.clear();
        txtEditorial.clear();
        txtAnio.clear();
        txtStockDisponible.clear();
        txtStockTotal.clear();
        tableView.getSelectionModel().clearSelection();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}