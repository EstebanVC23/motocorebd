package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IUserDao;
import com.motocoredb.models.User;
import com.motocoredb.utils.PasswordUtils;

public class AuthService {
    private final IUserDao userDao;
    
    public AuthService(IUserDao userDao) {
        this.userDao = userDao;
    }
    
    public User login(String username, String password) {
        try {
            User user = userDao.findByUsername(username);
            
            if (user != null) {
                System.out.println("[DEBUG] Contraseña almacenada: " + user.getPassword());
                
                // Verificación para contraseña hasheada
                boolean isValid = PasswordUtils.verify(password, user.getPassword());
                
                // Si falla, verificar si es contraseña plana (solo durante transición)
                if (!isValid && user.getPassword().equals(password)) {
                    System.out.println("[WARNING] Usando contraseña plana - Debe actualizarse");
                    isValid = true;
                    
                    // Actualizar a contraseña hasheada automáticamente
                    String hashedPassword = PasswordUtils.encrypt(password);
                    userDao.changePassword(user.getUserId(), hashedPassword);
                    System.out.println("[INFO] Contraseña actualizada a formato hasheado");
                }
                
                if (isValid) {
                    userDao.updateLastLogin(user.getUserId());
                    return user;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("[ERROR] Error en autenticación: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean register(User user) {
        try {
            // Validación básica
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }
            
            // Verificar si el usuario ya existe
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new IllegalStateException("El nombre de usuario ya existe");
            }
            
            // Hash de la contraseña ANTES de almacenarla
            String encryptedPassword = PasswordUtils.encrypt(user.getPassword());
            user.setPassword(encryptedPassword);
            user.setStatus("Active");
            
            System.out.println("[DEBUG] Registrando usuario con hash: " + encryptedPassword);
            
            return userDao.createUser(user);
        } catch (Exception e) {
            System.err.println("[ERROR] Error en registro: " + e.getMessage());
            return false;
        }
    }
}