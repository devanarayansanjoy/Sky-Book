package data;

import model.*;
import ui.*;
import ui.components.*;
import util.*;
import app.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static Map<String, User> users = new HashMap<>();
    private static User loggedInUser = null;
    private static final String DATA_FILE = "users.dat";

    static {
        loadFromFile();
        // Ensure admin and user exist
        if (!users.containsKey("admin")) {
            registerUser("admin", "admin123", "System Administrator", "admin@skybook.com", "555-0000", true);
        }
        if (!users.containsKey("user")) {
            registerUser("user", "password", "Regular User", "user@skybook.com", "555-1111", false);
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            System.out.println("No existing users file found or error loading. Starting fresh.");
        }
    }

    private static void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String password, String fullName, String email, String phoneNumber, boolean isAdmin) {
        if (users.containsKey(username.toLowerCase())) {
            return false; // Username exists
        }
        String id = "U" + (1000 + users.size());
        users.put(username.toLowerCase(), new User(id, username, password, fullName, email, phoneNumber, isAdmin));
        saveToFile();
        return true;
    }

    public static void updateUser(User updatedUser) {
        if (users.containsKey(updatedUser.getUsername().toLowerCase())) {
            users.put(updatedUser.getUsername().toLowerCase(), updatedUser);
            saveToFile();
        }
    }

    public static User login(String username, String password) {
        User user = users.get(username.toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user;
            return user;
        }
        return null;
    }

    public static void logout() {
        loggedInUser = null;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}
