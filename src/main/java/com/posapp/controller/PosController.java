package com.posapp.controller;

import com.posapp.model.*;
import com.posapp.service.PosService;
import com.posapp.service.ProductService;
import com.posapp.util.CurrencyFormatter;
import com.posapp.util.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PosController {

    @FXML private TextField txtSearch;
    @FXML private TableView<TransactionItem> tblCart;
    @FXML private TableColumn<TransactionItem, String> colBarcode;
    @FXML private TableColumn<TransactionItem, String> colName;
    @FXML private TableColumn<TransactionItem, String> colPrice;
    @FXML private TableColumn<TransactionItem, String> colQty;
    @FXML private TableColumn<TransactionItem, String> colSubtotal;

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

    @FXML
    public void initialize() {
        currentTransaction = posService.createNewTransaction(UserSession.getInstance().getCurrentUser());
        setupCartTable();
        setupPaymentFields();
        updateDisplay();
    }

    private void setupCartTable() {
        colBarcode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getBarcode()));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getName()));
        colPrice.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getUnitPrice())));
        colQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        colSubtotal.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getLineTotal())));

        tblCart.setItems(cartList);
    }

    private void setupPaymentFields() {
        txtAmountPaid.textProperty().addListener((obs, oldVal, newVal) -> calculateChange());
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
            try {
                posService.addProductToCart(currentTransaction, product, 1);
                refreshCart();
                txtSearch.clear();
                lblMessage.setText("");
            } catch (Exception e) {
                lblMessage.setText(e.getMessage());
            }
        } else {
            lblMessage.setText("Produk '" + keyword + "' tidak ditemukan.");
        }
    }

    @FXML
    public void handleIncreaseQty() {
        TransactionItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                posService.addProductToCart(currentTransaction, selected.getProduct(), 1);
                refreshCart();
            } catch (Exception e) {
                lblMessage.setText(e.getMessage());
            }
        }
    }

    @FXML
    public void handleDecreaseQty() {
        TransactionItem selected = tblCart.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int current = selected.getQuantity();
            if (current > 1) {
                currentTransaction.updateQuantity(selected.getProduct(), current - 1);
            } else {
                currentTransaction.removeItem(selected.getProduct());
            }
            refreshCart();
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
                // Reset keranjang baru
                currentTransaction = posService.createNewTransaction(UserSession.getInstance().getCurrentUser());
                refreshCart();
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

