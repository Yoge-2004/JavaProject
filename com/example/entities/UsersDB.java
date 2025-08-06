package com.example.entities;

import com.example.exceptions.ValidationException;
import com.example.storage.DataStorage;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsersDB implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String USER_SER_FILE = "data/users_db.ser";

    private static UsersDB instance;
    private Map<String, String> users;

    private UsersDB() {
        users = new LinkedHashMap<>();
    }

    static {
        try {
            instance = DataStorage.readSerializedUsers(USER_SER_FILE);
            if (instance == null) {
                instance = new UsersDB();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load users from .ser file. Initializing empty DB.");
            instance = new UsersDB();
        }
    }

    public static UsersDB getInstance() {
        return instance;
    }

    public boolean addUser(String userName, String password) {
        users.put(userName, password);
        persist();
        return true;
    }

    public boolean modifyUserPassword(String userName, String newPassword) {
        validateUserExists(userName);
        users.put(userName, newPassword);
        persist();
        return true;
    }

    public boolean modifyUserName(String oldUserName, String newUserName) {
        validateUserExists(oldUserName);
        String password = users.remove(oldUserName);
        users.put(newUserName, password);
        persist();
        return true;
    }

    public boolean containsUser(String userName) {
        return users.containsKey(userName);
    }

    public String getPassword(String userName) {
        validateUserExists(userName);
        return users.get(userName);
    }

    public boolean deleteUser(String userName, String password) {
        if (containsUser(userName) && users.get(userName).equals(password)) {
            users.remove(userName);
            persist();
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

    public Map<String, String> fetchAllUsers() {
        return new LinkedHashMap<>(users);
    }

    private void validateUserExists(String userName) {
        if (!users.containsKey(userName)) {
            throw new ValidationException("User not found.");
        }
    }

    private void persist() {
        try {
            DataStorage.writeSerializedUsers(USER_SER_FILE, this);
        } catch (IOException e) {
            System.err.println("Failed to persist user data: " + e.getMessage());
        }
    }
}
