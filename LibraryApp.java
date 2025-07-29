package com.example.application;

import com.example.entities.Book;
import com.example.services.BookService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LibraryApp extends Application {

    private TableView<Book> tableView;
    private ObservableList<Book> bookList;

    private TextField isbnField, titleField, authorField, categoryField, quantityField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("📚 Library Management System");

        // Input Fields
        isbnField = new TextField(); isbnField.setPromptText("ISBN");
        titleField = new TextField(); titleField.setPromptText("Title");
        authorField = new TextField(); authorField.setPromptText("Author");
        categoryField = new TextField(); categoryField.setPromptText("Category");
        quantityField = new TextField(); quantityField.setPromptText("Quantity");

        // Table Setup
        tableView = new TableView<>();
        bookList = FXCollections.observableArrayList(BookService.getAllBooksList());
        tableView.setItems(bookList);

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Book, String> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));

        tableView.getColumns().addAll(isbnCol, titleCol, authorCol, categoryCol, quantityCol);
        tableView.setPrefHeight(300);

        // Buttons
        Button addBtn = new Button("Add Book");
        Button updateBtn = new Button("Update Book");
        Button deleteBtn = new Button("Delete Book");
        Button searchBtn = new Button("Search");
        Button issueBtn = new Button("Issue Book");
        Button returnBtn = new Button("Return Book");
        Button refreshBtn = new Button("Refresh");

        addBtn.setOnAction(e -> {
            try {
                BookService.createBook(
                        isbnField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        categoryField.getText(),
                        Integer.parseInt(quantityField.getText())
                );
                clearFields();
                refreshTable();
            } catch (Exception ex) {
                showAlert("Error", "Invalid input or Book already exists.");
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                Book book = new Book(
                        isbnField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        categoryField.getText(),
                        Integer.parseInt(quantityField.getText())
                );
                BookService.updateBook(book);
                clearFields();
                refreshTable();
            } catch (Exception ex) {
                showAlert("Update Failed", ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            try {
                BookService.deleteBook(isbnField.getText());
                clearFields();
                refreshTable();
            } catch (Exception ex) {
                showAlert("Delete Failed", ex.getMessage());
            }
        });

        searchBtn.setOnAction(e -> {
            try {
                Book result = BookService.searchByTitleOrAuthorOrCategory(
                        titleField.getText(),
                        authorField.getText(),
                        categoryField.getText()
                );
                bookList.setAll(result);
            } catch (Exception ex) {
                showAlert("Not Found", ex.getMessage());
            }
        });

        issueBtn.setOnAction(e -> {
            try {
                BookService.issueBook(isbnField.getText());
                refreshTable();
            } catch (Exception ex) {
                showAlert("Issue Failed", ex.getMessage());
            }
        });

        returnBtn.setOnAction(e -> {
            try {
                BookService.returnBook(isbnField.getText());
                refreshTable();
            } catch (Exception ex) {
                showAlert("Return Failed", ex.getMessage());
            }
        });

        refreshBtn.setOnAction(e -> refreshTable());

        // Layout
        HBox fields = new HBox(10, isbnField, titleField, authorField, categoryField, quantityField);
        HBox actions = new HBox(10, addBtn, updateBtn, deleteBtn, searchBtn, issueBtn, returnBtn, refreshBtn);
        fields.setPadding(new Insets(10));
        actions.setPadding(new Insets(10));

        VBox root = new VBox(10, fields, actions, tableView);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f0f8ff;");

        // Scene
        Scene scene = new Scene(root, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTable() {
        bookList.setAll(BookService.getAllBooksList());
    }

    private void clearFields() {
        isbnField.clear();
        titleField.clear();
        authorField.clear();
        categoryField.clear();
        quantityField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
