package com.skybook.data;

import com.skybook.model.*;
import com.skybook.ui.*;
import com.skybook.ui.components.*;
import com.skybook.util.*;
import com.skybook.main.*;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    private static final Map<String, User> users = new HashMap<>();
    private static User loggedInUser = null;

    static {
        // Pre-register some sample users
        registerUser("admin", "admin123", "System Administrator", true);
        registerUser("user", "password", "Regular User", false);
    }

    public static boolean registerUser(String username, String password, String fullName, boolean isAdmin) {
        if (users.containsKey(username.toLowerCase())) {
            return false; // Username exists
        }
        String id = "U" + (1000 + users.size());
        users.put(username.toLowerCase(), new User(id, username, password, fullName, isAdmin));
        return true;
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
