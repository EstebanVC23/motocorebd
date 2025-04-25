package com.motocoredb.controllers;

import com.motocoredb.models.User;
import com.motocoredb.services.UserService;
import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public boolean changePassword(int userId, String newPassword) {
        return userService.changePassword(userId, newPassword);
    }

    public boolean updateUserProfile(User user) {
        return userService.updateUserProfile(user);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public boolean deactivateUser(int userId) {
        return userService.deactivateUser(userId);
    }
}