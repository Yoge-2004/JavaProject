package com.example.entities;

import com.example.exceptions.BooksException;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private int quantity; // 🔥 REQUIRED for issue/return logic

    public Book(String isbn, String title, String author, String category, int quantity) {
        if (quantity < 0) {
            throw new BooksException("Invalid Quantity : Quantity should be greater than 0");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
    }

    // ✅ Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }

    // ✅ Setters
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCategory(String category) { this.category = category; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return String.format("[ISBN: %s | Title: %s | Author: %s | Category: %s | Quantity: %d]",
                isbn, title, author, category, quantity);
    }
}
