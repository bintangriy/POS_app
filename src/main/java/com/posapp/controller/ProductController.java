package com.posapp.controller;

import com.posapp.model.Category;
import com.posapp.model.Product;
import com.posapp.service.ProductService;
import com.posapp.util.CurrencyFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<Category> cmbCategory;
    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, String> colBarcode;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, String> colBuyPrice;
    @FXML private TableColumn<Product, String> colSellPrice;
    @FXML private TableColumn<Product, String> colStock;
    @FXML private TableColumn<Product, String> colMinStock;
    @FXML private Label lblMessage;

    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        setupTable();
        loadCategories();
        loadProducts();
    }

    private void setupTable() {
        colBarcode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBarcode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCategory() != null ? c.getValue().getCategory().getName() : "-"
        ));
        colBuyPrice.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getBuyPrice())));
        colSellPrice.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getSellPrice())));
        colStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        colMinStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMinStockAlert())));
    }

    private void loadCategories() {
        var cats = productService.getAllCategories();
        Category all = new Category(0, "-- Semua Kategori --", "");
        var list = FXCollections.observableArrayList(all);
        list.addAll(cats);
        cmbCategory.setItems(list);
        cmbCategory.getSelectionModel().selectFirst();
    }

    private void loadProducts() {
        tblProducts.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
    }

    @FXML
    public void handleSearch() {
        String keyword = txtSearch.getText();
        tblProducts.setItems(FXCollections.observableArrayList(productService.searchProducts(keyword)));
    }

    @FXML
    public void handleCategoryFilter() {
        Category selected = cmbCategory.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            loadProducts();
        } else {
            tblProducts.setItems(FXCollections.observableArrayList(productService.getProductsByCategory(selected.getId())));
        }
    }

    @FXML
    public void handleAddProduct() {
        showProductDialog(null);
    }

    @FXML
    public void handleEditProduct() {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showProductDialog(selected);
        } else {
            lblMessage.setText("Pilih produk yang ingin diedit.");
        }
    }

    @FXML
    public void handleDeleteProduct() {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hapus produk '" + selected.getName() + "'?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();
            if (confirm.getResult() == ButtonType.YES) {
                productService.deleteProduct(selected.getId());
                loadProducts();
                lblMessage.setText("Produk berhasil dihapus.");
            }
        }
    }

    private void showProductDialog(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/product_dialog.fxml"));
            Parent root = loader.load();
            ProductDialogController ctrl = loader.getController();
            ctrl.setProduct(product);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle(product == null ? "Tambah Produk Baru" : "Edit Produk");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            loadProducts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

