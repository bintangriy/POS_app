package com.posapp.model;

import com.posapp.model.enums.Role;

/**
 * Kelas abstrak dasar yang merepresentasikan pengguna sistem.
 * Menerapkan enkapsulasi atribut dan kontrak polimorfik hak akses.
 */
public abstract class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private Role role;
    private boolean active;

    public User(int id, String username, String passwordHash, String fullName, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Method abstrak untuk menentukan apakah peran pengguna memiliki akses menu admin.
     */
    public abstract boolean canAccessAdminMenu();

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}

