package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.collections.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LibraryManagementGUI extends Application { // Extend Application correctly

    // Book Data Arrays
    private final String[] books = { "DPCO", "DM", "OOPS", "DS", "FDS", "PYTHON", "C", "C++", "TAMIL", "ENGLISH" };
    private final int[] bookCodes = { 1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009 };
    private final int[] prices = { 200, 250, 750, 300, 400, 200, 125, 450, 300, 200 };
    private final int[] availableQty = { 50, 15, 100, 25, 10, 100, 75, 25, 40, 30 };

    private final ObservableList<Book> inventory = FXCollections.observableArrayList();
    private final ObservableList<Book> invoice = FXCollections.observableArrayList();

    private TableView<Book> bookTable;
    private TableView<Book> invoiceTable;
    private TextField bookCodeField, quantityField;
    private Label totalLabel;
    private int grandTotal = 0;

    public static void main(String[] args) {
        launch(args); // JavaFX applications must use launch()
    }

    @Override
    public void start(Stage primaryStage) { // Correct JavaFX startup method
        primaryStage.setTitle("Library Management System");

        // Book TableView
        bookTable = createBookTable();
        invoiceTable = createInvoiceTable();

        // Input Fields
        bookCodeField = new TextField();
        bookCodeField.setPromptText("Enter Book Code");

        quantityField = new TextField();
        quantityField.setPromptText("Enter Quantity");

        // Buttons
        Button addButton = new Button("Add to Invoice");
        addButton.setOnAction(e -> addToInvoice());

        Button generateInvoiceButton = new Button("Generate Invoice");
        generateInvoiceButton.setOnAction(e -> generateInvoice());

        // Total Label
        totalLabel = new Label("Grand Total: ₹0");

        // Layout
        HBox inputBox = new HBox(10, new Label("Book Code:"), bookCodeField, new Label("Quantity:"), quantityField, addButton);
        inputBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, bookTable, inputBox, invoiceTable, totalLabel, generateInvoiceButton);
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(600);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        // Load books into table
        loadBooks();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();
        TableColumn<Book, Number> codeColumn = new TableColumn<>("Book Code");
        codeColumn.setCellValueFactory(cell -> cell.getValue().bookCodeProperty());

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(cell -> cell.getValue().bookNameProperty());

        TableColumn<Book, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty());

        TableColumn<Book, Number> qtyColumn = new TableColumn<>("Available Quantity");
        qtyColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty());

        table.getColumns().addAll(codeColumn, nameColumn, priceColumn, qtyColumn);
        table.setItems(inventory);
        return table;
    }

    private TableView<Book> createInvoiceTable() {
        TableView<Book> table = new TableView<>();
        TableColumn<Book, Number> codeColumn = new TableColumn<>("Book Code");
        codeColumn.setCellValueFactory(cell -> cell.getValue().bookCodeProperty());

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(cell -> cell.getValue().bookNameProperty());

        TableColumn<Book, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty());

        TableColumn<Book, Number> qtyColumn = new TableColumn<>("Quantity");
        qtyColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty());

        TableColumn<Book, Number> costColumn = new TableColumn<>("Cost");
        costColumn.setCellValueFactory(cell -> cell.getValue().costProperty());

        table.getColumns().addAll(codeColumn, nameColumn, priceColumn, qtyColumn, costColumn);
        table.setItems(invoice);
        return table;
    }

    private void loadBooks() {
        for (int i = 0; i < books.length; i++) {
            inventory.add(new Book(bookCodes[i], books[i], prices[i], availableQty[i]));
        }
    }

    private void addToInvoice() {
        try {
            int code = Integer.parseInt(bookCodeField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                throw new Exception("Quantity must be greater than zero.");
            }

            Book selectedBook = null;
            for (Book book : inventory) {
                if (book.getBookCode() == code) {
                    selectedBook = book;
                    break;
                }
            }

            if (selectedBook == null) {
                throw new Exception("Invalid book code.");
            }

            if (quantity > selectedBook.getQuantity()) {
                throw new Exception("Not enough stock available.");
            }

            selectedBook.setQuantity(selectedBook.getQuantity() - quantity);

            Book invoiceBook = null;
            for (Book book : invoice) {
                if (book.getBookCode() == code) {
                    invoiceBook = book;
                    break;
                }
            }

            if (invoiceBook != null) {
                invoiceBook.setQuantity(invoiceBook.getQuantity() + quantity);
                invoiceBook.setCost(invoiceBook.getQuantity() * invoiceBook.getPrice());
            } else {
                invoice.add(new Book(code, selectedBook.getBookName(), selectedBook.getPrice(), quantity,
                        selectedBook.getPrice() * quantity));
            }

            grandTotal += selectedBook.getPrice() * quantity;
            totalLabel.setText("Grand Total: ₹" + grandTotal);

            bookCodeField.clear();
            quantityField.clear();

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numeric values for book code and quantity.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void generateInvoice() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("invoice.txt"))) {
            writer.write("Library Invoice\n");
            writer.write("Book Code\tBook Name\tPrice\tQuantity\tCost\n");

            for (Book book : invoice) {
                writer.write(book.getBookCode() + "\t" + book.getBookName() + "\t" + book.getPrice() + "\t"
                        + book.getQuantity() + "\t" + book.getCost());
                writer.newLine();
            }

            writer.write("------------------------------------------------\n");
            writer.write("Grand Total: ₹" + grandTotal);
            writer.newLine();
            showAlert("Success", "Invoice saved successfully!");
        } catch (IOException e) {
            showAlert("File Error", "Failed to save invoice.");
        }
    }
}
