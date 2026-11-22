# Entity Relationship Diagram (ERD) - POS_app

Dokumen ini memetakan relasi antar-entitas data dalam basis data **pos_app_db** menggunakan diagram Mermaid ERD beserta penjelasan kardinalitas dan integritas referensial.

---

## 1. Diagram ERD (Mermaid)

```mermaid
erDiagram
    USERS ||--o{ TRANSACTIONS : "processes (as cashier)"
    USERS ||--o{ TRANSACTIONS : "cancels (as admin)"
    USERS ||--o{ STOCK_MOVEMENTS : "logs"
    
    CATEGORIES ||--o{ PRODUCTS : "contains"
    
    PRODUCTS ||--o{ TRANSACTION_DETAILS : "referenced in"
    PRODUCTS ||--o{ STOCK_MOVEMENTS : "audited by"
    
    TRANSACTIONS ||--|{ TRANSACTION_DETAILS : "composed of"

    USERS {
        int id PK
        string username UK
        string password_hash
        string full_name
        string role "ADMIN / CASHIER"
        string terminal_id
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    CATEGORIES {
        int id PK
        string name UK
        string description
        timestamp created_at
    }

    PRODUCTS {
        int id PK
        string barcode UK
        string name
        int category_id FK
        decimal buy_price
        decimal sell_price
        int stock
        int min_stock_alert
        timestamp created_at
        timestamp updated_at
    }

    TRANSACTIONS {
        int id PK
        string invoice_number UK
        int user_id FK
        datetime transaction_date
        decimal subtotal
        decimal discount_amount
        decimal tax_amount
        decimal grand_total
        string payment_type "CASH / QRIS"
        decimal amount_paid
        decimal change_amount
        string reference_number
        string status "PENDING / COMPLETED / CANCELLED"
        int cancelled_by FK
        datetime cancelled_at
        string cancellation_reason
        timestamp created_at
    }

    TRANSACTION_DETAILS {
        int id PK
        int transaction_id FK
        int product_id FK
        int quantity
        decimal unit_price
        decimal buy_price_snapshot
        decimal discount_amount
        decimal line_total
    }

    STOCK_MOVEMENTS {
        int id PK
        int product_id FK
        int user_id FK
        string movement_type "STOCK_IN / ADJUSTMENT / SALE / CANCELLATION_RETURN"
        int quantity
        int stock_before
        int stock_after
        string remarks
        timestamp created_at
    }
```

---

## 2. Penjelasan Kardinalitas & Integritas Referensial

1. **`CATEGORIES` ke `PRODUCTS` (1 to Many):**
   - Satu kategori dapat memuat banyak produk ($1 : N$).
   - Aturan Hapus: `ON DELETE RESTRICT` (Kategori tidak boleh dihapus jika masih terdapat produk yang menggunakannya).

2. **`PRODUCTS` ke `TRANSACTION_DETAILS` (1 to Many):**
   - Satu produk dapat dibeli dalam banyak transaksi ($1 : N$).
   - Aturan Hapus: `ON DELETE RESTRICT` (Produk yang sudah memiliki riwayat penjualan tidak boleh dihapus demi menjaga integritas data akuntansi).

3. **`TRANSACTIONS` ke `TRANSACTION_DETAILS` (1 to Many - Komposisi Kuat):**
   - Satu header transaksi wajib memiliki 1 atau lebih rincian belanja ($1 : N$).
   - Aturan Hapus: `ON DELETE CASCADE` (Jika transaksi dibatalkan/dibersihkan secara permanen, detail item transaksi otomatis terhapus).

4. **`USERS` ke `TRANSACTIONS` (1 to Many):**
   - Satu pengguna kasir dapat melayani banyak transaksi ($1 : N$).
   - Kolom `cancelled_by` secara terpisah mereferensikan pengguna Admin yang melakukan pembatalan nota.

5. **`PRODUCTS` & `USERS` ke `STOCK_MOVEMENTS` (1 to Many):**
   - Setiap mutasi stok barang terhubung dengan produk yang bersangkutan dan staf yang bertanggung jawab.

