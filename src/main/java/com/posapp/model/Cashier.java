package com.posapp.model;

import com.posapp.model.enums.Role;

/**
 * Kelas konkrit yang merepresentasikan pengguna dengan peran Kasir.
 */
public class Cashier extends User {
    private String terminalId;

    public Cashier(int id, String username, String passwordHash, String fullName, String terminalId) {
        super(id, username, passwordHash, fullName, Role.CASHIER);
        this.terminalId = terminalId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @Override
    public boolean canAccessAdminMenu() {
        return false;
    }
}

