package com.posapp.model;

import com.posapp.model.enums.Role;

/**
 * Kelas konkrit yang merepresentasikan pengguna dengan hak akses Administrator.
 */
public class Admin extends User {

    public Admin(int id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.ADMIN);
    }

    @Override
    public boolean canAccessAdminMenu() {
        return true;
    }
}

