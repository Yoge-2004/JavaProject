package com.example.entities;

import com.example.exceptions.ValidationException;

import java.util.LinkedHashMap;
import java.util.Map;

public class UsersDB {
    private static UsersDB instance = null;
    private Map<String, String> users;

    private UsersDB() {
        users = new LinkedHashMap<>();
    }

    public static UsersDB getInstance() {
        if (instance == null) {
            instance = new UsersDB();
        }
        return instance;
    }

    public boolean addUser(String userName, String password) {
        users.put(userName, password);
        return true;
    }

    public boolean modifyUserPassword(String userName, String newPassword) {
        if (!users.containsKey(userName)) {
            throw new ValidationException("User not found.");
        }
        users.replace(userName, newPassword);
        return true;
    }

    public boolean modifyUserName(String oldUserName, String newUserName) {
        if (!users.containsKey(oldUserName)) {
            throw new ValidationException("User not found.");
        }
        String password = users.remove(oldUserName);
        users.put(newUserName, password);
        return true;
    }

    public boolean containsUser(String userName) {
        return users.containsKey(userName);
    }

    public String getPassword(String userName) {
        if (!containsUser(userName)) {
            throw new ValidationException("Invalid username.");
        }
        return users.get(userName);
    }

    public boolean deleteUser(String userName, String password) {
        if (containsUser(userName) && users.get(userName).equals(password)) {
            users.remove(userName);
            return true;
        }
        throw new ValidationException("Delete Failed: Invalid credentials.");
    }

    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users to display.");
            return;
        }
        users.forEach((username, password) -> System.out.println(username + " -> ********"));
    }
}
