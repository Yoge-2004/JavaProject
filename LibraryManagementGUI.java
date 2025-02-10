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

public class LibraryManagementGUI extends Application {

    // Book Data Arrays
    private String[] books = {"DPCO", "DM", "OOPS", "DS", "FDS", "PYTHON", "C", "C++", "TAMIL", "ENGLISH"};
    private int[] bookCodes = {1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009};
    private int[] prices = {200, 250, 750, 300, 400, 200, 125, 450, 300, 200};
    private int[] availableQty = {50, 15, 100, 25, 10, 100, 75, 25, 40, 30};

    private ObservableList<Book> inventory = FXCollections.observableArrayList();
    private ObservableList<Book> invoice = FXCollections.observableArrayList();

    private TableView<Book> bookTable;
    private TableView<Book> invoiceTable;
    private TextField bookCodeField, quantityField;
    private Label totalLabel;
    private int grandTotal = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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

    // Method to create the book inventory table
    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();
        TableColumn<Book, Integer> codeColumn = new TableColumn<>("Book Code");
        codeColumn.setCellValueFactory(cell -> cell.getValue().bookCodeProperty().asObject());

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(cell -> cell.getValue().bookNameProperty());

        TableColumn<Book, Integer> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());

        TableColumn<Book, Integer> qtyColumn = new TableColumn<>("Available Quantity");
        qtyColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());

        table.getColumns().addAll(codeColumn, nameColumn, priceColumn, qtyColumn);
        table.setItems(inventory);
        return table;
    }

    // Method to create the invoice table
    private TableView<Book> createInvoiceTable() {
        TableView<Book> table = new TableView<>();
        TableColumn<Book, Integer> codeColumn = new TableColumn<>("Book Code");
        codeColumn.setCellValueFactory(cell -> cell.getValue().bookCodeProperty().asObject());

        TableColumn<Book, String> nameColumn = new TableColumn<>("Book Name");
        nameColumn.setCellValueFactory(cell -> cell.getValue().bookNameProperty());

        TableColumn<Book, Integer> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());

        TableColumn<Book, Integer> qtyColumn = new TableColumn<>("Quantity");
        qtyColumn.setCellValueFactory(cell -> cell.getValue().quantityProperty().asObject());

        TableColumn<Book, Integer> costColumn = new TableColumn<>("Cost");
        costColumn.setCellValueFactory(cell -> cell.getValue().costProperty().asObject());

        table.getColumns().addAll(codeColumn, nameColumn, priceColumn, qtyColumn, costColumn);
        table.setItems(invoice);
        return table;
    }

    // Load books into inventory table
    private void loadBooks() {
        for (int i = 0; i < books.length; i++) {
            inventory.add(new Book(bookCodes[i], books[i], prices[i], availableQty[i]));
        }
    }

    // Add book to invoice
    private void addToInvoice() {
        try {
            int code = Integer.parseInt(bookCodeField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                throw new Exception("Quantity must be greater than zero.");
            }

            for (Book book : inventory) {
                if (book.getBookCode() == code) {
                    if (quantity > book.getQuantity()) {
                        throw new Exception("Not enough stock available.");
                    }

                    // Add to invoice and update inventory
                    book.setQuantity(book.getQuantity() - quantity);
                    invoice.add(new Book(code, book.getBookName(), book.getPrice(), quantity, book.getPrice() * quantity));

                    grandTotal += book.getPrice() * quantity;
                    totalLabel.setText("Grand Total: ₹" + grandTotal);
                    return;
                }
            }

            throw new Exception("Invalid book code.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    // Generate invoice and save to file
    private void generateInvoice() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("invoice.txt"))) {
            writer.write("Library Invoice\n");
            writer.write("Book Code\tBook Name\tPrice\tQuantity\tCost\n");

            for (Book book : invoice) {
                writer.write(book.getBookCode() + "\t" + book.getBookName() + "\t" + book.getPrice() + "\t" + book.getQuantity() + "\t" + book.getCost());
                writer.newLine();
            }

            writer.write("------------------------------------------------\n");
            writer.write("Grand Total: ₹" + grandTotal);
            writer.newLine();
            showAlert("Success", "Invoice saved successfully!");
        } catch (IOException e) {
            showAlert("Error", "Failed to save invoice.");
        }
    }

    // Show an alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
