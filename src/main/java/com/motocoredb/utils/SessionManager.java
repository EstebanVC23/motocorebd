package com.motocoredb.utils;

import com.motocoredb.models.User;

public class SessionManager {
    private static User currentUser; // Usuario logueado

    /**
     * Establece el usuario autenticado en la sesión actual
     *
     * @param user El usuario autenticado
     */
    public static void setSession(User user) {
        currentUser = user; // Establecer el usuario autenticado
    }

    /**
     * Obtiene el usuario actualmente autenticado en la sesión
     *
     * @return El usuario logueado o null si no hay sesión activa
     */
    public static User getCurrentUser() {
        return currentUser; // Obtener el usuario logueado
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado
     *
     * @return ID del usuario o -1 si no hay sesión activa
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1; // Retorna el ID del usuario o -1 si no hay usuario
    }

    /**
     * Obtiene el nombre completo del usuario actualmente autenticado
     *
     * @return El nombre completo del usuario o "Desconocido" si no hay sesión activa
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : "Desconocido"; // Retorna el nombre del usuario o "Desconocido"
    }

    /**
     * Limpia la sesión actual eliminando al usuario autenticado
     */
    public static void clearSession() {
        currentUser = null; // Limpia la sesión actual
    }
}