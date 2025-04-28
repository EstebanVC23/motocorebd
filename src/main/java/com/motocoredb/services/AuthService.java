package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IUserDao;
import com.motocoredb.dao.interfaces.IStaffDao;
import com.motocoredb.models.User;
import com.motocoredb.models.Staff;
import com.motocoredb.utils.PasswordUtils;

import java.sql.Timestamp;
import java.time.Instant;

public class AuthService {
    private final IUserDao userDao;
    private final IStaffDao staffDao;

    public AuthService(IUserDao userDao, IStaffDao staffDao) {
        this.userDao = userDao;
        this.staffDao = staffDao;
    }

    /**
     * Realiza el inicio de sesión de un usuario
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Usuario autenticado o null si la autenticación falla
     */
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

    /**
     * Registra un nuevo usuario
     * 
     * @param user Usuario a registrar
     * @return true si el registro fue exitoso, false en caso contrario
     */
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

    /**
     * Registra un nuevo usuario y lo asocia con un miembro del staff
     * 
     * @param user    Usuario a registrar
     * @param staffId ID del miembro del staff a asociar
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registerUser(User user, int staffId) {
        try {
            // Validación básica
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }

            // Verificar si el usuario ya existe
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new IllegalStateException("El nombre de usuario ya existe");
            }

            // Hash de la contraseña ANTES de almacenarla usando PasswordUtils
            String encryptedPassword = PasswordUtils.encrypt(user.getPassword());
            user.setPassword(encryptedPassword);

            // Establecer timestamp de creación si no está definido
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(Timestamp.from(Instant.now()));
            }

            // Crear el usuario y obtener su ID
            boolean userCreated = userDao.createUser(user);

            if (userCreated) {
                // Obtener el ID del usuario recién creado
                User createdUser = userDao.findByUsername(user.getUsername());
                if (createdUser != null) {
                    // Asociar el staff con el usuario
                    boolean staffUpdated = updateUserIdForStaff(staffId, createdUser.getUserId());
                    return staffUpdated;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("[ERROR] Error en registro de usuario con staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza el userId en un miembro del staff
     * 
     * @param staffId ID del miembro del staff
     * @param userId  ID del usuario a asociar
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    private boolean updateUserIdForStaff(int staffId, int userId) {
        try {
            return staffDao.updateUserIdForStaff(staffId, userId);
        } catch (Exception e) {
            System.err.println("[ERROR] Error al actualizar userId en staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un miembro del staff por su documento de identidad
     * 
     * @param identityDocument Documento de identidad a buscar
     * @return Staff encontrado o null si no existe
     */
    public Staff findStaffByIdentityDocument(String identityDocument) {
        try {
            return staffDao.findByIdentityDocument(identityDocument);
        } catch (Exception e) {
            System.err.println("[ERROR] Error al buscar staff por documento: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica si un miembro del staff ya está asociado con un usuario
     * 
     * @param staffId ID del miembro del staff
     * @return true si ya está asociado, false en caso contrario
     */
    public boolean isStaffAssociatedWithUser(int staffId) {
        try {
            Staff staff = staffDao.findById(staffId);
            return staff != null && staff.getUserId() > 0;
        } catch (Exception e) {
            System.err.println("[ERROR] Error al verificar asociación de staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un nombre de usuario ya está en uso
     * 
     * @param username Nombre de usuario a verificar
     * @return true si ya está en uso, false en caso contrario
     */
    public boolean isUsernameInUse(String username) {
        try {
            return userDao.findByUsername(username) != null;
        } catch (Exception e) {
            System.err.println("[ERROR] Error al verificar nombre de usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza la última hora de inicio de sesión del usuario
     * 
     * @param userId ID del usuario
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean updateLastLogin(int userId) {
        return userDao.updateLastLogin(userId);
    }
}