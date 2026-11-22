package com.posapp.controller;

import com.posapp.model.CashPayment;
import com.posapp.model.Payment;
import com.posapp.model.Transaction;
import com.posapp.model.TransactionItem;
import com.posapp.util.CurrencyFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class ReceiptDialogController {

    @FXML private TextArea txtReceipt;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void setTransaction(Transaction t) {
        if (t == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("               POS_app RETAIL           \n");
        sb.append("       Solusi Kasir & Inventaris UMKM   \n");
        sb.append("========================================\n");
        sb.append("No. Nota : ").append(t.getInvoiceNumber()).append("\n");
        sb.append("Tanggal  : ").append(t.getTransactionDate().format(FMT)).append("\n");
        sb.append("Kasir    : ").append(t.getCashier() != null ? t.getCashier().getFullName() : "Umum").append("\n");
        sb.append("Status   : ").append(t.getStatus()).append("\n");
        sb.append("----------------------------------------\n");

        for (TransactionItem item : t.getItems()) {
            sb.append(String.format("%-20s %2d x %9s\n", 
                truncate(item.getProduct().getName(), 20),
                item.getQuantity(),
                CurrencyFormatter.formatRupiah(item.getUnitPrice())));
            sb.append(String.format("%38s\n", CurrencyFormatter.formatRupiah(item.getLineTotal())));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal     : %25s\n", CurrencyFormatter.formatRupiah(t.getSubtotal())));
        if (t.getDiscountAmount() > 0) {
            sb.append(String.format("Diskon       : %25s\n", CurrencyFormatter.formatRupiah(t.getDiscountAmount())));
        }
        sb.append(String.format("GRAND TOTAL  : %25s\n", CurrencyFormatter.formatRupiah(t.getGrandTotal())));

        Payment p = t.getPayment();
        if (p != null) {
            sb.append("----------------------------------------\n");
            sb.append("Metode Bayar : ").append(p.getPaymentType()).append("\n");
            sb.append(String.format("Dibayar      : %25s\n", CurrencyFormatter.formatRupiah(p.getAmountPaid())));
            if (p instanceof CashPayment cp) {
                sb.append(String.format("Kembalian    : %25s\n", CurrencyFormatter.formatRupiah(cp.getChange())));
            }
        }

        sb.append("========================================\n");
        sb.append("      Terima Kasih Atas Kunjungan Anda   \n");
        sb.append("========================================\n");

        txtReceipt.setText(sb.toString());
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) txtReceipt.getScene().getWindow();
        stage.close();
    }
}

