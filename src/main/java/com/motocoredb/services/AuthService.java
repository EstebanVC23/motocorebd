package com.motocoredb.services;

import com.motocoredb.dao.interfaces.IUserDao;
import com.motocoredb.models.User;
import com.motocoredb.utils.PasswordUtils;

public class AuthService {
    private final IUserDao userDao;
    
    public AuthService(IUserDao userDao) {
        this.userDao = userDao;
    }
    
    public User login(String username, String password) {
        String encryptedPassword = PasswordUtils.encrypt(password);
        return userDao.authenticate(username, encryptedPassword);
    }
    
    public boolean register(User user) {
        user.setPassword(PasswordUtils.encrypt(user.getPassword()));
        return userDao.createUser(user);
    }
}