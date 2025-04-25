package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IInventoryDao;
import com.motocoredb.models.InventoryMovement;
import com.motocoredb.models.Product;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDaoImpl implements IInventoryDao {
    private final Connection connection;

    public InventoryDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public List<InventoryMovement> getMovements(int productId) {
        List<InventoryMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM InventoryMovements WHERE productId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                movements.add(mapMovement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movements;
    }

    private InventoryMovement mapMovement(ResultSet rs) throws SQLException {
        InventoryMovement movement = new InventoryMovement();
        movement.setMovementId(rs.getInt("movementId"));
        movement.setProductId(rs.getInt("productId"));
        movement.setMovementType(rs.getString("movementType"));
        movement.setQuantity(rs.getInt("quantity"));
        movement.setMovementDate(rs.getTimestamp("movementDate"));
        movement.setUserId(rs.getInt("userId"));
        movement.setReferenceId(rs.getInt("referenceId"));
        movement.setReferenceType(rs.getString("referenceType"));
        movement.setNotes(rs.getString("notes"));
        return movement;
    }

    @Override
    public boolean adjustInventory(int productId, int quantity, String notes) {
        String sql = "INSERT INTO InventoryMovements (productId, movementType, quantity, notes) VALUES (?, 'Adjustment', ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, quantity);
            stmt.setString(3, notes);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE currentStock < minimumStock";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("productId"));
                product.setName(rs.getString("name"));
                product.setCurrentStock(rs.getInt("currentStock"));
                product.setMinimumStock(rs.getInt("minimumStock"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}