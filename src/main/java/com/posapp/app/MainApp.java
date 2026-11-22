package com.posapp.app;

import com.posapp.util.AppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        try {
            // Inisialisasi konfigurasi Java I/O
            AppConfig.load();
        } catch (Exception e) {
            System.err.println("Peringatan konfigurasi: " + e.getMessage());
        }

        showLoginScreen();
    }

    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/posapp/views/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 480, 520);
            primaryStage.setTitle("POS_app - Login Sistem");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/posapp/views/main_layout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1100, 720);
            primaryStage.setTitle("POS_app - Point of Sale & Inventory Management");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

