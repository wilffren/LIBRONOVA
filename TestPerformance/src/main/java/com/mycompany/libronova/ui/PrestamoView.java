package com.mycompany.libronova.ui;

import com.mycompany.libronova.domain.*;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX view for loan management with transaction support.
 * 
 * @author Wilffren Muñoz
 */
public class PrestamoView {
    
    private static final Logger LOGGER = Logger.getLogger(PrestamoView.class.getName());
    private static final int DIAS_PRESTAMO_DEFAULT = 14;
    
    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final SocioService socioService;
    
    private TableView<Prestamo> tableView;
    private ObservableList<Prestamo> prestamos;
    
    // Form fields
    private ComboBox<Libro> cmbLibro;
    private ComboBox<Socio> cmbSocio;
    private TextField txtDiasPrestamo;
    private Label lblInfoPrestamo;
    
    public PrestamoView(PrestamoService prestamoService, LibroService libroService, SocioService socioService) {
        this.prestamoService = prestamoService;
        this.libroService = libroService;
        this.socioService = socioService;
        this.prestamos = FXCollections.observableArrayList();
    }
    
    /**
     * Shows the loan management view.
     */
    public void show(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Gestión de Préstamos");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Top: Filter buttons
        root.setTop(createFilterBar());
        
        // Center: Table
        root.setCenter(createTableView());
        
        // Right: Form
        root.setRight(createFormPanel());
        
        // Bottom: Buttons
        root.setBottom(createButtonPanel(stage, parentStage));
        
        Scene scene = new Scene(root, 1300, 700);
        stage.setScene(scene);
        stage.show();
        
        // Load data
        loadComboBoxData();
        loadPrestamos();
    }
    
    /**
     * Creates the filter bar.
     */
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(Pos.CENTER_LEFT);
        
        Button btnTodos = new Button("📋 Todos");
        btnTodos.setOnAction(e -> loadPrestamos());
        
        Button btnVencidos = new Button("⚠️ Vencidos");
        btnVencidos.setOnAction(e -> loadPrestamosVencidos());
        
        Label lblInfo = new Label("Filtros:");
        lblInfo.setStyle("-fx-font-weight: bold;");
        
        filterBar.getChildren().addAll(lblInfo, btnTodos, btnVencidos);
        return filterBar;
    }
    
    /**
     * Creates the table view.
     */
    private VBox createTableView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        Label title = new Label("Lista de Préstamos");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        tableView = new TableView<>();
        tableView.setItems(prestamos);
        
        TableColumn<Prestamo, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Prestamo, String> colLibro = new TableColumn<>("Libro");
        colLibro.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getLibro().getTitulo()));
        colLibro.setPrefWidth(200);
        
        TableColumn<Prestamo, String> colSocio = new TableColumn<>("Socio");
        colSocio.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSocio().getNombre()));
        colSocio.setPrefWidth(150);
        
        TableColumn<Prestamo, String> colNumeroSocio = new TableColumn<>("Nº Socio");
        colNumeroSocio.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSocio().getNumeroSocio()));
        colNumeroSocio.setPrefWidth(100);
        
        TableColumn<Prestamo, LocalDate> colFechaPrestamo = new TableColumn<>("Fecha Préstamo");
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaPrestamo.setPrefWidth(120);
        
        TableColumn<Prestamo, LocalDate> colFechaDevPrev = new TableColumn<>("Devolución Prevista");
        colFechaDevPrev.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucionPrevista"));
        colFechaDevPrev.setPrefWidth(140);
        
        TableColumn<Prestamo, LocalDate> colFechaDevReal = new TableColumn<>("Devolución Real");
        colFechaDevReal.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucionReal"));
        colFechaDevReal.setPrefWidth(130);
        
        TableColumn<Prestamo, EstadoPrestamo> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setPrefWidth(100);
        
        // Custom cell factory for estado with colors
        colEstado.setCellFactory(column -> new TableCell<Prestamo, EstadoPrestamo>() {
            @Override
            protected void updateItem(EstadoPrestamo estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Prestamo prestamo = getTableView().getItems().get(getIndex());
                    if (prestamo.isVencido()) {
                        setText("VENCIDO");
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setText(estado.toString());
                        switch (estado) {
                            case ACTIVO:
                                setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                                break;
                            case DEVUELTO:
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                                break;
                        }
                    }
                }
            }
        });
        
        TableColumn<Prestamo, String> colMulta = new TableColumn<>("Multa");
        colMulta.setCellValueFactory(cellData -> {
            Prestamo p = cellData.getValue();
            try {
                double multa = prestamoService.calcularMulta(p.getId());
                return new javafx.beans.property.SimpleStringProperty(
                        multa > 0 ? String.format("$%.2f", multa) : "-");
            } catch (Exception ex) {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });
        colMulta.setPrefWidth(80);
        
        tableView.getColumns().addAll(colId, colLibro, colSocio, colNumeroSocio,
                colFechaPrestamo, colFechaDevPrev, colFechaDevReal, colEstado, colMulta);
        
        // Selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarInfoPrestamo(newSelection);
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
        
        Label title = new Label("Nuevo Préstamo");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        cmbLibro = new ComboBox<>();
        cmbLibro.setPrefWidth(300);
        cmbLibro.setPromptText("Seleccione un libro");
        
        cmbSocio = new ComboBox<>();
        cmbSocio.setPrefWidth(300);
        cmbSocio.setPromptText("Seleccione un socio");
        
        txtDiasPrestamo = new TextField(String.valueOf(DIAS_PRESTAMO_DEFAULT));
        txtDiasPrestamo.setPromptText("Días de préstamo");
        
        lblInfoPrestamo = new Label();
        lblInfoPrestamo.setWrapText(true);
        lblInfoPrestamo.setStyle("-fx-padding: 10; -fx-background-color: #fff; " +
                "-fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        
        // Configure combo box cell factories for better display
        cmbLibro.setCellFactory(param -> new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                if (empty || libro == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - Stock: %d", 
                            libro.getTitulo(), libro.getStockDisponible()));
                }
            }
        });
        cmbLibro.setButtonCell(new ListCell<Libro>() {
            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                if (empty || libro == null) {
                    setText(null);
                } else {
                    setText(libro.getTitulo());
                }
            }
        });
        
        cmbSocio.setCellFactory(param -> new ListCell<Socio>() {
            @Override
            protected void updateItem(Socio socio, boolean empty) {
                super.updateItem(socio, empty);
                if (empty || socio == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (%s)", 
                            socio.getNumeroSocio(), socio.getNombre(), socio.getEstado()));
                }
            }
        });
        cmbSocio.setButtonCell(new ListCell<Socio>() {
            @Override
            protected void updateItem(Socio socio, boolean empty) {
                super.updateItem(socio, empty);
                if (empty || socio == null) {
                    setText(null);
                } else {
                    setText(socio.getNombre());
                }
            }
        });
        
        formPanel.getChildren().addAll(
                title,
                new Label("Libro:"), cmbLibro,
                new Label("Socio:"), cmbSocio,
                new Label("Días de Préstamo:"), txtDiasPrestamo,
                new Separator(),
                new Label("Información:"), lblInfoPrestamo
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
        
        Button btnCrear = new Button("➕ Crear Préstamo");
        btnCrear.setOnAction(e -> crearPrestamo());
        
        Button btnDevolver = new Button("📥 Devolver Libro");
        btnDevolver.setOnAction(e -> devolverLibro());
        
        Button btnCalcularMulta = new Button("💰 Calcular Multa");
        btnCalcularMulta.setOnAction(e -> mostrarMulta());
        
        Button btnRefresh = new Button("🔄 Actualizar");
        btnRefresh.setOnAction(e -> {
            loadComboBoxData();
            loadPrestamos();
        });
        
        Button btnVolver = new Button("↩️ Volver");
        btnVolver.setOnAction(e -> {
            currentStage.close();
            parentStage.show();
        });
        
        buttonPanel.getChildren().addAll(btnCrear, btnDevolver, btnCalcularMulta, 
                btnRefresh, btnVolver);
        return buttonPanel;
    }
    
    /**
     * Loads combo box data.
     */
    private void loadComboBoxData() {
        try {
            // Load books with available stock
            cmbLibro.getItems().clear();
            cmbLibro.getItems().addAll(
                    libroService.listarTodosLosLibros().stream()
                            .filter(Libro::isDisponible)
                            .toList()
            );
            
            // Load active members
            cmbSocio.getItems().clear();
            cmbSocio.getItems().addAll(socioService.listarSociosActivos());
            
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error loading combo box data", ex);
            showError("Error al cargar datos: " + ex.getMessage());
        }
    }
    
    /**
     * Loads all loans from database.
     */
    private void loadPrestamos() {
        try {
            prestamos.clear();
            prestamos.addAll(prestamoService.listarTodosPrestamos());
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error loading loans", ex);
            showError("Error al cargar préstamos: " + ex.getMessage());
        }
    }
    
    /**
     * Loads overdue loans.
     */
    private void loadPrestamosVencidos() {
        try {
            prestamos.clear();
            prestamos.addAll(prestamoService.listarPrestamosVencidos());
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error loading overdue loans", ex);
            showError("Error al cargar préstamos vencidos: " + ex.getMessage());
        }
    }
    
    /**
     * Creates a new loan with transaction support.
     */
    private void crearPrestamo() {
        Libro libro = cmbLibro.getValue();
        Socio socio = cmbSocio.getValue();
        
        if (libro == null || socio == null) {
            showWarning("Seleccione un libro y un socio");
            return;
        }
        
        try {
            int diasPrestamo = Integer.parseInt(txtDiasPrestamo.getText().trim());
            
            if (diasPrestamo <= 0) {
                showWarning("Los días de préstamo deben ser mayor a 0");
                return;
            }
            
            Prestamo prestamo = prestamoService.crearPrestamo(
                    libro.getId(), socio.getId(), diasPrestamo);
            
            prestamos.add(0, prestamo); // Add at beginning
            showSuccess(String.format("Préstamo creado exitosamente.\n" +
                    "ID: %d\nDevolución prevista: %s", 
                    prestamo.getId(), prestamo.getFechaDevolucionPrevista()));
            
            limpiarFormulario();
            loadComboBoxData(); // Refresh to update stock
            
        } catch (NumberFormatException ex) {
            showError("Días de préstamo debe ser un número válido");
        } catch (EntityNotFoundException ex) {
            showError("Entidad no encontrada: " + ex.getMessage());
        } catch (SocioInactivoException ex) {
            showError("El socio no está activo");
        } catch (StockInsuficienteException ex) {
            showError("Stock insuficiente para el libro seleccionado");
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error creating loan", ex);
            showError("Error al crear préstamo: " + ex.getMessage());
        }
    }
    
    /**
     * Processes a book return with transaction support.
     */
    private void devolverLibro() {
        Prestamo selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un préstamo");
            return;
        }
        
        if (selected.getEstado() != EstadoPrestamo.ACTIVO) {
            showWarning("El préstamo no está activo");
            return;
        }
        
        try {
            double multa = prestamoService.calcularMulta(selected.getId());
            
            String mensaje = "¿Confirmar devolución del libro?\n\n" +
                    "Libro: " + selected.getLibro().getTitulo() + "\n" +
                    "Socio: " + selected.getSocio().getNombre();
            
            if (multa > 0) {
                mensaje += String.format("\n\nMULTA POR RETRASO: $%.2f\n" +
                        "(%.0f días de retraso)", multa, selected.diasVencidos());
            }
            
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Devolución");
            confirm.setHeaderText(null);
            confirm.setContentText(mensaje);
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        Prestamo actualizado = prestamoService.devolverLibro(selected.getId());
                        
                        int index = prestamos.indexOf(selected);
                        prestamos.set(index, actualizado);
                        
                        showSuccess("Libro devuelto exitosamente" + 
                                (multa > 0 ? String.format("\n\nMulta a cobrar: $%.2f", multa) : ""));
                        
                        loadComboBoxData(); // Refresh to update stock
                        
                    } catch (EntityNotFoundException ex) {
                        showError("Préstamo no encontrado");
                    } catch (DatabaseException ex) {
                        LOGGER.log(Level.SEVERE, "Error returning book", ex);
                        showError("Error al devolver libro: " + ex.getMessage());
                    }
                }
            });
            
        } catch (EntityNotFoundException ex) {
            showError("Préstamo no encontrado");
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error calculating fine", ex);
            showError("Error al calcular multa: " + ex.getMessage());
        }
    }
    
    /**
     * Shows fine for selected loan.
     */
    private void mostrarMulta() {
        Prestamo selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un préstamo");
            return;
        }
        
        try {
            double multa = prestamoService.calcularMulta(selected.getId());
            
            String mensaje = String.format(
                    "Préstamo ID: %d\n" +
                    "Libro: %s\n" +
                    "Socio: %s\n" +
                    "Fecha devolución prevista: %s\n" +
                    "Días de retraso: %.0f\n\n" +
                    "MULTA: $%.2f",
                    selected.getId(),
                    selected.getLibro().getTitulo(),
                    selected.getSocio().getNombre(),
                    selected.getFechaDevolucionPrevista(),
                    selected.diasVencidos(),
                    multa
            );
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cálculo de Multa");
            alert.setHeaderText("Información de Multa");
            alert.setContentText(mensaje);
            alert.showAndWait();
            
        } catch (EntityNotFoundException ex) {
            showError("Préstamo no encontrado");
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error calculating fine", ex);
            showError("Error al calcular multa: " + ex.getMessage());
        }
    }
    
    /**
     * Shows loan information.
     */
    private void mostrarInfoPrestamo(Prestamo prestamo) {
        String info = String.format(
                "ID: %d\n" +
                "Libro: %s\n" +
                "Socio: %s (%s)\n" +
                "Estado: %s\n" +
                "Préstamo: %s\n" +
                "Devolución prevista: %s\n" +
                "%s",
                prestamo.getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getSocio().getNombre(),
                prestamo.getSocio().getNumeroSocio(),
                prestamo.getEstado(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucionPrevista(),
                prestamo.isVencido() ? 
                        String.format("⚠️ VENCIDO: %.0f días", prestamo.diasVencidos()) : ""
        );
        
        lblInfoPrestamo.setText(info);
    }
    
    /**
     * Clears form fields.
     */
    private void limpiarFormulario() {
        cmbLibro.setValue(null);
        cmbSocio.setValue(null);
        txtDiasPrestamo.setText(String.valueOf(DIAS_PRESTAMO_DEFAULT));
        lblInfoPrestamo.setText("");
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