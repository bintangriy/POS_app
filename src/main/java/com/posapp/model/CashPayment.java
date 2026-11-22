package com.posapp.model;

import com.posapp.model.enums.PaymentType;

/**
 * Implementasi pembayaran tunai (Cash).
 * Memvalidasi kecukupan uang dan menghitung nilai kembalian.
 */
public class CashPayment extends Payment {
    private double change;

    public CashPayment(double amountPaid) {
        super(0, amountPaid, PaymentType.CASH);
        this.change = 0;
    }

    @Override
    public boolean processPayment(double grandTotal) {
        if (getAmountPaid() < grandTotal) {
            throw new IllegalArgumentException(
                "Uang tunai kurang. Tagihan: " + grandTotal + ", Dibayar: " + getAmountPaid()
            );
        }
        this.change = getAmountPaid() - grandTotal;
        return true;
    }

    @Override
    public String getPaymentSummary() {
        return String.format("Tunai: Dibayar = Rp %.0f | Kembali = Rp %.0f", getAmountPaid(), change);
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }
}

