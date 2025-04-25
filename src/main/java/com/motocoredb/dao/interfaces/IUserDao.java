package com.motocoredb.dao.interfaces;

import com.motocoredb.models.User;
import java.util.List;

public interface IUserDao {
    User authenticate(String username, String password);
    boolean createUser(User user);
    User getById(int id);
    List<User> listAll();
    boolean updateUser(User user);
    boolean changeStatus(int id, String status);
    boolean changePassword(int userId, String newPassword);
}