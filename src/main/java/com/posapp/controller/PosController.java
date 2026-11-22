package com.posapp.controller;

import com.posapp.model.*;
import com.posapp.service.PosService;
import com.posapp.service.ProductService;
import com.posapp.util.CurrencyFormatter;
import com.posapp.util.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PosController {

    // Katalog Produk Cepat
    @FXML private TextField txtCatalogSearch;
    @FXML private TableView<Product> tblCatalog;
    @FXML private TableColumn<Product, String> colCatalogName;
    @FXML private TableColumn<Product, String> colCatalogPrice;
    @FXML private TableColumn<Product, String> colCatalogStock;
    @FXML private TableColumn<Product, Void> colCatalogAction;

    // Scan Barcode & Keranjang Belanja
    @FXML private TextField txtSearch;
    @FXML private TableView<TransactionItem> tblCart;
    @FXML private TableColumn<TransactionItem, String> colBarcode;
    @FXML private TableColumn<TransactionItem, String> colName;
    @FXML private TableColumn<TransactionItem, String> colPrice;
    @FXML private TableColumn<TransactionItem, String> colQty;
    @FXML private TableColumn<TransactionItem, String> colSubtotal;
    @FXML private TableColumn<TransactionItem, Void> colCartAction;

    // Panel Pembayaran
    @FXML private Label lblGrandTotal;
    @FXML private RadioButton rbCash;
    @FXML private RadioButton rbQris;
    @FXML private ToggleGroup paymentToggleGroup;
    @FXML private TextField txtAmountPaid;
    @FXML private Label lblChange;
    @FXML private Label lblMessage;

    private final PosService posService = new PosService();
    private final ProductService productService = new ProductService();
    private Transaction currentTransaction;
    private final ObservableList<TransactionItem> cartList = FXCollections.observableArrayList();
    private final ObservableList<Product> catalogList = FXCollections.observableArrayList();
    private FilteredList<Product> filteredCatalog;

    @FXML
    public void initialize() {
        currentTransaction = posService.createNewTransaction(UserSession.getInstance().getCurrentUser());
        setupCatalogTable();
        setupCartTable();
        setupPaymentFields();
        loadCatalog();
        updateDisplay();
    }

    private void setupCatalogTable() {
        colCatalogName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colCatalogPrice.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getSellPrice())));
        colCatalogStock.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getStock())));

        // Tombol Tambah (+) langsung di baris katalog
        colCatalogAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnAdd = new Button("➕");

            {
                btnAdd.getStyleClass().add("btn-primary");
                btnAdd.setStyle("-fx-padding: 3 8; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnAdd.setTooltip(new Tooltip("Tambahkan ke Keranjang"));
                btnAdd.setOnAction(e -> {
                    Product prod = getTableView().getItems().get(getIndex());
                    if (prod != null) {
                        addProduct(prod, 1);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(btnAdd);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Double click baris produk juga langsung menambahkan ke keranjang
        tblCatalog.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    addProduct(row.getItem(), 1);
                }
            });
            return row;
        });

        // Live filter pencarian katalog
        filteredCatalog = new FilteredList<>(catalogList, p -> true);
        txtCatalogSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredCatalog.setPredicate(p -> {
                if (newVal == null || newVal.trim().isEmpty()) return true;
                String lower = newVal.toLowerCase().trim();
                return p.getName().toLowerCase().contains(lower)
                    || p.getBarcode().toLowerCase().contains(lower)
                    || (p.getCategory() != null && p.getCategory().getName().toLowerCase().contains(lower));
            });
        });

        tblCatalog.setItems(filteredCatalog);
    }

    private void setupCartTable() {
        colBarcode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getBarcode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getName()));
        colPrice.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getUnitPrice())));
        colQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getLineTotal())));

        // Tombol inline (+) dan (-) di kolom aksi keranjang
        colCartAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnPlus = new Button("+");
            private final Button btnMinus = new Button("-");
            private final HBox actionBox = new HBox(4, btnMinus, btnPlus);

            {
                actionBox.setAlignment(Pos.CENTER);
                btnPlus.getStyleClass().add("btn-secondary");
                btnPlus.setStyle("-fx-padding: 2 6; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnPlus.setTooltip(new Tooltip("Tambah 1"));

                btnMinus.getStyleClass().add("btn-secondary");
                btnMinus.setStyle("-fx-padding: 2 6; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnMinus.setTooltip(new Tooltip("Kurang 1"));

                btnPlus.setOnAction(e -> {
                    TransactionItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        addProduct(item.getProduct(), 1);
                    }
                });

                btnMinus.setOnAction(e -> {
                    TransactionItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        decreaseItem(item);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tblCart.setItems(cartList);
    }

    private void setupPaymentFields() {
        txtAmountPaid.textProperty().addListener((obs, oldVal, newVal) -> calculateChange());
    }

    private void loadCatalog() {
        catalogList.setAll(productService.getAllProducts());
    }

    private void addProduct(Product product, int qty) {
        if (product == null) return;
        try {
            posService.addProductToCart(currentTransaction, product, qty);
            refreshCart();
            lblMessage.setText("");
        } catch (Exception e) {
            lblMessage.setText(e.getMessage());
        }
    }

    private void decreaseItem(TransactionItem item) {
        if (item == null) return;
        int current = item.getQuantity();
        if (current > 1) {
            currentTransaction.updateQuantity(item.getProduct(), current - 1);
        } else {
            currentTransaction.removeItem(item.getProduct());
        }
        refreshCart();
    }

    @FXML
    public void handleSearchProduct() {
        String keyword = txtSearch.getText();
        if (keyword == null || keyword.trim().isEmpty()) return;

        Product product = productService.getProductByBarcode(keyword.trim());
        if (product == null) {
            var list = productService.searchProducts(keyword.trim());
            if (!list.isEmpty()) {
                product = list.get(0);
            }
        }

        if (product != null) {
            addProduct(product, 1);
            txtSearch.clear();
        } else {
            lblMessage.setText("Produk '" + keyword + "' tidak ditemukan.");
        }
    }

    @FXML
    public void handleIncreaseQty() {
        TransactionItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            addProduct(selected.getProduct(), 1);
        }
    }

    @FXML
    public void handleDecreaseQty() {
        TransactionItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            decreaseItem(selected);
        }
    }

    @FXML
    public void handleRemoveItem() {
        TransactionItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentTransaction.removeItem(selected.getProduct());
            refreshCart();
        }
    }

    @FXML
    public void handleClearCart() {
        currentTransaction = posService.createNewTransaction(UserSession.getInstance().getCurrentUser());
        refreshCart();
        lblMessage.setText("");
    }

    @FXML
    public void handleQuickCash(javafx.event.ActionEvent event) {
        if (event.getSource() instanceof Button btn) {
            String text = btn.getText();
            if ("Uang Pas".equalsIgnoreCase(text)) {
                txtAmountPaid.setText(String.format(java.util.Locale.US, "%.0f", currentTransaction.getGrandTotal()));
            } else {
                double val = CurrencyFormatter.parseRupiah(text);
                if (val > 0) {
                    txtAmountPaid.setText(String.format(java.util.Locale.US, "%.0f", val));
                }
            }
        }
    }

    private void calculateChange() {
        try {
            double paid = Double.parseDouble(txtAmountPaid.getText().trim());
            double change = paid - currentTransaction.getGrandTotal();
            if (change >= 0) {
                lblChange.setText(CurrencyFormatter.formatRupiah(change));
            } else {
                lblChange.setText("Kurang: " + CurrencyFormatter.formatRupiah(Math.abs(change)));
            }
        } catch (NumberFormatException e) {
            lblChange.setText("Rp 0");
        }
    }

    @FXML
    public void handleProcessCheckout() {
        if (currentTransaction.getItems().isEmpty()) {
            lblMessage.setText("Keranjang belanja masih kosong.");
            return;
        }

        try {
            Payment payment;
            if (rbCash.isSelected()) {
                double paid = Double.parseDouble(txtAmountPaid.getText().trim());
                payment = new CashPayment(paid);
            } else {
                payment = new QrisPayment(currentTransaction.getGrandTotal(), "QRIS-" + System.currentTimeMillis());
            }

            boolean success = posService.processCheckout(currentTransaction, payment);
            if (success) {
                showReceiptModal(currentTransaction);
                // Reset keranjang & reload stok katalog
                currentTransaction = posService.createNewTransaction(UserSession.getInstance().getCurrentUser());
                refreshCart();
                loadCatalog();
                txtAmountPaid.clear();
                lblChange.setText("Rp 0");
                lblMessage.setText("Transaksi berhasil disimpan!");
            }
        } catch (Exception e) {
            lblMessage.setText("Gagal bayar: " + e.getMessage());
        }
    }

    private void showReceiptModal(Transaction transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/receipt_dialog.fxml"));
            Parent root = loader.load();
            ReceiptDialogController ctrl = loader.getController();
            ctrl.setTransaction(transaction);

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Struk Nota Penjualan - " + transaction.getInvoiceNumber());
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshCart() {
        cartList.setAll(currentTransaction.getItems());
        updateDisplay();
    }

    private void updateDisplay() {
        lblGrandTotal.setText(CurrencyFormatter.formatRupiah(currentTransaction.getGrandTotal()));
        calculateChange();
    }
}
