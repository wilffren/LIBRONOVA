package com.mycompany.libronova.ui;

import com.mycompany.libronova.domain.EstadoSocio;
import com.mycompany.libronova.domain.Socio;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.service.SocioService;
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
 * JavaFX view for member management.
 * 
 * @author Wilffren Muñoz
 */
public class SocioView {
    
    private static final Logger LOGGER = Logger.getLogger(SocioView.class.getName());
    private final SocioService socioService;
    
    private TableView<Socio> tableView;
    private ObservableList<Socio> socios;
    
    // Form fields
    private TextField txtNombre;
    private TextField txtEmail;
    private TextField txtNumeroSocio;
    private ComboBox<EstadoSocio> cmbEstado;
    private DatePicker dpFechaRegistro;
    
    public SocioView(SocioService socioService) {
        this.socioService = socioService;
        this.socios = FXCollections.observableArrayList();
    }
    
    /**
     * Shows the member management view.
     */
    public void show(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Gestión de Socios");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Center: Table
        root.setCenter(createTableView());
        
        // Right: Form
        root.setRight(createFormPanel());
        
        // Bottom: Buttons
        root.setBottom(createButtonPanel(stage, parentStage));
        
        Scene scene = new Scene(root, 1100, 600);
        stage.setScene(scene);
        stage.show();
        
        // Load data
        loadSocios();
    }
    
    /**
     * Creates the table view.
     */
    private VBox createTableView() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        
        Label title = new Label("Lista de Socios");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        tableView = new TableView<>();
        tableView.setItems(socios);
        
        TableColumn<Socio, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);
        
        TableColumn<Socio, String> colNumero = new TableColumn<>("Nº Socio");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroSocio"));
        colNumero.setPrefWidth(100);
        
        TableColumn<Socio, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(200);
        
        TableColumn<Socio, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);
        
        TableColumn<Socio, EstadoSocio> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setPrefWidth(100);
        
        // Custom cell factory for estado with colors
        colEstado.setCellFactory(column -> new TableCell<Socio, EstadoSocio>() {
            @Override
            protected void updateItem(EstadoSocio estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado.toString());
                    switch (estado) {
                        case ACTIVO:
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case INACTIVO:
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        case SUSPENDIDO:
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
        
        TableColumn<Socio, LocalDate> colFecha = new TableColumn<>("Fecha Registro");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colFecha.setPrefWidth(120);
        
        tableView.getColumns().addAll(colId, colNumero, colNombre, colEmail, colEstado, colFecha);
        
        // Selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillFormWithSocio(newSelection);
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
        
        Label title = new Label("Formulario de Socio");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        txtNumeroSocio = new TextField();
        txtNombre = new TextField();
        txtEmail = new TextField();
        cmbEstado = new ComboBox<>(FXCollections.observableArrayList(EstadoSocio.values()));
        cmbEstado.setValue(EstadoSocio.ACTIVO);
        dpFechaRegistro = new DatePicker(LocalDate.now());
        
        txtNumeroSocio.setPromptText("Número de socio (único)");
        txtNombre.setPromptText("Nombre completo");
        txtEmail.setPromptText("correo@ejemplo.com");
        
        formPanel.getChildren().addAll(
                title,
                new Label("Número de Socio:"), txtNumeroSocio,
                new Label("Nombre:"), txtNombre,
                new Label("Email:"), txtEmail,
                new Label("Estado:"), cmbEstado,
                new Label("Fecha Registro:"), dpFechaRegistro
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
        btnGuardar.setOnAction(e -> guardarSocio());
        
        Button btnActualizar = new Button("✏️ Actualizar");
        btnActualizar.setOnAction(e -> actualizarSocio());
        
        Button btnActivar = new Button("✅ Activar");
        btnActivar.setOnAction(e -> cambiarEstadoSocio(EstadoSocio.ACTIVO));
        
        Button btnDesactivar = new Button("❌ Desactivar");
        btnDesactivar.setOnAction(e -> cambiarEstadoSocio(EstadoSocio.INACTIVO));
        
        Button btnVolver = new Button("↩️ Volver");
        btnVolver.setOnAction(e -> {
            currentStage.close();
            parentStage.show();
        });
        
        buttonPanel.getChildren().addAll(btnNuevo, btnGuardar, btnActualizar, 
                btnActivar, btnDesactivar, btnVolver);
        return buttonPanel;
    }
    
    /**
     * Loads all members from database.
     */
    private void loadSocios() {
        try {
            socios.clear();
            socios.addAll(socioService.listarTodosLosSocios());
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error loading members", ex);
            showError("Error al cargar socios: " + ex.getMessage());
        }
    }
    
    /**
     * Saves a new member.
     */
    private void guardarSocio() {
        try {
            Socio socio = createSocioFromForm();
            socio = socioService.registrarSocio(socio);
            socios.add(socio);
            showSuccess("Socio guardado exitosamente");
            limpiarFormulario();
        } catch (ValidationException ex) {
            showError("Errores de validación:\n" + String.join("\n", ex.getErrors()));
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error saving member", ex);
            showError("Error al guardar socio: " + ex.getMessage());
        }
    }
    
    /**
     * Updates selected member.
     */
    private void actualizarSocio() {
        Socio selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un socio para actualizar");
            return;
        }
        
        try {
            Socio socio = createSocioFromForm();
            socio.setId(selected.getId());
            socio = socioService.actualizarSocio(socio);
            
            int index = socios.indexOf(selected);
            socios.set(index, socio);
            
            showSuccess("Socio actualizado exitosamente");
            limpiarFormulario();
        } catch (EntityNotFoundException ex) {
            showError("Socio no encontrado");
        } catch (ValidationException ex) {
            showError("Errores de validación:\n" + String.join("\n", ex.getErrors()));
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error updating member", ex);
            showError("Error al actualizar socio: " + ex.getMessage());
        }
    }
    
    /**
     * Changes member status.
     */
    private void cambiarEstadoSocio(EstadoSocio nuevoEstado) {
        Socio selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Seleccione un socio");
            return;
        }
        
        try {
            if (nuevoEstado == EstadoSocio.ACTIVO) {
                socioService.activarSocio(selected.getId());
            } else {
                socioService.desactivarSocio(selected.getId());
            }
            
            selected.setEstado(nuevoEstado);
            tableView.refresh();
            
            showSuccess("Estado del socio actualizado");
        } catch (EntityNotFoundException ex) {
            showError("Socio no encontrado");
        } catch (DatabaseException ex) {
            LOGGER.log(Level.SEVERE, "Error changing member status", ex);
            showError("Error al cambiar estado: " + ex.getMessage());
        }
    }
    
    /**
     * Creates a Socio object from form fields.
     */
    private Socio createSocioFromForm() throws ValidationException {
        Socio socio = new Socio();
        socio.setNumeroSocio(txtNumeroSocio.getText().trim());
        socio.setNombre(txtNombre.getText().trim());
        socio.setEmail(txtEmail.getText().trim());
        socio.setEstado(cmbEstado.getValue());
        socio.setFechaRegistro(dpFechaRegistro.getValue());
        return socio;
    }
    
    /**
     * Fills form with selected member data.
     */
    private void fillFormWithSocio(Socio socio) {
        txtNumeroSocio.setText(socio.getNumeroSocio());
        txtNombre.setText(socio.getNombre());
        txtEmail.setText(socio.getEmail());
        cmbEstado.setValue(socio.getEstado());
        dpFechaRegistro.setValue(socio.getFechaRegistro());
    }
    
    /**
     * Clears all form fields.
     */
    private void limpiarFormulario() {
        txtNumeroSocio.clear();
        txtNombre.clear();
        txtEmail.clear();
        cmbEstado.setValue(EstadoSocio.ACTIVO);
        dpFechaRegistro.setValue(LocalDate.now());
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