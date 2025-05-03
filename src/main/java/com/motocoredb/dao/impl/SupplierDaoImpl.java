package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ISupplierDao;
import com.motocoredb.models.Supplier;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación de la interfaz {@link ISupplierDao} para gestionar proveedores en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar proveedores.
 */
public class SupplierDaoImpl implements ISupplierDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public SupplierDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Crea un nuevo proveedor en la base de datos. Si el proveedor no tiene Tax ID, se genera uno único.
     *
     * @param supplier la instancia de {@link Supplier} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     * @throws SQLException si ocurre un error durante la operación de inserción.
     */
    public boolean createSupplier(Supplier supplier) throws SQLException {
        if (supplier.getTaxId() == null || supplier.getTaxId().isEmpty()) {
            supplier.setTaxId(generateUniqueTaxId());
        }
        String sql = "INSERT INTO Suppliers (companyName, taxId, contactPerson, contactPhone, contactEmail, address, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getCompanyName());
            stmt.setString(2, supplier.getTaxId());
            stmt.setString(3, supplier.getContactPerson());
            stmt.setString(4, supplier.getContactPhone());
            stmt.setString(5, supplier.getContactEmail());
            stmt.setString(6, supplier.getAddress());
            stmt.setString(7, supplier.getStatus() != null ? supplier.getStatus() : "Active");
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setSupplierId(generatedKeys.getInt(1));
                    }
                }
            }
            return affectedRows > 0;
        }
    }

    /**
     * Obtiene un proveedor por su identificador.
     *
     * @param id el identificador del proveedor.
     * @return una instancia de {@link Supplier} si se encuentra; null en caso contrario.
     */
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

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link Supplier}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link Supplier} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
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

    /**
     * Obtiene una lista de todos los proveedores registrados.
     *
     * @return una lista de proveedores.
     */
    @Override
    public List<Supplier> listAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers";
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

    /**
     * Actualiza los datos de un proveedor en la base de datos.
     *
     * @param supplier la instancia de {@link Supplier} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
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

    /**
     * Cambia el estado de un proveedor especificado.
     *
     * @param id el identificador del proveedor.
     * @param status el nuevo estado que se asignará al proveedor.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
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

    /**
     * Obtiene un proveedor por su Tax ID.
     *
     * @param taxId el Tax ID del proveedor.
     * @return una instancia de {@link Supplier} si se encuentra; null en caso contrario.
     */
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

    /**
     * Genera un Tax ID único para un nuevo proveedor.
     *
     * @return un Tax ID único como cadena de texto.
     * @throws SQLException si ocurre un error en la validación de unicidad.
     */
    private String generateUniqueTaxId() throws SQLException {
        String taxId;
        boolean isUnique = false;
        do {
            taxId = UUID.randomUUID().toString().substring(0, 20);
            String checkSql = "SELECT COUNT(*) FROM Suppliers WHERE taxId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
                stmt.setString(1, taxId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        isUnique = true;
                    }
                }
            }
        } while (!isUnique);
        return taxId;
    }
}