package com.example.demo;


import javafx.beans.property.*;

public class Book {
    private final IntegerProperty bookCode;
    private final StringProperty bookName;
    private final IntegerProperty price;
    private final IntegerProperty quantity;
    private final IntegerProperty cost;

    // Constructor for inventory books (without initial cost)
    public Book(int bookCode, String bookName, int price, int quantity) {
        this(bookCode, bookName, price, quantity, 0);
    }

    // Constructor for invoice books (with cost)
    public Book(int bookCode, String bookName, int price, int quantity, int cost) {
        this.bookCode = new SimpleIntegerProperty(bookCode);
        this.bookName = new SimpleStringProperty(bookName);
        this.price = new SimpleIntegerProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.cost = new SimpleIntegerProperty(cost);
    }

    // Getters
    public int getBookCode() {
        return bookCode.get();
    }

    public String getBookName() {
        return bookName.get();
    }

    public int getPrice() {
        return price.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public int getCost() {
        return cost.get();
    }

    // Property methods (needed for TableView bindings)
    public IntegerProperty bookCodeProperty() {
        return bookCode;
    }

    public StringProperty bookNameProperty() {
        return bookName;
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public IntegerProperty costProperty() {
        return cost;
    }

    // Setters
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public void setCost(int cost) {
        this.cost.set(cost);
    }
}
