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
        String sql = "INSERT INTO Products (productCode, name, categoryId, purchasePrice, salePrice) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getName());
            stmt.setInt(3, product.getCategoryId());
            stmt.setDouble(4, product.getPurchasePrice());
            stmt.setDouble(5, product.getSalePrice());
            return stmt.executeUpdate() > 0;
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
        product.setCategoryId(rs.getInt("categoryId"));
        product.setPurchasePrice(rs.getDouble("purchasePrice"));
        product.setSalePrice(rs.getDouble("salePrice"));
        product.setCurrentStock(rs.getInt("currentStock"));
        product.setMinimumStock(rs.getInt("minimumStock"));
        product.setSupplierId(rs.getInt("supplierId"));
        product.setStatus(rs.getString("status"));
        product.setRegistrationDate(rs.getTimestamp("registrationDate"));
        product.setLastUpdate(rs.getTimestamp("lastUpdate"));
        return product;
    }

    @Override
    public List<Product> listAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";
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
        String sql = "UPDATE Products SET name = ?, categoryId = ?, purchasePrice = ?, salePrice = ? WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCategoryId());
            stmt.setDouble(3, product.getPurchasePrice());
            stmt.setDouble(4, product.getSalePrice());
            stmt.setInt(5, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE Products SET currentStock = currentStock + ? WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}