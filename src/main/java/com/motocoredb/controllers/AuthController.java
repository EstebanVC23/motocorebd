package com.motocoredb.controllers;

import com.motocoredb.models.User;
import com.motocoredb.services.AuthService;

public class AuthController {
    private final AuthService authService;
    private User currentUser; // Para manejar el usuario actual

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public User login(String username, String password) {
        this.currentUser = authService.login(username, password);
        return this.currentUser;
    }

    public boolean register(User user) {
        return authService.register(user);
    }

    // Añade este método para cerrar sesión
    public void logout() {
        if (currentUser != null) {
            authService.updateLastLogin(currentUser.getUserId());
        }
        this.currentUser = null;
    }

    // Método para obtener el usuario actual (opcional)
    public User getCurrentUser() {
        return currentUser;
    }
}