package com.example.entities;

import com.example.exceptions.BooksException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BooksDB {
    private static final Map<String, Book> books = new LinkedHashMap<>(); // ISBN -> Book
    private static final Map<String, LocalDate> issuedBooks = new LinkedHashMap<>(); // ISBN -> Issue Date

    private static BooksDB instance = null;

    private BooksDB() {}

    public static BooksDB getInstance() {
        if (instance == null) {
            instance = new BooksDB();
        }
        return instance;
    }

    public boolean addNewBook(Book book) {
        books.put(book.getIsbn(), book);
        return true;
    }

    public boolean modifyBookDetails(Book book) {
        if (!books.containsKey(book.getIsbn())) {
            throw new BooksException("Book not found for update.");
        }
        books.put(book.getIsbn(), book);
        return true;
    }

    public boolean removeBook(String isbn) {
        if (!books.containsKey(isbn)) {
            throw new BooksException("Book not found.");
        }
        books.remove(isbn);
        issuedBooks.remove(isbn);
        return true;
    }

    public boolean containsBook(String isbn) {
        return books.containsKey(isbn);
    }

    public Book searchBookByTitle(String title) {
        return books.values().stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new BooksException("Book not found by title."));
    }

    public Book searchBookByAuthor(String author) {
        return books.values().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .findFirst()
                .orElseThrow(() -> new BooksException("Book not found by author."));
    }

    public Book searchBookByCategory(String category) {
        return books.values().stream()
                .filter(book -> book.getCategory().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new BooksException("Book not found by category."));
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            throw new BooksException("No books to display.");
        }
        books.forEach((isbn, book) -> System.out.println(book));
    }

    // ✅ Refactored issueBook
    public boolean issueBook(String isbn) {
        if (!books.containsKey(isbn)) {
            throw new BooksException("Book not available.");
        }

        Book book = books.get(isbn);
        if (book.getQuantity() <= 0) {
            throw new BooksException("Book out of stock.");
        }

        book.setQuantity(book.getQuantity() - 1);
        books.put(isbn, book);

        issuedBooks.put(isbn, LocalDate.now());

        System.out.println("Issued: " + book.getTitle() + " on " + LocalDate.now());
        return true;
    }

    // ✅ Refactored returnBook
    public boolean returnBook(String isbn) {
        if (!issuedBooks.containsKey(isbn)) {
            throw new BooksException("This book was not issued.");
        }

        Book book = books.get(isbn);
        book.setQuantity(book.getQuantity() + 1);
        books.put(isbn, book);

        issuedBooks.remove(isbn);

        System.out.println("Returned: " + book.getTitle());
        return true;
    }

    public void displayIssuedBooks() {
        if (issuedBooks.isEmpty()) {
            System.out.println("No books are currently issued.");
            return;
        }

        issuedBooks.forEach((isbn, date) -> {
            Book book = books.get(isbn);
            System.out.println(book + " | Issued on: " + date);
        });
    }

    public String getAllBooksAsString() {
        if (books.isEmpty()) return "No books available.";
        StringBuilder sb = new StringBuilder();
        books.forEach((isbn, book) -> sb.append(book).append("\n"));
        return sb.toString();
    }

    public String getIssuedBooksAsString() {
        if (issuedBooks.isEmpty()) return "No books currently issued.";
        StringBuilder sb = new StringBuilder();
        issuedBooks.forEach((isbn, date) -> {
            Book book = books.get(isbn);
            sb.append(book).append(" | Issued on: ").append(date).append("\n");
        });
        return sb.toString();
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
}
