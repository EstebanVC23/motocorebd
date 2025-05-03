package com.motocoredb.dao.interfaces;

import com.motocoredb.models.ProductCategory;
import java.util.List;

/**
 * Interfaz para gestionar categorías de productos en la base de datos.
 * Define métodos para crear, leer, actualizar y cambiar el estado de categorías.
 */
public interface ICategoryDao {

    /**
     * Crea una nueva categoría en la base de datos.
     *
     * @param category la instancia de {@link ProductCategory} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean createCategory(ProductCategory category);

    /**
     * Obtiene una categoría por su identificador.
     *
     * @param id el identificador de la categoría.
     * @return una instancia de {@link ProductCategory} si se encuentra; null en caso contrario.
     */
    ProductCategory getById(int id);

    /**
     * Obtiene una lista de todas las categorías registradas.
     *
     * @return una lista de instancias de {@link ProductCategory}.
     */
    List<ProductCategory> listAll();

    /**
     * Actualiza los datos de una categoría en la base de datos.
     *
     * @param category la instancia de {@link ProductCategory} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean updateCategory(ProductCategory category);

    /**
     * Cambia el estado de una categoría especificada.
     *
     * @param id el identificador de la categoría.
     * @param status el nuevo estado que se asignará a la categoría.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    boolean changeStatus(int id, String status);
}