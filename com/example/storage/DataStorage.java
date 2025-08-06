package com.example.storage;

import com.example.entities.BooksDB;
import com.example.entities.UsersDB;

import java.io.*;

public class DataStorage {

    // 🔐 USERS
    public static UsersDB readSerializedUsers(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (UsersDB) ois.readObject();
        }
    }

    public static void writeSerializedUsers(String filePath, UsersDB usersDB) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(usersDB);
        }
    }

    // 📚 BOOKS
    public static BooksDB readSerializedBooksDB(String filePath) throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (BooksDB) ois.readObject();
        }
    }

    public static void writeSerializedBooksDB(String filePath, BooksDB booksDB) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(booksDB);
        }
    }
}