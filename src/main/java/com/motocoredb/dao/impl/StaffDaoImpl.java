package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IStaffDao;
import com.motocoredb.models.Staff;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz {@link IStaffDao} para gestionar empleados en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y eliminar empleados.
 */
public class StaffDaoImpl implements IStaffDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public StaffDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Crea un nuevo empleado en la base de datos.
     *
     * @param staff la instancia de {@link Staff} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean createStaff(Staff staff) {
        String sql = "INSERT INTO Staff (fullName, identityDocument, position, specialty, " +
                "phone, email, address, hireDate, status, userId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, staff.getFullName());
            stmt.setString(2, staff.getIdentityDocument());
            stmt.setString(3, staff.getPosition());
            stmt.setString(4, staff.getSpecialty());
            stmt.setString(5, staff.getPhone());
            stmt.setString(6, staff.getEmail());
            stmt.setString(7, staff.getAddress());
            stmt.setDate(8, new java.sql.Date(staff.getHireDate().getTime()));
            stmt.setString(9, staff.getStatus());
            if (staff.getUserId() == 0) {
                stmt.setNull(10, Types.INTEGER);
            } else {
                stmt.setInt(10, staff.getUserId());
            }
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        staff.setStaffId(generatedKeys.getInt(1));
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

    /**
     * Obtiene un empleado por su identificador.
     *
     * @param id el identificador del empleado.
     * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
     */
    @Override
    public Staff getById(int id) {
        String sql = "SELECT * FROM Staff WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mapea un objeto {@link ResultSet} a una instancia de {@link Staff}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link Staff} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private Staff mapStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staffId"));
        staff.setFullName(rs.getString("fullName"));
        staff.setIdentityDocument(rs.getString("identityDocument"));
        staff.setPosition(rs.getString("position"));
        staff.setSpecialty(rs.getString("specialty"));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setAddress(rs.getString("address"));
        staff.setHireDate(rs.getDate("hireDate"));
        staff.setStatus(rs.getString("status"));
        staff.setUserId(rs.getInt("userId"));
        return staff;
    }

    /**
     * Obtiene una lista de todos los empleados registrados.
     *
     * @return una lista de empleados.
     */
    @Override
    public List<Staff> listAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM Staff";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                staffList.add(mapStaff(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }

    /**
     * Actualiza los datos de un empleado en la base de datos.
     *
     * @param staff la instancia de {@link Staff} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     * @throws SQLException si ocurre un error en la validación o actualización.
     */
    @Override
    public boolean updateStaff(Staff staff) throws SQLException {
        if (!isIdentityDocumentUnique(staff.getIdentityDocument(), staff.getStaffId())) {
            throw new IllegalArgumentException("El documento de identidad ya está en uso por otro empleado.");
        }
        String sql = "UPDATE Staff SET fullName = ?, identityDocument = ?, position = ?, specialty = ?, phone = ?, email = ?, address = ?, status = ? WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, staff.getFullName());
            stmt.setString(2, staff.getIdentityDocument());
            stmt.setString(3, staff.getPosition());
            stmt.setString(4, staff.getSpecialty());
            stmt.setString(5, staff.getPhone());
            stmt.setString(6, staff.getEmail());
            stmt.setString(7, staff.getAddress());
            stmt.setString(8, staff.getStatus() != null ? staff.getStatus() : "Active");
            stmt.setInt(9, staff.getStaffId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica si el documento de identidad es único entre los empleados registrados.
     *
     * @param identityDocument el documento de identidad a verificar.
     * @param staffId el identificador del empleado actual para excluirlo de la verificación.
     * @return true si el documento es único; false en caso contrario.
     * @throws SQLException si ocurre un error en la consulta de la base de datos.
     */
    private boolean isIdentityDocumentUnique(String identityDocument, int staffId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Staff WHERE identityDocument = ? AND staffId != ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, identityDocument);
            stmt.setInt(2, staffId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Cambia el estado de un empleado especificado.
     *
     * @param id el identificador del empleado.
     * @param status el nuevo estado que se asignará al empleado.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Staff SET status = ? WHERE staffId = ?";
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
     * Obtiene un empleado por su identificador de usuario.
     *
     * @param userId el identificador de usuario del empleado.
     * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
     */
    @Override
    public Staff getByStaffId(int userId) {
        String sql = "SELECT * FROM Staff WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza el identificador de usuario asociado a un empleado.
     *
     * @param staffId el identificador del empleado.
     * @param userId el nuevo identificador de usuario a asociar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean updateUserIdForStaff(int staffId, int userId) {
        String sql = "UPDATE Staff SET userId = ? WHERE staffId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, staffId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
        /**
         * Busca un empleado por su documento de identidad.
         *
         * @param identityDocument el documento de identidad del empleado.
         * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
         */
        @Override
        public Staff findByIdentityDocument(String identityDocument) {
            String sql = "SELECT * FROM Staff WHERE identityDocument = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, identityDocument);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapStaff(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    
        /**
         * Busca un empleado por su identificador.
         *
         * @param id el identificador del empleado.
         * @return una instancia de {@link Staff} si se encuentra; null en caso contrario.
         */
        @Override
        public Staff findById(int id) {
            return getById(id);
        }
    }