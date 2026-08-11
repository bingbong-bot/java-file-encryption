package com.filesecurity.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX main application for file encryption and security management
 */
public class MainApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Java File Encryption Application");

        primaryStage.setTitle("File Encryption and Security System");
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);

        BorderPane root = new BorderPane();

        // Top menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Main content area
        VBox mainContent = createMainContent();
        root.setCenter(mainContent);

        // Status bar
        Label statusBar = new Label("Ready");
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-border-top: 1px solid #ccc;");
        root.setBottom(statusBar);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.info("Application window displayed");
    }

    /**
     * Create menu bar
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        // Operations menu
        Menu operationsMenu = new Menu("Operations");
        MenuItem encryptItem = new MenuItem("Encrypt File");
        MenuItem decryptItem = new MenuItem("Decrypt File");
        operationsMenu.getItems().addAll(encryptItem, decryptItem);

        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, operationsMenu, helpMenu);
        return menuBar;
    }

    /**
     * Create main content area
     */
    private VBox createMainContent() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label title = new Label("File Encryption and Security System");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Encryption tab
        Tab encryptTab = new Tab("Encryption", createEncryptionPanel());
        encryptTab.setClosable(false);

        // Access Control tab
        Tab accessTab = new Tab("Access Control", createAccessControlPanel());
        accessTab.setClosable(false);

        // Audit Log tab
        Tab auditTab = new Tab("Audit Logs", createAuditLogPanel());
        auditTab.setClosable(false);

        tabPane.getTabs().addAll(encryptTab, accessTab, auditTab);

        vbox.getChildren().addAll(title, tabPane);
        return vbox;
    }

    /**
     * Create encryption panel
     */
    private VBox createEncryptionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label instructions = new Label("Select a file to encrypt or decrypt:");
        
        HBox filePanel = new HBox(10);
        TextField filePathField = new TextField();
        filePathField.setPrefWidth(400);
        filePathField.setEditable(false);
        
        Button browseButton = new Button("Browse...");
        browseButton.setPrefWidth(100);
        
        filePanel.getChildren().addAll(filePathField, browseButton);

        HBox actionPanel = new HBox(10);
        Button encryptButton = new Button("Encrypt");
        encryptButton.setPrefWidth(100);
        encryptButton.setStyle("-fx-padding: 10;");
        
        Button decryptButton = new Button("Decrypt");
        decryptButton.setPrefWidth(100);
        decryptButton.setStyle("-fx-padding: 10;");
        
        actionPanel.getChildren().addAll(encryptButton, decryptButton);

        TextArea logArea = new TextArea();
        logArea.setWrapText(true);
        logArea.setEditable(false);
        logArea.setPrefHeight(300);

        panel.getChildren().addAll(instructions, filePanel, actionPanel, new Separator(), logArea);
        return panel;
    }

    /**
     * Create access control panel
     */
    private VBox createAccessControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label title = new Label("Role-Based Access Control");
        title.setStyle("-fx-font-weight: bold;");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("ADMIN", "USER", "VIEWER");
        roleCombo.setValue("USER");

        TextArea permissionArea = new TextArea();
        permissionArea.setWrapText(true);
        permissionArea.setEditable(false);
        permissionArea.setPrefHeight(250);
        permissionArea.setText("Permissions will be displayed here based on selected role");

        Button checkButton = new Button("Check Permissions");
        checkButton.setPrefWidth(150);

        panel.getChildren().addAll(title, 
                new Label("Select Role:"), roleCombo, 
                new Label("Permissions:"), permissionArea, 
                checkButton);
        return panel;
    }

    /**
     * Create audit log panel
     */
    private VBox createAuditLogPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label title = new Label("Audit Logs");
        title.setStyle("-fx-font-weight: bold;");

        TextArea logArea = new TextArea();
        logArea.setWrapText(true);
        logArea.setEditable(false);
        logArea.setPrefHeight(300);
        logArea.setText("No audit logs available yet");

        Button refreshButton = new Button("Refresh");
        refreshButton.setPrefWidth(100);

        HBox buttonPanel = new HBox(10);
        buttonPanel.getChildren().add(refreshButton);

        panel.getChildren().addAll(title, logArea, buttonPanel);
        return panel;
    }

    /**
     * Show about dialog
     */
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("File Encryption and Security System");
        alert.setContentText("Version 1.0.0\n\nA secure file encryption solution with:\n" +
                "• AES-256 Encryption\n" +
                "• Role-Based Access Control\n" +
                "• Comprehensive Audit Logging");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        logger.info("Java File Encryption System - Main entry point");
        launch(args);
    }
}
