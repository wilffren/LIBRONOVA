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
import com.mycompany.libronova.domain.Member;
import com.mycompany.libronova.domain.MemberStatus;
import com.mycompany.libronova.exceptions.*;
import com.mycompany.libronova.service.MemberService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX View for Member management.
 * 
 * @author Wilffren Muñoz
 */
public class MemberView {
    
    private final MemberService memberService;
    private Stage stage;
    private Stage parentStage;
    
    // UI Components
    private TableView<Member> memberTable;
    private ObservableList<Member> memberList;
    
    // Form fields
    private TextField txtName;
    private TextField txtEmail;
    private TextField txtMemberNumber;
    private ComboBox<MemberStatus> cboStatus;
    private DatePicker dpRegistrationDate;
    
    public MemberView(MemberService memberService) {
        this.memberService = memberService;
        this.memberList = FXCollections.observableArrayList();
    }
    
    public void show(Stage parentStage) {
        this.parentStage = parentStage;
        this.stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        
        createUI();
        loadMembers();
        
        stage.setTitle("Member Management");
        stage.setResizable(true);
        stage.show();
    }
    
    private void createUI() {
        BorderPane root = new BorderPane();
        
        // Top section - Title
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // Center section - Table
        memberTable = createMemberTable();
        root.setCenter(memberTable);
        
        // Right section - Form
        VBox formSection = createFormSection();
        root.setRight(formSection);
        
        // Bottom section - Buttons
        HBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
        
        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
    }
    
    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setStyle("-fx-background-color: #9b59b6;");
        
        Label titleLabel = new Label("Member Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        topSection.getChildren().add(titleLabel);
        
        return topSection;
    }
    
    private TableView<Member> createMemberTable() {
        TableView<Member> table = new TableView<>();
        table.setItems(memberList);
        
        // Create columns
        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Member, String> memberNumberCol = new TableColumn<>("Member Number");
        memberNumberCol.setCellValueFactory(new PropertyValueFactory<>("memberNumber"));
        memberNumberCol.setPrefWidth(120);
        
        TableColumn<Member, MemberStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Member, LocalDate> registrationCol = new TableColumn<>("Registration Date");
        registrationCol.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        registrationCol.setPrefWidth(130);
        
        table.getColumns().addAll(nameCol, emailCol, memberNumberCol, statusCol, registrationCol);
        
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
        formSection.setPrefWidth(280);
        formSection.setStyle("-fx-background-color: #ecf0f1;");
        
        Label formTitle = new Label("Member Details");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Form fields
        txtName = new TextField();
        txtEmail = new TextField();
        txtMemberNumber = new TextField();
        txtMemberNumber.setPromptText("Enter numeric member number only");
        
        // Add real-time validation for member number
        txtMemberNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty() && !newValue.matches("\\d*")) {
                // Remove non-numeric characters
                txtMemberNumber.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        cboStatus = new ComboBox<>();
        cboStatus.setItems(FXCollections.observableArrayList(MemberStatus.values()));
        cboStatus.setValue(MemberStatus.ACTIVE);
        dpRegistrationDate = new DatePicker();
        dpRegistrationDate.setValue(LocalDate.now());
        
        // Add form rows
        formSection.getChildren().addAll(
            formTitle,
            new Label("Name:"), txtName,
            new Label("Email:"), txtEmail,
            new Label("Member Number:"), txtMemberNumber,
            new Label("Status:"), cboStatus,
            new Label("Registration Date:"), dpRegistrationDate
        );
        
        return formSection;
    }
    
    private HBox createBottomSection() {
        HBox bottomSection = new HBox(10);
        bottomSection.setPadding(new Insets(20));
        bottomSection.setAlignment(Pos.CENTER);
        
        Button btnAdd = new Button("Add Member");
        btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAdd.setOnAction(e -> addMember());
        
        Button btnUpdate = new Button("Update Member");
        btnUpdate.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        btnUpdate.setOnAction(e -> updateMember());
        
        Button btnActivate = new Button("Activate");
        btnActivate.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnActivate.setOnAction(e -> activateMember());
        
        Button btnDeactivate = new Button("Deactivate");
        btnDeactivate.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        btnDeactivate.setOnAction(e -> deactivateMember());
        
        Button btnClear = new Button("Clear Form");
        btnClear.setOnAction(e -> clearForm());
        
        Button btnBack = new Button("Back to Main Menu");
        btnBack.setOnAction(e -> {
            stage.close();
        });
        
        bottomSection.getChildren().addAll(btnAdd, btnUpdate, btnActivate, btnDeactivate, btnClear, btnBack);
        
        return bottomSection;
    }
    
    private void loadMembers() {
        try {
            List<Member> members = memberService.listAllMembers();
            memberList.clear();
            memberList.addAll(members);
        } catch (DatabaseException e) {
            showError("Database Error", "Failed to load members: " + e.getMessage());
        }
    }
    
    private void addMember() {
        try {
            Member member = createMemberFromForm();
            memberService.registerMember(member);
            loadMembers();
            clearForm();
            showInfo("Success", "Member added successfully!");
        } catch (InvalidMemberNumberException e) {
            showError("Invalid Member Number", e.getMessage() + "\n\nPlease enter only numeric characters for the member number.");
            txtMemberNumber.requestFocus();
            txtMemberNumber.selectAll();
        } catch (Exception e) {
            showError("Error", "Failed to add member: " + e.getMessage());
        }
    }
    
    private void updateMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showWarning("No Selection", "Please select a member to update.");
            return;
        }
        
        try {
            Member member = createMemberFromForm();
            member.setId(selectedMember.getId());
            memberService.updateMember(member);
            loadMembers();
            clearForm();
            showInfo("Success", "Member updated successfully!");
        } catch (InvalidMemberNumberException e) {
            showError("Invalid Member Number", e.getMessage() + "\n\nPlease enter only numeric characters for the member number.");
            txtMemberNumber.requestFocus();
            txtMemberNumber.selectAll();
        } catch (Exception e) {
            showError("Error", "Failed to update member: " + e.getMessage());
        }
    }
    
    private void activateMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showWarning("No Selection", "Please select a member to activate.");
            return;
        }
        
        try {
            memberService.activateMember(selectedMember.getId());
            loadMembers();
            showInfo("Success", "Member activated successfully!");
        } catch (Exception e) {
            showError("Error", "Failed to activate member: " + e.getMessage());
        }
    }
    
    private void deactivateMember() {
        Member selectedMember = memberTable.getSelectionModel().getSelectedItem();
        if (selectedMember == null) {
            showWarning("No Selection", "Please select a member to deactivate.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deactivation");
        alert.setHeaderText("Deactivate Member");
        alert.setContentText("Are you sure you want to deactivate this member?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                memberService.deactivateMember(selectedMember.getId());
                loadMembers();
                showInfo("Success", "Member deactivated successfully!");
            } catch (Exception e) {
                showError("Error", "Failed to deactivate member: " + e.getMessage());
            }
        }
    }
    
    private Member createMemberFromForm() throws InvalidMemberNumberException {
        String memberNumber = txtMemberNumber.getText().trim();
        
        // Validate member number - must be numeric only
        if (!memberNumber.isEmpty() && !memberNumber.matches("\\d+")) {
            throw new InvalidMemberNumberException(memberNumber);
        }
        
        Member member = new Member(
            txtName.getText().trim(),
            txtEmail.getText().trim(),
            memberNumber
        );
        member.setStatus(cboStatus.getValue());
        member.setRegistrationDate(dpRegistrationDate.getValue());
        return member;
    }
    
    private void populateForm(Member member) {
        txtName.setText(member.getName());
        txtEmail.setText(member.getEmail());
        txtMemberNumber.setText(member.getMemberNumber());
        cboStatus.setValue(member.getStatus());
        dpRegistrationDate.setValue(member.getRegistrationDate());
    }
    
    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtMemberNumber.clear();
        cboStatus.setValue(MemberStatus.ACTIVE);
        dpRegistrationDate.setValue(LocalDate.now());
        memberTable.getSelectionModel().clearSelection();
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
}
