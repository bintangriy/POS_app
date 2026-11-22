package com.posapp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Komposisi: Menambahkan produk ke keranjang dan kalkulasi grand total")
    void testAddToCart() {
        User cashier = new Cashier(1, "kasir1", "hash", "Kasir Satu", "POS-01");
        Transaction t = new Transaction("INV-001", cashier);

        Category cat = new Category(1, "Minuman", "");
        Product p1 = new Product(1, "111", "Aqua", cat, 2000, 3000, 50, 10);
        Product p2 = new Product(2, "222", "Teh Botol", cat, 3000, 4000, 50, 10);

        t.addItem(p1, 2); // 2 * 3000 = 6000
        t.addItem(p2, 3); // 3 * 4000 = 12000

        assertEquals(2, t.getItems().size());
        assertEquals(18000.0, t.getGrandTotal());
    }

    @Test
    @DisplayName("Komposisi: Scan produk yang sama otomatis menambah kuantitas tanpa duplikasi baris")
    void testAddDuplicateProduct() {
        User cashier = new Cashier(1, "kasir1", "hash", "Kasir Satu", "POS-01");
        Transaction t = new Transaction("INV-001", cashier);

        Category cat = new Category(1, "Minuman", "");
        Product p1 = new Product(1, "111", "Aqua", cat, 2000, 3000, 50, 10);

        t.addItem(p1, 2);
        t.addItem(p1, 3); // total kuantitas harus 5

        assertEquals(1, t.getItems().size());
        assertEquals(5, t.getItems().get(0).getQuantity());
        assertEquals(15000.0, t.getGrandTotal());
    }

    @Test
    @DisplayName("Penerapan diskon nota mengurangi grand total")
    void testDiscount() {
        User cashier = new Cashier(1, "kasir1", "hash", "Kasir Satu", "POS-01");
        Transaction t = new Transaction("INV-001", cashier);

        Category cat = new Category(1, "Makanan", "");
        Product p1 = new Product(1, "111", "Biskuit", cat, 8000, 10000, 20, 5);

        t.addItem(p1, 2); // 20.000
        t.setDiscountAmount(2000.0);

        assertEquals(18000.0, t.getGrandTotal());
    }
}

