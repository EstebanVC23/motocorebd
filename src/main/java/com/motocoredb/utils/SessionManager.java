package com.motocoredb.utils;

import com.motocoredb.models.User;

public class SessionManager {
    private static User currentUser;

    /**
     * Establece el usuario autenticado en la sesión actual
     *
     * @param user El usuario autenticado
     */
    public static void setSession(User user) {
        currentUser = user;
    }

    /**
     * Obtiene el usuario actualmente autenticado en la sesión
     *
     * @return El usuario logueado o null si no hay sesión activa
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado
     *
     * @return ID del usuario o -1 si no hay sesión activa
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }

    /**
     * Obtiene el nombre completo del usuario actualmente autenticado
     *
     * @return El nombre completo del usuario o "Desconocido" si no hay sesión activa
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : "Desconocido";
    }

    /**
     * Limpia la sesión actual eliminando al usuario autenticado
     */
    public static void clearSession() {
        currentUser = null;
    }
}