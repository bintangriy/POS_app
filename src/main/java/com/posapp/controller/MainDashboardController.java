package com.posapp.controller;

import com.posapp.app.MainApp;
import com.posapp.model.User;
import com.posapp.service.AuthService;
import com.posapp.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainDashboardController {

    @FXML private Label lblUserFullName;
    @FXML private Label lblUserRole;
    @FXML private StackPane contentArea;

    @FXML private Button btnMenuDashboard;
    @FXML private Button btnMenuPos;
    @FXML private Button btnMenuProducts;
    @FXML private Button btnMenuInventory;
    @FXML private Button btnMenuHistory;
    @FXML private Button btnMenuReports;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user != null) {
            lblUserFullName.setText(user.getFullName());
            lblUserRole.setText(user.getRole().name());

            // Kontrol visibilitas menu berbasis peran polimorfik
            boolean isAdmin = user.canAccessAdminMenu();
            btnMenuProducts.setVisible(isAdmin);
            btnMenuProducts.setManaged(isAdmin);
            btnMenuInventory.setVisible(isAdmin);
            btnMenuInventory.setManaged(isAdmin);
            btnMenuReports.setVisible(isAdmin);
            btnMenuReports.setManaged(isAdmin);

            // Buka halaman default
            if (isAdmin) {
                openDashboard();
            } else {
                openPos();
            }
        }
    }

    @FXML
    public void openDashboard() {
        loadView("/com/posapp/views/dashboard.fxml");
    }

    @FXML
    public void openPos() {
        loadView("/com/posapp/views/pos.fxml");
    }

    @FXML
    public void openProducts() {
        loadView("/com/posapp/views/products.fxml");
    }

    @FXML
    public void openInventory() {
        loadView("/com/posapp/views/inventory.fxml");
    }

    @FXML
    public void openHistory() {
        loadView("/com/posapp/views/transaction_history.fxml");
    }

    @FXML
    public void openReports() {
        loadView("/com/posapp/views/reports.fxml");
    }

    @FXML
    public void handleLogout() {
        authService.logout();
        MainApp.showLoginScreen();
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

