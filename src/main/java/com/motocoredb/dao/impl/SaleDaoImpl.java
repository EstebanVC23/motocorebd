package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ISaleDao;
import com.motocoredb.models.Sale;
import com.motocoredb.models.SaleDetail;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDaoImpl implements ISaleDao {

    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     */
    public SaleDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean createSale(Sale sale, List<SaleDetail> details) {
        try {
            connection.setAutoCommit(false);

            // Insertar la venta principal
            String saleQuery = "INSERT INTO Sales (invoiceNumber, saleDate, customerId, userId, subtotal, tax, discount, total, paymentMethod, status, notes) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement saleStmt = connection.prepareStatement(saleQuery, Statement.RETURN_GENERATED_KEYS);

            saleStmt.setString(1, sale.getInvoiceNumber());
            saleStmt.setTimestamp(2, sale.getSaleDate());
            saleStmt.setInt(3, sale.getCustomerId());
            saleStmt.setInt(4, sale.getUserId());
            saleStmt.setDouble(5, sale.getSubtotal());
            saleStmt.setDouble(6, sale.getTax());
            saleStmt.setDouble(7, sale.getDiscount());
            saleStmt.setDouble(8, sale.getTotal());
            saleStmt.setString(9, sale.getPaymentMethod());
            saleStmt.setString(10, sale.getStatus());
            saleStmt.setString(11, sale.getNotes());

            int saleRows = saleStmt.executeUpdate();

            // Obtener el ID de la venta generada
            ResultSet generatedKeys = saleStmt.getGeneratedKeys();
            int saleId = -1;
            if (generatedKeys.next()) {
                saleId = generatedKeys.getInt(1);
            }

            // Insertar los detalles de la venta
            String detailQuery = "INSERT INTO SaleDetails (saleId, productId, quantity, unitPrice, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement detailStmt = connection.prepareStatement(detailQuery);

            for (SaleDetail detail : details) {
                detailStmt.setInt(1, saleId);
                detailStmt.setInt(2, detail.getProductId());
                detailStmt.setInt(3, detail.getQuantity());
                detailStmt.setDouble(4, detail.getUnitPrice()); // Actualizar el precio unitario correctamente
                detailStmt.setDouble(5, detail.getSubtotal());
                detailStmt.addBatch();
            }

            int[] detailRows = detailStmt.executeBatch();

            connection.commit();
            return saleRows > 0 && detailRows.length == details.size();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public List<Sale> getByDateRange(String startDate, String endDate) {
        List<Sale> sales = new ArrayList<>();
        try {
            String query;
            PreparedStatement stmt;

            if (startDate == null && endDate == null) {
                // Consulta sin filtro de fechas
                query = "SELECT * FROM Sales";
                stmt = connection.prepareStatement(query);
            } else {
                // Consulta con filtro de fechas
                query = "SELECT * FROM Sales WHERE saleDate BETWEEN ? AND ?";
                stmt = connection.prepareStatement(query);
                stmt.setString(1, startDate);
                stmt.setString(2, endDate);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("saleId"),
                    rs.getString("invoiceNumber"),
                    rs.getTimestamp("saleDate"),
                    rs.getInt("customerId"),
                    rs.getInt("userId"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("tax"),
                    rs.getDouble("discount"),
                    rs.getDouble("total"),
                    rs.getString("paymentMethod"),
                    rs.getString("status"),
                    rs.getString("notes")
                );
                sales.add(sale);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    @Override
    public boolean cancelSale(int saleId) {
        try {
            String query = "UPDATE Sales SET status = 'Cancelled' WHERE saleId = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, saleId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SaleDetail> getDetails(int saleId) {
        List<SaleDetail> details = new ArrayList<>();
        try {
            String query = "SELECT * FROM SaleDetails WHERE saleId = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, saleId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SaleDetail detail = new SaleDetail(
                    rs.getInt("saleDetailId"),
                    rs.getInt("saleId"),
                    rs.getInt("productId"),
                    rs.getInt("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getDouble("subtotal")
                );
                details.add(detail);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    @Override
    public boolean updateSale(Sale sale) {
        String query = "UPDATE Sales SET invoiceNumber = ?, saleDate = ?, customerId = ?, userId = ?, "
                    + "subtotal = ?, tax = ?, discount = ?, total = ?, paymentMethod = ?, status = ?, notes = ? "
                    + "WHERE saleId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sale.getInvoiceNumber());
            stmt.setTimestamp(2, sale.getSaleDate());
            stmt.setInt(3, sale.getCustomerId());
            stmt.setInt(4, sale.getUserId());
            stmt.setDouble(5, sale.getSubtotal());
            stmt.setDouble(6, sale.getTax());
            stmt.setDouble(7, sale.getDiscount());
            stmt.setDouble(8, sale.getTotal());
            stmt.setString(9, sale.getPaymentMethod());
            stmt.setString(10, sale.getStatus());
            stmt.setString(11, sale.getNotes());
            stmt.setInt(12, sale.getSaleId());

            return stmt.executeUpdate() > 0; // Devuelve true si se actualizó correctamente
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Sale getSaleById(int saleId) {
        String query = "SELECT * FROM Sales WHERE saleId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Sale(
                        rs.getInt("saleId"),
                        rs.getString("invoiceNumber"),
                        rs.getTimestamp("saleDate"),
                        rs.getInt("customerId"),
                        rs.getInt("userId"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("tax"),
                        rs.getDouble("discount"),
                        rs.getDouble("total"),
                        rs.getString("paymentMethod"),
                        rs.getString("status"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra, devuelve null
    }

    @Override
    public SaleDetail getSaleDetailBySaleId(int saleId) {
        String query = "SELECT * FROM SaleDetails WHERE saleId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SaleDetail(
                    rs.getInt("saleDetailId"),
                    rs.getInt("saleId"),
                    rs.getInt("productId"),
                    rs.getInt("quantity"),
                    rs.getDouble("unitPrice"),
                    rs.getDouble("subtotal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Devuelve null si no se encuentra SaleDetail
    }
}