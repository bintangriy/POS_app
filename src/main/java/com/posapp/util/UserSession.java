package com.posapp.util;

import com.posapp.model.User;

/**
 * Pengelola sesi pengguna aktif di memori aplikasi (Singleton).
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public synchronized void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized void clearSession() {
        this.currentUser = null;
    }
}

