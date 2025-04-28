package com.motocoredb.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.motocoredb.models.Staff;
import com.motocoredb.models.User;
import com.motocoredb.services.AuthService;
import com.motocoredb.utils.DBConnection;

public class AuthController {
    private final AuthService authService;
    private User currentUser; // Para manejar el usuario actual
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Realiza el inicio de sesión de un usuario
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Usuario autenticado o null si la autenticación falla
     */
    public User login(String username, String password) {
        this.currentUser = authService.login(username, password);
        return this.currentUser;
    }
    
    /**
     * Registra un nuevo usuario y lo asocia con un miembro del staff
     * 
     * @param user Usuario a registrar
     * @param staffId ID del miembro del staff a asociar
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registerUser(User user, int staffId) {
        return authService.registerUser(user, staffId);
    }
    
    /**
     * Busca un miembro del staff por su documento de identidad
     * 
     * @param identityDocument Documento de identidad a buscar
     * @return Staff encontrado o null si no existe
     */
    public Staff findStaffByIdentityDocument(String identityDocument) {
        return authService.findStaffByIdentityDocument(identityDocument);
    }
    
    /**
     * Verifica si un miembro del staff ya está asociado con un usuario
     * 
     * @param staffId ID del miembro del staff
     * @return true si ya está asociado, false en caso contrario
     */
    public boolean isStaffAssociatedWithUser(int staffId) {
        return authService.isStaffAssociatedWithUser(staffId);
    }
    
    /**
     * Verifica si un nombre de usuario ya está en uso
     * 
     * @param username Nombre de usuario a verificar
     * @return true si ya está en uso, false en caso contrario
     */
    public boolean isUsernameInUse(String username) {
        return authService.isUsernameInUse(username);
    }
    
    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() {
        if (currentUser != null) {
            authService.updateLastLogin(currentUser.getUserId());
        }
        this.currentUser = null;
    }
    
    /**
     * Obtiene el usuario actual
     * 
     * @return Usuario actual o null si no hay sesión
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Actualiza la fecha del último inicio de sesión del usuario
     * 
     * @param username Nombre de usuario
     * @param lastLogin Timestamp del último inicio de sesión
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean updateUserLastLogin(String username, java.sql.Timestamp lastLogin) {
        String sql = "UPDATE Users SET lastLogin = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, lastLogin);
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la fecha de último inicio de sesión: " + e.getMessage());
            return false;
        }
    }
}