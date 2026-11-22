package com.posapp.model;

import com.posapp.model.enums.TransactionStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model agregat transaksi penjualan.
 * Menerapkan prinsip Komposisi (Composition over Inheritance) terhadap TransactionItem.
 */
public class Transaction {
    private int id;
    private String invoiceNumber;
    private User cashier;
    private LocalDateTime transactionDate;
    private final List<TransactionItem> items; // Hubungan Komposisi Kuat
    private Payment payment;
    private double subtotal;
    private double discountAmount;
    private double taxAmount;
    private double grandTotal;
    private TransactionStatus status;
    private User cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    public Transaction(String invoiceNumber, User cashier) {
        this.invoiceNumber = invoiceNumber;
        this.cashier = cashier;
        this.transactionDate = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.status = TransactionStatus.PENDING;
        this.subtotal = 0;
        this.discountAmount = 0;
        this.taxAmount = 0;
        this.grandTotal = 0;
    }

    public void addItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Kuantitas belanja harus lebih dari 0.");
        }
        for (TransactionItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.addQuantity(quantity);
                recalculateTotals();
                return;
            }
        }
        items.add(new TransactionItem(product, quantity, product.getSellPrice(), 0));
        recalculateTotals();
    }

    public void removeItem(Product product) {
        items.removeIf(item -> item.getProduct().getId() == product.getId());
        recalculateTotals();
    }

    public void updateQuantity(Product product, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(product);
            return;
        }
        for (TransactionItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(newQuantity);
                recalculateTotals();
                return;
            }
        }
    }

    public void recalculateTotals() {
        double sum = 0;
        for (TransactionItem item : items) {
            sum += item.getLineTotal();
        }
        this.subtotal = sum;
        this.grandTotal = Math.max(0, this.subtotal - this.discountAmount + this.taxAmount);
    }

    public void completeTransaction() {
        if (this.payment == null) {
            throw new IllegalStateException("Transaksi belum memiliki metode pembayaran.");
        }
        this.status = TransactionStatus.COMPLETED;
    }

    public void cancel(User admin, String reason) {
        if (admin == null || !admin.canAccessAdminMenu()) {
            throw new IllegalArgumentException("Hanya Admin yang berhak membatalkan transaksi.");
        }
        if (this.status == TransactionStatus.CANCELLED) {
            throw new IllegalStateException("Transaksi sudah dibatalkan sebelumnya.");
        }
        this.status = TransactionStatus.CANCELLED;
        this.cancelledBy = admin;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public User getCashier() { return cashier; }
    public void setCashier(User cashier) { this.cashier = cashier; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public List<TransactionItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addRawItem(TransactionItem item) {
        this.items.add(item);
    }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        recalculateTotals();
    }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
        recalculateTotals();
    }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public User getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(User cancelledBy) { this.cancelledBy = cancelledBy; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}

