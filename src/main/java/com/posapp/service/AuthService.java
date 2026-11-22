package com.posapp.service;

import com.posapp.dao.UserDao;
import com.posapp.dao.csv.UserDaoCsv;
import com.posapp.model.User;
import com.posapp.util.PasswordSecurity;
import com.posapp.util.UserSession;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public AuthService() {
        this(new UserDaoCsv());
    }

    public User login(String username, String plainPassword) {
        if (username == null || plainPassword == null) {
            throw new IllegalArgumentException("Username dan password wajib diisi.");
        }

        User user = userDao.findByUsername(username.trim());
        if (user == null || !user.isActive()) {
            return null; // Pengguna tidak ditemukan atau nonaktif
        }

        if (PasswordSecurity.checkPassword(plainPassword, user.getPasswordHash())) {
            UserSession.getInstance().setCurrentUser(user);
            return user;
        }
        return null; // Password tidak cocok
    }

    public void logout() {
        UserSession.getInstance().clearSession();
    }

    public User getCurrentUser() {
        return UserSession.getInstance().getCurrentUser();
    }
}

