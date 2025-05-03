package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Staff;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz para gestionar empleados en la base de datos.
 * Define métodos para crear, leer, actualizar y gestionar datos relacionados con los empleados.
 */
public interface IStaffDao {

    /**
     * Crea un nuevo empleado en la base de datos.
     *
     * @param staff la instancia de {@link Staff} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createStaff(Staff staff);

    /**
     * Obtiene un empleado por su identificador.
     *
     * @param id el identificador del empleado.
     * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
     */
    Staff getById(int id);

    /**
     * Obtiene una lista de todos los empleados registrados en la base de datos.
     *
     * @return una lista de instancias de {@link Staff}.
     */
    List<Staff> listAll();

    /**
     * Actualiza los datos de un empleado en la base de datos.
     *
     * @param staff la instancia de {@link Staff} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     * @throws SQLException si ocurre un error en la consulta.
     */
    boolean updateStaff(Staff staff) throws SQLException;

    /**
     * Cambia el estado de un empleado especificado.
     *
     * @param id el identificador del empleado.
     * @param status el nuevo estado que se asignará al empleado.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changeStatus(int id, String status);

    /**
     * Obtiene un empleado por su identificador de usuario.
     *
     * @param userId el identificador de usuario del empleado.
     * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
     */
    Staff getByStaffId(int userId);

    /**
     * Actualiza el identificador de usuario asociado a un empleado.
     *
     * @param staffId el identificador del empleado.
     * @param userId el nuevo identificador de usuario a asociar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateUserIdForStaff(int staffId, int userId);

    /**
     * Busca un empleado por su documento de identidad.
     *
     * @param identityDocument el documento de identidad del empleado.
     * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
     */
    Staff findByIdentityDocument(String identityDocument);

    /**
     * Busca un empleado por su identificador.
     *
     * @param id el identificador del empleado.
     * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
     */
    Staff findById(int id);
}