package com.posapp.model;

import com.posapp.model.enums.PaymentType;
import java.time.LocalDateTime;

/**
 * Kelas abstrak dasar yang merepresentasikan metode pembayaran transaksi.
 */
public abstract class Payment {
    private int id;
    private double amountPaid;
    private LocalDateTime paymentTime;
    private PaymentType paymentType;

    public Payment(int id, double amountPaid, PaymentType paymentType) {
        this.id = id;
        this.amountPaid = amountPaid;
        this.paymentTime = LocalDateTime.now();
        this.paymentType = paymentType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    /**
     * Memproses pelunasan transaksi secara polimorfik.
     *
     * @param grandTotal total tagihan yang harus dibayar
     * @return true jika pembayaran berhasil
     */
    public abstract boolean processPayment(double grandTotal);

    /**
     * Mengembalikan ringkasan teks bukti pelunasan.
     */
    public abstract String getPaymentSummary();
}

