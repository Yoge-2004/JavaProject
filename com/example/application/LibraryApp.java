package com.example.application;

import com.example.entities.Book;
import com.example.services.BookService;
import com.example.services.UserService;
import com.example.exceptions.ValidationException;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class LibraryApp extends Application {

    private String loggedInUser = null;
    private ListView<String> bookListView;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("📚 Library Galaxy");

        VBox loginPane = createLoginPane(primaryStage);
        Scene loginScene = new Scene(loginPane, 500, 350);
        applyFadeTransition(loginPane);

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox createLoginPane(Stage stage) {
        Label title = new Label("🚀 Welcome to Library Galaxy");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#ffffff"));

        TextField userField = new TextField();
        userField.setPromptText("Username");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Button loginBtn = createStyledButton("🔓 Login");
        Button registerBtn = createStyledButton("📝 Register");

        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.YELLOW);

        loginBtn.setOnAction(e -> {
            try {
                if (UserService.login(userField.getText(), passField.getText())) {
                    loggedInUser = userField.getText();
                    BorderPane mainPane = createMainPane();
                    applyFadeTransition(mainPane);
                    stage.setScene(new Scene(mainPane, 1100, 650));
                } else {
                    messageLabel.setText("❌ Invalid credentials.");
                }
            } catch (ValidationException ex) {
                messageLabel.setText("❌ " + ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> {
            try {
                UserService.createUser(userField.getText(), passField.getText());
                messageLabel.setText("✅ Registered successfully.");
                messageLabel.setTextFill(Color.LIMEGREEN);
                UserService.persistUsers();
            } catch (ValidationException ex) {
                messageLabel.setText("❌ " + ex.getMessage());
                messageLabel.setTextFill(Color.RED);
            }
        });

        VBox box = new VBox(15, title, userField, passField, loginBtn, registerBtn, messageLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#1e3c72")),
                        new Stop(1, Color.web("#2a5298"))),
                CornerRadii.EMPTY, Insets.EMPTY)));

        return box;
    }

    private BorderPane createMainPane() {
        BorderPane root = new BorderPane();

        Label header = new Label("🌟 Welcome, " + loggedInUser + "!");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#ffffff"));
        header.setPadding(new Insets(10));
        header.setBackground(new Background(new BackgroundFill(Color.web("#0077be"), CornerRadii.EMPTY, Insets.EMPTY)));

        VBox controls = new VBox(10);
        controls.setPadding(new Insets(15));
        controls.setBackground(new Background(new BackgroundFill(Color.web("#d0f0fd"), CornerRadii.EMPTY, Insets.EMPTY)));

        TextField isbnField = new TextField(); isbnField.setPromptText("ISBN");
        TextField titleField = new TextField(); titleField.setPromptText("Title");
        TextField authorField = new TextField(); authorField.setPromptText("Author");
        TextField categoryField = new TextField(); categoryField.setPromptText("Category");
        TextField quantityField = new TextField(); quantityField.setPromptText("Quantity");

        Button addBookBtn = createStyledButton("➕ Add Book");
        Button updateBookBtn = createStyledButton("✏️ Update Book");
        Button deleteBookBtn = createStyledButton("🗑️ Delete Book");
        Button issueBookBtn = createStyledButton("📤 Issue Book");
        Button returnBookBtn = createStyledButton("📥 Return Book");
        Button refreshBtn = createStyledButton("🔄 Refresh List");

        addBookBtn.setOnAction(e -> {
            try {
                BookService.createBook(
                        isbnField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        categoryField.getText(),
                        Integer.parseInt(quantityField.getText())
                );
                persistAndRefresh();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        updateBookBtn.setOnAction(e -> {
            try {
                Book book = new Book(
                        isbnField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        categoryField.getText(),
                        Integer.parseInt(quantityField.getText())
                );
                BookService.updateBook(book);
                persistAndRefresh();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        deleteBookBtn.setOnAction(e -> {
            try {
                BookService.deleteBook(isbnField.getText());
                persistAndRefresh();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        issueBookBtn.setOnAction(e -> {
            try {
                BookService.issueBook(isbnField.getText());
                persistAndRefresh();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        returnBookBtn.setOnAction(e -> {
            try {
                BookService.returnBook(isbnField.getText());
                persistAndRefresh();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        refreshBtn.setOnAction(e -> refreshListView());

        controls.getChildren().addAll(
                new Label("📘 Book Management"),
                isbnField, titleField, authorField, categoryField, quantityField,
                addBookBtn, updateBookBtn, deleteBookBtn,
                issueBookBtn, returnBookBtn, refreshBtn
        );

        bookListView = new ListView<>();
        refreshListView();
        bookListView.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px;");

        VBox centerPane = new VBox(10, new Label("📚 Book List:"), bookListView);
        centerPane.setPadding(new Insets(15));
        centerPane.setBackground(new Background(new BackgroundFill(Color.web("#f0ffff"), CornerRadii.EMPTY, Insets.EMPTY)));

        root.setTop(header);
        root.setLeft(controls);
        root.setCenter(centerPane);

        return root;
    }

    private void refreshListView() {
        List<Book> books = BookService.getAllBooksList();
        ObservableList<String> bookStrings = FXCollections.observableArrayList();
        for (Book book : books) {
            bookStrings.add(book.toString());
        }
        bookListView.setItems(bookStrings);
    }

    private void persistAndRefresh() {
        try {
            BookService.persistBooks();
            refreshListView();
        } catch (Exception ex) {
            showAlert("Persistence Error", ex.getMessage());
        }
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #00bfff; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setEffect(new DropShadow());
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #009acd; -fx-text-fill: white;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #00bfff; -fx-text-fill: white;"));
        return btn;
    }

    private void applyFadeTransition(Pane pane) {
        FadeTransition ft = new FadeTransition(Duration.millis(800), pane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

