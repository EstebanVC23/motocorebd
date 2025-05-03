package com.motocoredb.dao.interfaces;

import com.motocoredb.models.Customer;
import java.util.List;

/**
 * Interfaz para gestionar clientes en la base de datos.
 * Define métodos para crear, leer, actualizar y cambiar el estado de clientes.
 */
public interface ICustomerDao {

    /**
     * Crea un nuevo cliente en la base de datos.
     *
     * @param customer la instancia de {@link Customer} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createCustomer(Customer customer);

    /**
     * Obtiene un cliente por su identificador.
     *
     * @param id el identificador del cliente.
     * @return una instancia de {@link Customer} si se encuentra; null en caso contrario.
     */
    Customer getById(int id);

    /**
     * Obtiene una lista de todos los clientes, incluyendo administradores.
     *
     * @return una lista de instancias de {@link Customer}.
     */
    List<Customer> listAllAdmin();

    /**
     * Obtiene una lista de todos los clientes activos.
     *
     * @return una lista de clientes activos.
     */
    List<Customer> listAll();

    /**
     * Actualiza los datos de un cliente en la base de datos.
     *
     * @param customer la instancia de {@link Customer} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateCustomer(Customer customer);

    /**
     * Cambia el estado de un cliente especificado.
     *
     * @param id el identificador del cliente.
     * @param status el nuevo estado que se asignará al cliente.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changeStatus(int id, String status);
}