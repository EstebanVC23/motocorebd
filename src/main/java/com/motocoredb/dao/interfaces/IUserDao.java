package com.motocoredb.dao.interfaces;

import com.motocoredb.models.User;
import java.util.List;

/**
 * Interfaz para gestionar usuarios en la base de datos.
 * Define métodos para crear, leer, actualizar y gestionar información de los usuarios.
 */
public interface IUserDao {

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param user la instancia de {@link User} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createUser(User user);

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id el identificador del usuario.
     * @return una instancia de {@link User} si se encuentra; null en caso contrario.
     */
    User getById(int id);

    /**
     * Obtiene una lista de todos los usuarios registrados y activos en la base de datos.
     *
     * @return una lista de instancias de {@link User}.
     */
    List<User> listAll();

    /**
     * Actualiza los datos de un usuario en la base de datos.
     *
     * @param user la instancia de {@link User} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateUser(User user);

    /**
     * Cambia el estado de un usuario especificado.
     *
     * @param id el identificador del usuario.
     * @param status el nuevo estado que se asignará al usuario.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changeStatus(int id, String status);

    /**
     * Cambia la contraseña de un usuario especificado.
     *
     * @param userId el identificador del usuario.
     * @param newPassword la nueva contraseña que se asignará al usuario.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changePassword(int userId, String newPassword);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario a buscar.
     * @return una instancia de {@link User} si se encuentra; null en caso contrario.
     */
    User findByUsername(String username);

    /**
     * Actualiza la última fecha de inicio de sesión de un usuario.
     *
     * @param userId el identificador del usuario.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateLastLogin(int userId);
}