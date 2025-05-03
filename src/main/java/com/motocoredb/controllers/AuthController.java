package com.motocoredb.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.motocoredb.models.Staff;
import com.motocoredb.models.User;
import com.motocoredb.services.AuthService;
import com.motocoredb.utils.DBConnection;
import com.motocoredb.utils.SessionManager;

/**
 * Controlador para la autenticación de usuarios y gestión de sesiones.
 * Proporciona métodos para iniciar sesión, registrar usuarios y gestionar la sesión actual.
 */
public class AuthController {
    private final AuthService authService;
    private User currentUser;
    
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

    /**
     * Actualiza la fecha del último inicio de sesión del usuario antes de cerrar sesión
     * Este método debe ser llamado antes de logout()
     * 
     * @param username Nombre de usuario
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean updateLastLoginBeforeLogout(String username) {
        String sql = "UPDATE Users SET lastLogin = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            
            pstmt.setTimestamp(1, currentTimestamp);
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la fecha de último inicio de sesión en logout: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cierra la sesión del usuario actual
     * No intenta actualizar la base de datos, solo limpia la sesión actual
     */
    public void logout() {
        SessionManager.clearSession();
    }
}