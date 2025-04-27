package com.motocoredb.utils;

import com.motocoredb.models.User;

public class SessionManager {
    private static User currentUser; // Usuario logueado

    public static void setSession(User user) {
        currentUser = user; // Establecer el usuario autenticado
    }

    public static User getCurrentUser() {
        return currentUser; // Obtener el usuario logueado
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1; // Retorna el ID del usuario o -1 si no hay usuario
    }

    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : "Desconocido"; // Retorna el nombre del usuario o "Desconocido"
    }
}