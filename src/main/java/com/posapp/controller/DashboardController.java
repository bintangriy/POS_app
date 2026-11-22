package com.posapp.controller;

import com.posapp.model.Product;
import com.posapp.service.DashboardService;
import com.posapp.util.CurrencyFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController {

    @FXML private Label lblTodaySales;
    @FXML private Label lblTodayTransactions;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLowStockCount;

    @FXML private TableView<Product> tblLowStock;
    @FXML private TableColumn<Product, String> colBarcode;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colStock;
    @FXML private TableColumn<Product, String> colMinStock;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        loadMetrics();
        setupTable();
    }

    private void loadMetrics() {
        lblTodaySales.setText(CurrencyFormatter.formatRupiah(dashboardService.getTodaySales()));
        lblTodayTransactions.setText(dashboardService.getTodayTransactionCount() + " Transaksi");
        lblTotalProducts.setText(dashboardService.getTotalProductCount() + " Produk");
        lblLowStockCount.setText(dashboardService.getLowStockCount() + " Item");
    }

    private void setupTable() {
        colBarcode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBarcode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCategory() != null ? c.getValue().getCategory().getName() : "-"
        ));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        colMinStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMinStockAlert())));

        tblLowStock.setItems(FXCollections.observableArrayList(dashboardService.getLowStockProducts()));
    }
}

