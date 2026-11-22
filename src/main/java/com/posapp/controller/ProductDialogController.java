package com.posapp.controller;

import com.posapp.model.Category;
import com.posapp.model.Product;
import com.posapp.service.ProductService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProductDialogController {

    @FXML private TextField txtBarcode;
    @FXML private TextField txtName;
    @FXML private ComboBox<Category> cmbCategory;
    @FXML private TextField txtBuyPrice;
    @FXML private TextField txtSellPrice;
    @FXML private TextField txtStock;
    @FXML private TextField txtMinStock;
    @FXML private Label lblError;

    private final ProductService productService = new ProductService();
    private Product existingProduct;

    @FXML
    public void initialize() {
        cmbCategory.setItems(FXCollections.observableArrayList(productService.getAllCategories()));
        if (!cmbCategory.getItems().isEmpty()) {
            cmbCategory.getSelectionModel().selectFirst();
        }
    }

    public void setProduct(Product product) {
        this.existingProduct = product;
        if (product != null) {
            txtBarcode.setText(product.getBarcode());
            txtName.setText(product.getName());
            txtBuyPrice.setText(String.format(java.util.Locale.US, "%.0f", product.getBuyPrice()));
            txtSellPrice.setText(String.format(java.util.Locale.US, "%.0f", product.getSellPrice()));
            txtStock.setText(String.valueOf(product.getStock()));
            txtMinStock.setText(String.valueOf(product.getMinStockAlert()));

            if (product.getCategory() != null) {
                for (Category c : cmbCategory.getItems()) {
                    if (c.getId() == product.getCategory().getId()) {
                        cmbCategory.getSelectionModel().select(c);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    public void handleSave() {
        try {
            String barcode = txtBarcode.getText().trim();
            String name = txtName.getText().trim();
            Category category = cmbCategory.getSelectionModel().getSelectedItem();
            double buyPrice = Double.parseDouble(txtBuyPrice.getText().trim());
            double sellPrice = Double.parseDouble(txtSellPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            int minStock = Integer.parseInt(txtMinStock.getText().trim());

            if (existingProduct == null) {
                Product p = new Product(0, barcode, name, category, buyPrice, sellPrice, stock, minStock);
                productService.createProduct(p);
            } else {
                existingProduct.setBarcode(barcode);
                existingProduct.setName(name);
                existingProduct.setCategory(category);
                existingProduct.setBuyPrice(buyPrice);
                existingProduct.setSellPrice(sellPrice);
                existingProduct.setStock(stock);
                existingProduct.setMinStockAlert(minStock);
                productService.updateProduct(existingProduct);
            }

            closeWindow();
        } catch (Exception e) {
            lblError.setText(e.getMessage());
            lblError.setVisible(true);
        }
    }

    @FXML
    public void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtBarcode.getScene().getWindow();
        stage.close();
    }
}

