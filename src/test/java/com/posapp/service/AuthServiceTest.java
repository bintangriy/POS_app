package com.posapp.service;

import com.posapp.dao.UserDao;
import com.posapp.dao.csv.UserDaoCsv;
import com.posapp.model.Admin;
import com.posapp.model.User;
import com.posapp.util.CsvFileManager;
import com.posapp.util.PasswordSecurity;
import com.posapp.util.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp(@TempDir File tempDir) {
        CsvFileManager fileManager = new CsvFileManager(tempDir.getAbsolutePath());
        UserDao userDao = new UserDaoCsv(fileManager);

        String passHash = PasswordSecurity.hashPassword("secret123");
        User user = new Admin(1, "bintang", passHash, "Bintang Ariyanto");
        userDao.insert(user);

        authService = new AuthService(userDao);
    }

    @Test
    @DisplayName("Login dengan password yang cocok berhasil membentuk sesi pengguna")
    void testLoginSuccess() {
        User loggedIn = authService.login("bintang", "secret123");
        assertNotNull(loggedIn);
        assertEquals("Bintang Ariyanto", loggedIn.getFullName());
        assertTrue(UserSession.getInstance().isLoggedIn());

        authService.logout();
        assertFalse(UserSession.getInstance().isLoggedIn());
    }

    @Test
    @DisplayName("Login dengan password yang salah ditolak")
    void testLoginWrongPassword() {
        User loggedIn = authService.login("bintang", "wrongpass");
        assertNull(loggedIn);
        assertFalse(UserSession.getInstance().isLoggedIn());
    }
}

