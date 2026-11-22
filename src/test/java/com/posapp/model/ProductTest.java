package com.posapp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("Invarian: Harga jual tidak boleh lebih rendah dari harga beli")
    void testInvalidPrices() {
        Category cat = new Category(1, "Makanan", "");
        assertThrows(IllegalArgumentException.class, () -> 
            new Product(1, "123", "Produk Rugi", cat, 10000, 8000, 10, 2)
        );
    }

    @Test
    @DisplayName("Invarian: Pengurangan stok melebihi saldo fisik melempar IllegalStateException (mencegah stok negatif)")
    void testReduceStockOverflow() {
        Category cat = new Category(1, "Makanan", "");
        Product p = new Product(1, "123", "Indomie", cat, 2500, 3000, 5, 2);

        assertThrows(IllegalStateException.class, () -> p.reduceStock(6));
        assertEquals(5, p.getStock()); // stok tetap aman
    }

    @Test
    @DisplayName("Pengurangan stok berhasil jika kuantitas mencukupi")
    void testReduceStockSuccess() {
        Category cat = new Category(1, "Makanan", "");
        Product p = new Product(1, "123", "Indomie", cat, 2500, 3000, 10, 2);

        p.reduceStock(4);
        assertEquals(6, p.getStock());
    }

    @Test
    @DisplayName("Deteksi stok menipis (isLowStock)")
    void testLowStockAlert() {
        Category cat = new Category(1, "Makanan", "");
        Product p = new Product(1, "123", "Indomie", cat, 2500, 3000, 3, 5);

        assertTrue(p.isLowStock());
        p.addStock(10);
        assertFalse(p.isLowStock());
    }

    @Test
    @DisplayName("Kalkulasi margin keuntungan kotor per unit")
    void testProfitMargin() {
        Category cat = new Category(1, "Makanan", "");
        Product p = new Product(1, "123", "Indomie", cat, 2500, 3500, 10, 2);

        assertEquals(1000.0, p.calculateProfitMargin());
    }
}

