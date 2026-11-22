package com.posapp.model;

import com.posapp.model.enums.PaymentType;

/**
 * Implementasi pembayaran nontunai digital QRIS.
 */
public class QrisPayment extends Payment {
    private String transactionReference;
    private String qrContent;
    private boolean settled;

    public QrisPayment(double amountPaid, String transactionReference, String qrContent) {
        super(0, amountPaid, PaymentType.QRIS);
        this.transactionReference = transactionReference;
        this.qrContent = qrContent;
        this.settled = false;
    }

    public QrisPayment(double amountPaid, String transactionReference) {
        this(amountPaid, transactionReference, "");
    }

    @Override
    public boolean processPayment(double grandTotal) {
        if (Double.compare(getAmountPaid(), grandTotal) != 0) {
            throw new IllegalArgumentException(
                "Nominal QRIS harus sama persis dengan total tagihan. Tagihan: " + grandTotal + ", Nominal: " + getAmountPaid()
            );
        }
        this.settled = true;
        return true;
    }

    @Override
    public String getPaymentSummary() {
        return String.format("QRIS: Ref = %s | Status = %s", transactionReference, settled ? "LUNAS" : "PENDING");
    }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getQrContent() { return qrContent; }
    public void setQrContent(String qrContent) { this.qrContent = qrContent; }

    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
}

