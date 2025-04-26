package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IProductDao;
import com.motocoredb.models.Product;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements IProductDao {
    private final Connection connection;

    public ProductDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createProduct(Product product) {
        String sql = "INSERT INTO Products (productCode, name, description, categoryId, purchasePrice, " +
                    "salePrice, currentStock, minStock, supplierId, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCategoryId());
            stmt.setDouble(5, product.getPurchasePrice());
            stmt.setDouble(6, product.getSalePrice());
            stmt.setInt(7, product.getCurrentStock());
            stmt.setInt(8, product.getMinStock());
            stmt.setInt(9, product.getSupplierId());
            stmt.setString(10, product.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Product getById(int id) {
        String sql = "SELECT * FROM Products WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("productId"));
        product.setProductCode(rs.getString("productCode"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategoryId(rs.getInt("categoryId"));
        product.setPurchasePrice(rs.getDouble("purchasePrice"));
        product.setSalePrice(rs.getDouble("salePrice"));
        product.setCurrentStock(rs.getInt("currentStock"));
        product.setMinStock(rs.getInt("minStock"));
        product.setSupplierId(rs.getInt("supplierId"));
        product.setStatus(rs.getString("status"));
        product.setCreatedAt(rs.getTimestamp("createdAt"));
        product.setUpdatedAt(rs.getTimestamp("updatedAt"));
        return product;
    }

    @Override
    public List<Product> listAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE status = 'Active'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET name = ?, description = ?, categoryId = ?, purchasePrice = ?, " +
                    "salePrice = ?, currentStock = ?, minStock = ?, supplierId = ?, status = ?, " +
                    "updatedAt = CURRENT_TIMESTAMP WHERE productId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setInt(3, product.getCategoryId());
            stmt.setDouble(4, product.getPurchasePrice());
            stmt.setDouble(5, product.getSalePrice());
            stmt.setInt(6, product.getCurrentStock());
            stmt.setInt(7, product.getMinStock());
            stmt.setInt(8, product.getSupplierId());
            stmt.setString(9, product.getStatus());
            stmt.setInt(10, product.getProductId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE Products SET currentStock = currentStock + ?, updatedAt = CURRENT_TIMESTAMP " +
                    "WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeStatus(int productId, String status) {
        String sql = "UPDATE Products SET status = ?, updatedAt = CURRENT_TIMESTAMP WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}