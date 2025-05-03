package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ICategoryDao;
import com.motocoredb.models.ProductCategory;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz {@link ICategoryDao} para gestionar categorías de productos en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar categorías.
 */
public class CategoryDaoImpl implements ICategoryDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public CategoryDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Crea una nueva categoría en la base de datos.
     *
     * @param category la instancia de {@link ProductCategory} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean createCategory(ProductCategory category) {
        String sql = "INSERT INTO ProductCategories (categoryName) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene una categoría por su identificador.
     *
     * @param id el identificador de la categoría.
     * @return una instancia de {@link ProductCategory} si se encuentra; null en caso contrario.
     */
    @Override
    public ProductCategory getById(int id) {
        String sql = "SELECT * FROM ProductCategories WHERE categoryId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link ProductCategory}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link ProductCategory} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private ProductCategory mapCategory(ResultSet rs) throws SQLException {
        ProductCategory category = new ProductCategory();
        category.setCategoryId(rs.getInt("categoryId"));
        category.setCategoryName(rs.getString("categoryName"));
        category.setDescription(rs.getString("description"));
        category.setStatus(rs.getString("status"));
        return category;
    }

    /**
     * Obtiene una lista de todas las categorías de productos.
     *
     * @return una lista de todas las categorías registradas.
     */
    @Override
    public List<ProductCategory> listAll() {
        List<ProductCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM ProductCategories";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Actualiza los datos de una categoría existente.
     *
     * @param category la instancia de {@link ProductCategory} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean updateCategory(ProductCategory category) {
        String sql = "UPDATE ProductCategories SET categoryName = ?, description = ? WHERE categoryId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getCategoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el estado de una categoría específica.
     *
     * @param id     el identificador de la categoría.
     * @param status el nuevo estado que se asignará a la categoría.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE ProductCategories SET status = ? WHERE categoryId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}