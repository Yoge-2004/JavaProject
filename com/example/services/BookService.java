package com.example.services;

import com.example.entities.Book;
import com.example.entities.BooksDB;
import com.example.exceptions.BooksException;

import java.io.IOException;
import java.util.List;

public class BookService {

    private static final BooksDB db = BooksDB.getInstance();

    public static void createBook(String isbn, String title, String author, String category, int quantity) {
        Book book = new Book(isbn, title, author, category, quantity);
        db.addNewBook(book);
        System.out.println("✅ Book added successfully.");
    }

    public static void updateBook(Book book) {
        db.modifyBookDetails(book);
        System.out.println("✏️ Book updated successfully.");
    }

    public static void deleteBook(String isbn) {
        db.removeBook(isbn);
        System.out.println("🗑️ Book removed successfully.");
    }

    public static void searchByTitle(String title) {
        Book book = db.searchBookByTitle(title);
        System.out.println("🔍 Found: " + book);
    }

    public static void searchByAuthor(String author) {
        Book book = db.searchBookByAuthor(author);
        System.out.println("🔍 Found: " + book);
    }

    public static void searchByCategory(String category) {
        Book book = db.searchBookByCategory(category);
        System.out.println("🔍 Found: " + book);
    }

    public static void issueBook(String isbn) {
        db.issueBook(isbn);
        System.out.println("📤 Book issued successfully.");
    }

    public static void returnBook(String isbn) {
        db.returnBook(isbn);
        System.out.println("📥 Book returned successfully.");
    }

    public static void displayBooks() {
        db.displayBooks();
    }

    public static void displayIssuedBooks() {
        db.displayIssuedBooks();
    }

    public static Book searchByTitleOrAuthorOrCategory(String title, String author, String category) {
        if (!title.isBlank()) return db.searchBookByTitle(title);
        else if (!author.isBlank()) return db.searchBookByAuthor(author);
        else if (!category.isBlank()) return db.searchBookByCategory(category);
        else throw new BooksException("Enter Title/Author/Category to search.");
    }

    public static List<Book> getAllBooksList() {
        return db.getAllBooks();
    }

    public static void persistBooks() throws IOException {
        // Persistence is handled inside BooksDB after every mutation
        db.getClass(); // Ensures instance is initialized
    }
}
