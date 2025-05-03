package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.ICustomerDao;
import com.motocoredb.models.Customer;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz {@link ICustomerDao} para gestionar clientes en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar clientes.
 */
public class CustomerDaoImpl implements ICustomerDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public CustomerDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Crea un nuevo cliente en la base de datos.
     *
     * @param customer la instancia de {@link Customer} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (customerType, nameOrCompany, identityDocument, address, phone, email, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getCustomerType());
            stmt.setString(2, customer.getNameOrCompany());
            stmt.setString(3, customer.getIdentityDocument());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getPhone());
            stmt.setString(6, customer.getEmail());
            stmt.setString(7, customer.getStatus() != null ? customer.getStatus() : "Active");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomerId(generatedKeys.getInt(1));
                    }
                }
            }
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene un cliente por su identificador.
     *
     * @param id el identificador del cliente.
     * @return una instancia de {@link Customer} si se encuentra; null en caso contrario.
     */
    @Override
    public Customer getById(int id) {
        String sql = "SELECT * FROM Customers WHERE customerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link Customer}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link Customer} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customerId"));
        customer.setCustomerType(rs.getString("customerType"));
        customer.setNameOrCompany(rs.getString("nameOrCompany"));
        customer.setIdentityDocument(rs.getString("identityDocument"));
        customer.setAddress(rs.getString("address"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));
        customer.setCreatedAt(rs.getTimestamp("createdAt"));
        customer.setPurchaseCount(rs.getInt("purchaseCount"));
        customer.setStatus(rs.getString("status"));
        return customer;
    }

    /**
     * Obtiene una lista de todos los clientes, incluyendo administradores.
     *
     * @return una lista de todas las instancias de {@link Customer}.
     */
    @Override
    public List<Customer> listAllAdmin() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Obtiene una lista de todos los clientes activos.
     *
     * @return una lista de clientes activos.
     */
    @Override
    public List<Customer> listAll() {
        String query = "SELECT * FROM Customers WHERE status = 'Active'";
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Actualiza los datos de un cliente en la base de datos.
     *
     * @param customer la instancia de {@link Customer} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET nameOrCompany = ?, identityDocument = ?, address = ?, phone = ?, email = ?, status = ? WHERE customerId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getNameOrCompany());
            stmt.setString(2, customer.getIdentityDocument());
            stmt.setString(3, customer.getAddress());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getStatus() != null ? customer.getStatus() : "Active");
            stmt.setInt(7, customer.getCustomerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el estado de un cliente en la base de datos.
     *
     * @param id     el identificador del cliente.
     * @param status el nuevo estado que se asignará.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Customers SET status = ? WHERE customerId = ?";
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