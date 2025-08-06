package com.example.services;

import com.example.entities.UsersDB;
import com.example.exceptions.ValidationException;

public class UserService {

    private static final UsersDB users = UsersDB.getInstance();

    public static void createUser(String username, String password) {
        if (users.containsUser(username)) {
            throw new ValidationException("User with this username already exists.");
        }

        validatePasswordStrength(password);
        users.addUser(username, password);
        System.out.println("✅ User created successfully.");
    }

    public static boolean updateUserPassword(String username, String oldPassword, String newPassword) {
        if (!validateUser(username, oldPassword)) {
            throw new ValidationException("Invalid user credentials.");
        }

        validatePasswordStrength(newPassword);
        return users.modifyUserPassword(username, newPassword);
    }

    public static boolean updateUserName(String oldUsername, String newUsername, String password) {
        if (!validateUser(oldUsername, password)) {
            throw new ValidationException("Invalid user credentials.");
        }

        return users.modifyUserName(oldUsername, newUsername);
    }

    public static boolean validateUser(String username, String password) {
        if (!users.containsUser(username)) return false;
        return users.getPassword(username).equals(password);
    }

    public static boolean login(String username, String password) {
        return validateUser(username, password);
    }

    public static void displayUsers(String username, String password) {
        if (validateUser(username, password)) {
            users.displayUsers();
        } else {
            throw new ValidationException("Invalid credentials. Cannot display users.");
        }
    }

    private static void validatePasswordStrength(String password) {
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
        if (!password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one uppercase letter and one digit.");
        }
    }

    public static void persistUsers() {
        users.getClass(); // Ensures instance is initialized
    }
}
