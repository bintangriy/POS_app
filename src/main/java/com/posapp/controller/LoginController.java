package com.posapp.controller;

import com.posapp.app.MainApp;
import com.posapp.model.User;
import com.posapp.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        lblError.setVisible(false);
    }

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            showError("Username dan password wajib diisi.");
            return;
        }

        try {
            User user = authService.login(username, password);
            if (user != null) {
                lblError.setVisible(false);
                MainApp.showMainScreen();
            } else {
                showError("Username atau password salah / akun nonaktif.");
            }
        } catch (Exception e) {
            showError("Kesalahan sistem: " + e.getMessage());
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}

