package com.example.entities;

import com.example.exceptions.BooksException;
import com.example.storage.DataStorage;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class BooksDB implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String BOOKS_SER_FILE = "data/books_db.ser";
    private static final String ISSUED_BOOKS_FILE = "data/issuedbooks_db.ser";

    private static BooksDB instance;

    private Map<String, Book> books;
    private Map<String, Integer> issuedBooks;

    private BooksDB() {
        books = new LinkedHashMap<>();
        issuedBooks = new LinkedHashMap<>();
        loadIssuedBooks(); // Load issued books separately
    }

    static {
        try {
            instance = DataStorage.readSerializedBooksDB(BOOKS_SER_FILE);
            if (instance == null) {
                instance = new BooksDB();
            }
            instance.loadIssuedBooks(); // ✅ Always reload issuedBooks from separate file
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load books from .ser file. Initializing empty DB.");
            instance = new BooksDB();
        }
    }

    public static BooksDB getInstance() {
        return instance;
    }

    public boolean addNewBook(Book book) {
        books.put(book.getIsbn(), book);
        persistBooks();
        return true;
    }

    public boolean modifyBookDetails(Book book) {
        validateBookExists(book.getIsbn());
        books.put(book.getIsbn(), book);
        persistBooks();
        return true;
    }

    public boolean removeBook(String isbn) {
        validateBookExists(isbn);
        books.remove(isbn);
        issuedBooks.remove(isbn);
        persistBooks();
        persistIssuedBooks();
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

    public boolean issueBook(String isbn) {
        validateBookExists(isbn);
        Book book = books.get(isbn);
        if (book.getQuantity() <= 0) {
            throw new BooksException("Book out of stock.");
        }

        book.setQuantity(book.getQuantity() - 1);
        books.put(isbn, book);
        issuedBooks.put(isbn, issuedBooks.getOrDefault(isbn, 0) + 1);

        System.out.println("Issued: " + book.getTitle() + " on " + LocalDate.now());
        persistBooks();
        persistIssuedBooks();
        return true;
    }

    public boolean returnBook(String isbn) {
        if (!issuedBooks.containsKey(isbn)) {
            throw new BooksException("This book was not issued.");
        }

        Book book = books.get(isbn);
        if (book == null) {
            throw new BooksException("Book not found in the library.");
        }

        book.setQuantity(book.getQuantity() + 1);
        int issuedCount = issuedBooks.get(isbn);
        if (issuedCount == 1) {
            issuedBooks.remove(isbn);
        } else {
            issuedBooks.put(isbn, issuedCount - 1);
        }

        System.out.println("Returned: " + book.getTitle());
        persistBooks();
        persistIssuedBooks();
        return true;
    }

    public void displayIssuedBooks() {
        if (issuedBooks.isEmpty()) {
            System.out.println("No books are currently issued.");
            return;
        }

        issuedBooks.forEach((isbn, count) -> {
            Book book = books.get(isbn);
            System.out.println(book + " | Issued count: " + count);
        });
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public Map<String, Book> fetchAllBooks() {
        return new LinkedHashMap<>(books);
    }

    public Map<String, Integer> fetchAllIssuedBooks() {
        return new LinkedHashMap<>(issuedBooks);
    }

    private void validateBookExists(String isbn) {
        if (!books.containsKey(isbn)) {
            throw new BooksException("Book not found.");
        }
    }

    private void persistBooks() {
        try {
            DataStorage.writeSerializedBooksDB(BOOKS_SER_FILE, this);
        } catch (IOException e) {
            System.err.println("Failed to persist book data: " + e.getMessage());
        }
    }

    private void persistIssuedBooks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ISSUED_BOOKS_FILE))) {
            oos.writeObject(issuedBooks);
        } catch (IOException e) {
            System.err.println("Failed to persist issued books: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadIssuedBooks() {
        File file = new File(ISSUED_BOOKS_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                issuedBooks = (Map<String, Integer>) obj;
            } else {
                System.err.println("Invalid format in issued books file.");
                issuedBooks = new LinkedHashMap<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load issued books: " + e.getMessage());
            issuedBooks = new LinkedHashMap<>();
        }
    }
}
