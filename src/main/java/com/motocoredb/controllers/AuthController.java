package com.motocoredb.controllers;

import com.motocoredb.models.User;
import com.motocoredb.services.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public User login(String username, String password) {
        return authService.login(username, password);
    }

    public boolean register(User user) {
        return authService.register(user);
    }
}