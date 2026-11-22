package com.posapp.controller;

import com.posapp.model.Transaction;
import com.posapp.model.TransactionItem;
import com.posapp.model.User;
import com.posapp.service.TransactionService;
import com.posapp.util.CurrencyFormatter;
import com.posapp.util.UserSession;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionHistoryController {

    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;
    @FXML private TextField txtSearchInvoice;

    @FXML private TableView<Transaction> tblTransactions;
    @FXML private TableColumn<Transaction, String> colInvoice;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colCashier;
    @FXML private TableColumn<Transaction, String> colPayment;
    @FXML private TableColumn<Transaction, String> colTotal;
    @FXML private TableColumn<Transaction, String> colStatus;

    @FXML private TableView<TransactionItem> tblDetails;
    @FXML private TableColumn<TransactionItem, String> colItemProduct;
    @FXML private TableColumn<TransactionItem, String> colItemPrice;
    @FXML private TableColumn<TransactionItem, String> colItemQty;
    @FXML private TableColumn<TransactionItem, String> colItemTotal;

    @FXML private Button btnCancelTransaction;
    @FXML private Label lblMessage;

    private final TransactionService transactionService = new TransactionService();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        setupTables();
        loadTransactions();

        // Otorisasi tombol pembatalan nota khusus Admin
        User user = UserSession.getInstance().getCurrentUser();
        boolean isAdmin = user != null && user.canAccessAdminMenu();
        btnCancelTransaction.setVisible(isAdmin);
        btnCancelTransaction.setManaged(isAdmin);

        tblTransactions.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                tblDetails.setItems(FXCollections.observableArrayList(selected.getItems()));
            } else {
                tblDetails.getItems().clear();
            }
        });
    }

    private void setupTables() {
        colInvoice.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getInvoiceNumber()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTransactionDate().format(FMT)));
        colCashier.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getCashier() != null ? c.getValue().getCashier().getFullName() : "-"
        ));
        colPayment.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getPayment() != null ? c.getValue().getPayment().getPaymentType().name() : "-"
        ));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getGrandTotal())));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));

        colItemProduct.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getProduct() != null ? c.getValue().getProduct().getName() : "-"
        ));
        colItemPrice.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getUnitPrice())));
        colItemQty.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        colItemTotal.setCellValueFactory(c -> new SimpleStringProperty(CurrencyFormatter.formatRupiah(c.getValue().getLineTotal())));
    }

    private void loadTransactions() {
        tblTransactions.setItems(FXCollections.observableArrayList(transactionService.getAllTransactions()));
    }

    @FXML
    public void handleFilter() {
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();
        String invoice = txtSearchInvoice.getText();

        if (invoice != null && !invoice.trim().isEmpty()) {
            Transaction t = transactionService.getTransactionByInvoice(invoice.trim());
            tblTransactions.setItems(t != null ? FXCollections.observableArrayList(t) : FXCollections.emptyObservableList());
        } else if (from != null || to != null) {
            tblTransactions.setItems(FXCollections.observableArrayList(transactionService.getTransactionsByDateRange(from, to)));
        } else {
            loadTransactions();
        }
    }

    @FXML
    public void handleReset() {
        dpFrom.setValue(null);
        dpTo.setValue(null);
        txtSearchInvoice.clear();
        loadTransactions();
    }

    @FXML
    public void handleReprintReceipt() {
        Transaction selected = tblTransactions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/receipt_dialog.fxml"));
                Parent root = loader.load();
                ReceiptDialogController ctrl = loader.getController();
                ctrl.setTransaction(selected);

                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Struk Nota - " + selected.getInvoiceNumber());
                modal.setScene(new Scene(root));
                modal.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblMessage.setText("Pilih transaksi untuk cetak struk.");
        }
    }

    @FXML
    public void handleCancelTransaction() {
        Transaction selected = tblTransactions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblMessage.setText("Pilih transaksi yang ingin dibatalkan.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/cancel_dialog.fxml"));
            Parent root = loader.load();
            CancelDialogController ctrl = loader.getController();
            ctrl.setTransaction(selected);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Batalkan Transaksi - " + selected.getInvoiceNumber());
            modal.setScene(new Scene(root));
            modal.showAndWait();

            loadTransactions();
            lblMessage.setText("Status transaksi diperbarui.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

