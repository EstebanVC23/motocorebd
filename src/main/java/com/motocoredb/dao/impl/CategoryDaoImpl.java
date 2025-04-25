package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ICategoryDao;
import com.motocoredb.models.ProductCategory;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements ICategoryDao {
    private final Connection connection;

    public CategoryDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

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

    private ProductCategory mapCategory(ResultSet rs) throws SQLException {
        ProductCategory category = new ProductCategory();
        category.setCategoryId(rs.getInt("categoryId"));
        category.setCategoryName(rs.getString("categoryName"));
        category.setDescription(rs.getString("description"));
        category.setStatus(rs.getString("status"));
        return category;
    }

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