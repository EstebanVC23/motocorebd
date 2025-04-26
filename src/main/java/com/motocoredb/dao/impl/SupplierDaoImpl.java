package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ISupplierDao;
import com.motocoredb.models.Supplier;
import com.motocoredb.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoImpl implements ISupplierDao {
    private final Connection connection;

    public SupplierDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (companyName, taxId, contactPerson, contactPhone, " +
                    "contactEmail, address, status, createdAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getCompanyName());
            stmt.setString(2, supplier.getTaxId());
            stmt.setString(3, supplier.getContactPerson());
            stmt.setString(4, supplier.getContactPhone());
            stmt.setString(5, supplier.getContactEmail());
            stmt.setString(6, supplier.getAddress());
            stmt.setString(7, supplier.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setSupplierId(generatedKeys.getInt(1));
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
    public Supplier getById(int id) {
        String sql = "SELECT * FROM Suppliers WHERE supplierId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapSupplier(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplierId"));
        supplier.setCompanyName(rs.getString("companyName"));
        supplier.setTaxId(rs.getString("taxId"));
        supplier.setContactPerson(rs.getString("contactPerson"));
        supplier.setContactPhone(rs.getString("contactPhone"));
        supplier.setContactEmail(rs.getString("contactEmail"));
        supplier.setAddress(rs.getString("address"));
        supplier.setStatus(rs.getString("status"));
        supplier.setCreatedAt(rs.getTimestamp("createdAt"));
        return supplier;
    }

    @Override
    public List<Supplier> listAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE status = 'Active'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(mapSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET companyName = ?, taxId = ?, contactPerson = ?, " +
                    "contactPhone = ?, contactEmail = ?, address = ?, status = ? " +
                    "WHERE supplierId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, supplier.getCompanyName());
            stmt.setString(2, supplier.getTaxId());
            stmt.setString(3, supplier.getContactPerson());
            stmt.setString(4, supplier.getContactPhone());
            stmt.setString(5, supplier.getContactEmail());
            stmt.setString(6, supplier.getAddress());
            stmt.setString(7, supplier.getStatus());
            stmt.setInt(8, supplier.getSupplierId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Suppliers SET status = ? WHERE supplierId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Supplier getByTaxId(String taxId) {
        String sql = "SELECT * FROM Suppliers WHERE taxId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, taxId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapSupplier(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}