package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Supplier;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz para gestionar proveedores en la base de datos.
 * Define métodos para crear, leer, actualizar y gestionar datos relacionados con los proveedores.
 */
public interface ISupplierDao {

    /**
     * Crea un nuevo proveedor en la base de datos.
     *
     * @param supplier la instancia de {@link Supplier} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     * @throws SQLException si ocurre un error durante la operación de inserción.
     */
    boolean createSupplier(Supplier supplier) throws SQLException;

    /**
     * Obtiene un proveedor por su identificador.
     *
     * @param id el identificador del proveedor.
     * @return una instancia de {@link Supplier} si se encuentra; null en caso contrario.
     */
    Supplier getById(int id);

    /**
     * Obtiene una lista de todos los proveedores registrados en la base de datos.
     *
     * @return una lista de instancias de {@link Supplier}.
     */
    List<Supplier> listAll();

    /**
     * Actualiza los datos de un proveedor en la base de datos.
     *
     * @param supplier la instancia de {@link Supplier} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateSupplier(Supplier supplier);

    /**
     * Cambia el estado de un proveedor especificado.
     *
     * @param id el identificador del proveedor.
     * @param status el nuevo estado que se asignará al proveedor.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changeStatus(int id, String status);

    /**
     * Obtiene un proveedor por su Tax ID.
     *
     * @param taxId el Tax ID del proveedor.
     * @return una instancia de {@link Supplier} si se encuentra; null en caso contrario.
     */
    Supplier getByTaxId(String taxId);
}