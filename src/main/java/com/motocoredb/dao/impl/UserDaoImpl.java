package com.motocoredb.dao.impl;

import com.motocoredb.dao.interfaces.IUserDao;
import com.motocoredb.models.User;
import com.motocoredb.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz {@link IUserDao} para gestionar usuarios en la base de datos.
 * Proporciona métodos para crear, leer, actualizar y gestionar contraseñas de los usuarios.
 */
public class UserDaoImpl implements IUserDao {
    private final Connection connection;

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si ocurre un error al establecer la conexión.
     */
    public UserDaoImpl() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param user la instancia de {@link User} a insertar.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (fullName, username, password, role, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
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
     * Mapea un objeto {@link ResultSet} a una instancia de {@link User}.
     *
     * @param rs el {@link ResultSet} obtenido de la consulta.
     * @return una instancia de {@link User} con los datos del ResultSet.
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("userId"));
        user.setFullName(rs.getString("fullName"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("createdAt"));
        user.setLastLogin(rs.getTimestamp("lastLogin"));
        return user;
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * @param id el identificador del usuario.
     * @return una instancia de {@link User} si se encuentra; null en caso contrario.
     */
    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM Users WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene una lista de todos los usuarios activos registrados en la base de datos.
     *
     * @return una lista de usuarios activos.
     */
    @Override
    public List<User> listAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE status = 'Active'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Actualiza los datos de un usuario en la base de datos.
     *
     * @param user la instancia de {@link User} con los datos actualizados.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET fullName = ?, username = ?, role = ?, status = ? WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getStatus());
            stmt.setInt(5, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia el estado de un usuario especificado.
     *
     * @param id el identificador del usuario.
     * @param status el nuevo estado que se asignará al usuario.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean changeStatus(int id, String status) {
        String sql = "UPDATE Users SET status = ? WHERE userId = ?";
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
     * Cambia la contraseña de un usuario especificado.
     *
     * @param userId el identificador del usuario.
     * @param newPassword la nueva contraseña que se asignará.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET password = ?, lastLogin = CURRENT_TIMESTAMP WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario a buscar.
     * @return una instancia de {@link User} si se encuentra; null en caso contrario.
     */
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Actualiza la última fecha de inicio de sesión de un usuario.
     *
     * @param userId el identificador del usuario.
     * @return true si la operación fue exitosa; false en caso contrario.
     */
    @Override
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE Users SET lastLogin = CURRENT_TIMESTAMP WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}