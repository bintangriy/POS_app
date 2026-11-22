package com.posapp.controller;

import com.posapp.model.Transaction;
import com.posapp.service.TransactionService;
import com.posapp.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CancelDialogController {

    @FXML private Label lblInvoice;
    @FXML private TextArea txtReason;
    @FXML private Label lblError;

    private final TransactionService transactionService = new TransactionService();
    private Transaction transaction;

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        if (transaction != null) {
            lblInvoice.setText(transaction.getInvoiceNumber());
        }
    }

    @FXML
    public void handleConfirmCancel() {
        String reason = txtReason.getText();
        if (reason == null || reason.trim().isEmpty()) {
            lblError.setText("Alasan pembatalan wajib diisi.");
            lblError.setVisible(true);
            return;
        }

        try {
            transactionService.cancelTransaction(
                transaction.getId(),
                UserSession.getInstance().getCurrentUser(),
                reason.trim()
            );
            closeWindow();
        } catch (Exception e) {
            lblError.setText(e.getMessage());
            lblError.setVisible(true);
        }
    }

    @FXML
    public void handleDismiss() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtReason.getScene().getWindow();
        stage.close();
    }
}

