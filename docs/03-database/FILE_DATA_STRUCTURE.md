# File Data Structure & Entity Relationship - POS_app

Dokumen ini memetakan relasi logis antar-berkas data CSV pada aplikasi **POS_app**. Meskipun menggunakan berkas teks CSV tanpa engine database relasional, integritas relasi referensial ditegakkan melalui DAO dan Service Layer.

---

## 1. Diagram Relasi Berkas Data (Entity Relationship via CSV)

```mermaid
erDiagram
    USERS_CSV ||--o{ TRANSACTIONS_CSV : "processes / cancels"
    USERS_CSV ||--o{ STOCK_MOVEMENTS_CSV : "logs"
    
    CATEGORIES_CSV ||--o{ PRODUCTS_CSV : "classifies"
    
    PRODUCTS_CSV ||--o{ TRANSACTION_DETAILS_CSV : "item bought"
    PRODUCTS_CSV ||--o{ STOCK_MOVEMENTS_CSV : "stock audited"
    
    TRANSACTIONS_CSV ||--|{ TRANSACTION_DETAILS_CSV : "composed of"

    USERS_CSV {
        int id
        string username
        string password_hash
        string full_name
        string role
        string terminal_id
        boolean is_active
    }

    CATEGORIES_CSV {
        int id
        string name
        string description
    }

    PRODUCTS_CSV {
        int id
        string barcode
        string name
        int category_id
        decimal buy_price
        decimal sell_price
        int stock
        int min_stock_alert
    }

    TRANSACTIONS_CSV {
        int id
        string invoice_number
        int user_id
        datetime transaction_date
        decimal subtotal
        decimal discount_amount
        decimal tax_amount
        decimal grand_total
        string payment_type
        decimal amount_paid
        decimal change_amount
        string reference_number
        string status
        int cancelled_by
        datetime cancelled_at
        string cancellation_reason
    }

    TRANSACTION_DETAILS_CSV {
        int id
        int transaction_id
        int product_id
        int quantity
        decimal unit_price
        decimal buy_price_snapshot
        decimal discount_amount
        decimal line_total
    }

    STOCK_MOVEMENTS_CSV {
        int id
        int product_id
        int user_id
        string movement_type
        int quantity
        int stock_before
        int stock_after
        string remarks
        datetime created_at
    }
```

---

## 2. Integritas Relasi Referensial pada Lapisan Aplikasi

1. **Foreign Key Integrity:**
   - Ketika menambahkan produk baru pada `products.csv`, `ProductService` memverifikasi bahwa `category_id` benar-benar terdaftar di `categories.csv`.
   - Sebuah kategori tidak dapat dihapus jika masih ada produk yang mereferensikannya.
2. **Snapshotting:**
   - Kolom `unit_price` dan `buy_price_snapshot` pada `transaction_details.csv` menyimpan salinan permanen harga pada saat transaksi terjadi.
3. **Pemberian ID Unik (Auto-Increment Manual):**
   - DAO bertanggung jawab membaca nilai ID maksimum yang ada pada file CSV (`max(id) + 1`) sebelum menambahkan baris baru, menjamin ID tidak pernah duplikat.

