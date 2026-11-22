package com.posapp.dao.csv;

import com.posapp.dao.UserDao;
import com.posapp.model.Admin;
import com.posapp.model.Cashier;
import com.posapp.model.User;
import com.posapp.model.enums.Role;
import com.posapp.util.CsvFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDaoCsv implements UserDao {
    private static final String FILE_NAME = "users.csv";
    private static final List<String> HEADER = Arrays.asList(
        "id", "username", "password_hash", "full_name", "role", "terminal_id", "is_active"
    );

    private final CsvFileManager fileManager;

    public UserDaoCsv(CsvFileManager fileManager) {
        this.fileManager = fileManager;
        try {
            this.fileManager.ensureFileWithHeader(FILE_NAME, HEADER);
        } catch (IOException e) {
            throw new RuntimeException("Gagal inisialisasi file " + FILE_NAME, e);
        }
    }

    public UserDaoCsv() {
        this(new CsvFileManager());
    }

    @Override
    public User findById(int id) {
        return findAll().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) return null;
        return findAll().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst().orElse(null);
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try {
            List<List<String>> rows = fileManager.readAll(FILE_NAME);
            for (int i = 1; i < rows.size(); i++) { // lewati header
                List<String> r = rows.get(i);
                if (r.size() >= 7) {
                    int id = Integer.parseInt(r.get(0));
                    String username = r.get(1);
                    String passHash = r.get(2);
                    String fullName = r.get(3);
                    Role role = Role.valueOf(r.get(4).toUpperCase());
                    String terminalId = r.get(5);
                    boolean active = Boolean.parseBoolean(r.get(6));

                    User user;
                    if (role == Role.ADMIN) {
                        user = new Admin(id, username, passHash, fullName);
                    } else {
                        user = new Cashier(id, username, passHash, fullName, terminalId);
                    }
                    user.setActive(active);
                    list.add(user);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca data pengguna: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean insert(User user) {
        List<User> all = findAll();
        int newId = all.stream().mapToInt(User::getId).max().orElse(0) + 1;
        user.setId(newId);
        all.add(user);
        return saveAll(all);
    }

    @Override
    public boolean update(User user) {
        List<User> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == user.getId()) {
                all.set(i, user);
                found = true;
                break;
            }
        }
        return found && saveAll(all);
    }

    @Override
    public boolean delete(int id) {
        List<User> all = findAll();
        boolean removed = all.removeIf(u -> u.getId() == id);
        return removed && saveAll(all);
    }

    private boolean saveAll(List<User> users) {
        List<List<String>> records = new ArrayList<>();
        for (User u : users) {
            String terminal = (u instanceof Cashier) ? ((Cashier) u).getTerminalId() : "";
            records.add(Arrays.asList(
                String.valueOf(u.getId()),
                u.getUsername(),
                u.getPasswordHash(),
                u.getFullName(),
                u.getRole().name(),
                terminal != null ? terminal : "",
                String.valueOf(u.isActive())
            ));
        }
        try {
            fileManager.writeAll(FILE_NAME, HEADER, records);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan data pengguna: " + e.getMessage(), e);
        }
    }
}

