package com.posapp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("Polimorfisme CashPayment: Kalkulasi uang kembalian")
    void testCashPaymentSuccess() {
        CashPayment cash = new CashPayment(100000.0);
        boolean success = cash.processPayment(75000.0);

        assertTrue(success);
        assertEquals(25000.0, cash.getChange());
        assertTrue(cash.getPaymentSummary().contains("Kembali = Rp 25000"));
    }

    @Test
    @DisplayName("Polimorfisme CashPayment: Pembayaran gagal jika uang kurang")
    void testCashPaymentInsufficient() {
        CashPayment cash = new CashPayment(50000.0);
        assertThrows(IllegalArgumentException.class, () -> cash.processPayment(60000.0));
    }

    @Test
    @DisplayName("Polimorfisme QrisPayment: Nominal harus tepat sesuai tagihan")
    void testQrisPayment() {
        QrisPayment qris = new QrisPayment(50000.0, "REF-12345");
        assertTrue(qris.processPayment(50000.0));
        assertTrue(qris.isSettled());
        assertTrue(qris.getPaymentSummary().contains("LUNAS"));

        QrisPayment qrisWrong = new QrisPayment(40000.0, "REF-99999");
        assertThrows(IllegalArgumentException.class, () -> qrisWrong.processPayment(50000.0));
    }
}

