package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IUserDao;
import com.motocoredb.models.User;
import com.motocoredb.utils.PasswordUtils;
import java.util.List;

public class UserService {
    private final IUserDao userDao;
    
    public UserService(IUserDao userDao) {
        this.userDao = userDao;
    }
    
    public boolean changePassword(int userId, String newPassword) {
        String encryptedPassword = PasswordUtils.encrypt(newPassword);
        return userDao.changePassword(userId, encryptedPassword);
    }
    
    public boolean updateUserProfile(User user) {
        return userDao.updateUser(user);
    }
    
    public List<User> getAllUsers() {
        return userDao.listAll();
    }
    
    public boolean deactivateUser(int userId) {
        return userDao.changeStatus(userId, "Inactive");
    }
}