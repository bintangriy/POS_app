package com.posapp.controller;

import com.posapp.model.Product;
import com.posapp.model.StockMovement;
import com.posapp.service.InventoryService;
import com.posapp.service.ProductService;
import com.posapp.util.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;

public class InventoryController {

    // Restock Tab
    @FXML private ComboBox<Product> cmbRestockProduct;
    @FXML private TextField txtRestockQty;
    @FXML private TextField txtRestockRemarks;
    @FXML private Label lblRestockMsg;

    // Adjustment Tab
    @FXML private ComboBox<Product> cmbAdjustProduct;
    @FXML private TextField txtAdjustNewStock;
    @FXML private TextField txtAdjustReason;
    @FXML private Label lblAdjustMsg;

    // Movement History Tab
    @FXML private TableView<StockMovement> tblMovements;
    @FXML private TableColumn<StockMovement, String> colDate;
    @FXML private TableColumn<StockMovement, String> colProduct;
    @FXML private TableColumn<StockMovement, String> colType;
    @FXML private TableColumn<StockMovement, String> colQty;
    @FXML private TableColumn<StockMovement, String> colBefore;
    @FXML private TableColumn<StockMovement, String> colAfter;
    @FXML private TableColumn<StockMovement, String> colUser;
    @FXML private TableColumn<StockMovement, String> colRemarks;

    private final InventoryService inventoryService = new InventoryService();
    private final ProductService productService = new ProductService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        loadProductCombos();
        setupMovementTable();
        loadMovements();
    }

    private void loadProductCombos() {
        var products = FXCollections.observableArrayList(productService.getAllProducts());
        cmbRestockProduct.setItems(products);
        cmbAdjustProduct.setItems(products);
        if (!products.isEmpty()) {
            cmbRestockProduct.getSelectionModel().selectFirst();
            cmbAdjustProduct.getSelectionModel().selectFirst();
        }
    }

    private void setupMovementTable() {
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt().format(FMT)));
        colProduct.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getProduct() != null ? c.getValue().getProduct().getName() : "-"
        ));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMovementType().name()));
        colQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        colBefore.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStockBefore())));
        colAfter.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStockAfter())));
        colUser.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getUser() != null ? c.getValue().getUser().getFullName() : "-"
        ));
        colRemarks.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRemarks()));
    }

    private void loadMovements() {
        tblMovements.setItems(FXCollections.observableArrayList(inventoryService.getAllMovements()));
    }

    @FXML
    public void handleProcessRestock() {
        try {
            Product product = cmbRestockProduct.getSelectionModel().getSelectedItem();
            int qty = Integer.parseInt(txtRestockQty.getText().trim());
            String remarks = txtRestockRemarks.getText().trim();

            inventoryService.restockProduct(product.getId(), qty, UserSession.getInstance().getCurrentUser(), remarks);
            lblRestockMsg.setText("Berhasil menambah stok " + qty + " unit untuk " + product.getName());
            txtRestockQty.clear();
            txtRestockRemarks.clear();
            loadProductCombos();
            loadMovements();
        } catch (Exception e) {
            lblRestockMsg.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void handleProcessAdjustment() {
        try {
            Product product = cmbAdjustProduct.getSelectionModel().getSelectedItem();
            int newStock = Integer.parseInt(txtAdjustNewStock.getText().trim());
            String reason = txtAdjustReason.getText().trim();

            inventoryService.adjustStock(product.getId(), newStock, UserSession.getInstance().getCurrentUser(), reason);
            lblAdjustMsg.setText("Stok " + product.getName() + " berhasil disesuaikan menjadi " + newStock);
            txtAdjustNewStock.clear();
            txtAdjustReason.clear();
            loadProductCombos();
            loadMovements();
        } catch (Exception e) {
            lblAdjustMsg.setText("Error: " + e.getMessage());
        }
    }
}

