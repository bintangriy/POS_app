# Project Structure Guide - POS_app

Dokumen ini menjelaskan struktur folder, konvensi paket Java, dan organisasi sumber daya (*resources*) pada aplikasi **POS_app**.

---

## 1. Pohon Direktori Lengkap

```
POS_app/
├── pom.xml                                   # Konfigurasi Maven dependencies & plugins
├── README.md                                 # Panduan umum proyek & quick start
├── AGENTS.md                                 # Instruksi permanen AI Coding Agent & quality gates
│
├── config/
│   └── app.properties.example                # Template konfigurasi Java I/O (Database credentials)
│
├── database/
│   ├── schema.sql                            # DDL skema MySQL pos_app_db
│   └── seed.sql                              # Data awal (Users, Categories, Products, Stocks)
│
├── docs/                                     # Dokumentasi rekayasa perangkat lunak
│   ├── 01-product/                           # PRD, User Stories, Scope
│   ├── 02-design/                            # Arsitektur, OOP Design, UI/UX Guidelines, UI Screen Spec
│   ├── 03-database/                          # Database Dict, ERD, Business Rules
│   ├── 04-development/                       # Project Structure, Plan, Coding Standards, Dependency Guide
│   ├── 05-quality/                           # Testing Strategy, Test Cases, Acceptance Criteria
│   └── 06-maintenance/                       # Setup, Troubleshooting, Changelog
│
└── src/
    ├── main/
    │   ├── java/com/posapp/
    │   │   ├── app/                          # Entry point aplikasi JavaFX
    │   │   │   ├── MainApp.java              # JavaFX Application class
    │   │   │   └── AppLauncher.java          # Wrapper main method tanpa extends Application
    │   │   │
    │   │   ├── model/                        # Entitas Domain & Value Objects (Encapsulation)
    │   │   │   ├── User.java                 # Abstract user
    │   │   │   ├── Admin.java                # Concrete admin user
    │   │   │   ├── Cashier.java              # Concrete cashier user
    │   │   │   ├── Category.java             # Product category
    │   │   │   ├── Product.java              # Master product entity
    │   │   │   ├── Transaction.java          # Transaction aggregate root
    │   │   │   ├── TransactionItem.java      # Transaction detail item (Composition)
    │   │   │   ├── Payment.java              # Abstract payment
    │   │   │   ├── CashPayment.java          # Cash payment logic
    │   │   │   ├── QrisPayment.java          # QRIS payment logic
    │   │   │   ├── StockMovement.java        # Inventory mutation entity
    │   │   │   └── enums/                    # Enumerasi bertipe kuat
    │   │   │       ├── Role.java             # ADMIN, CASHIER
    │   │   │       ├── PaymentType.java      # CASH, QRIS
    │   │   │       ├── TransactionStatus.java# PENDING, COMPLETED, CANCELLED
    │   │   │       └── MovementType.java     # STOCK_IN, ADJUSTMENT, SALE, CANCELLATION_RETURN
    │   │   │
    │   │   ├── dao/                          # Kontrak antarmuka data access (Abstraction)
    │   │   │   ├── UserDao.java
    │   │   │   ├── CategoryDao.java
    │   │   │   ├── ProductDao.java
    │   │   │   ├── TransactionDao.java
    │   │   │   ├── InventoryDao.java
    │   │   │   └── jdbc/                     # Implementasi konkret JDBC
    │   │   │       ├── UserDaoJdbc.java
    │   │   │       ├── CategoryDaoJdbc.java
    │   │   │       ├── ProductDaoJdbc.java
    │   │   │       ├── TransactionDaoJdbc.java
    │   │   │       └── InventoryDaoJdbc.java
    │   │   │
    │   │   ├── service/                      # Logika bisnis & orkestrasi transaksi
    │   │   │   ├── AuthService.java          # Autentikasi & verifikasi hash BCrypt
    │   │   │   ├── DashboardService.java     # Metrik dashboard
    │   │   │   ├── ProductService.java       # Validasi & CRUD produk
    │   │   │   ├── PosService.java           # Keranjang kasir, checkout atomik, kalkulasi
    │   │   │   ├── InventoryService.java     # Restock, opname, deteksi low stock
    │   │   │   ├── TransactionService.java   # Riwayat & pembatalan nota oleh admin
    │   │   │   └── ReportService.java        # Agregator laporan
    │   │   │
    │   │   ├── controller/                   # JavaFX Controllers (View Glue)
    │   │   │   ├── LoginController.java
    │   │   │   ├── MainDashboardController.java
    │   │   │   ├── DashboardController.java
    │   │   │   ├── PosController.java
    │   │   │   ├── ProductController.java
    │   │   │   ├── ProductDialogController.java
    │   │   │   ├── InventoryController.java
    │   │   │   ├── TransactionHistoryController.java
    │   │   │   ├── CancelDialogController.java
    │   │   │   ├── ReportController.java
    │   │   │   └── ReceiptDialogController.java
    │   │   │
    │   │   └── util/                         # Kelas pendukung
    │   │       ├── AppConfig.java            # Java I/O: Membaca config/app.properties via FileInputStream
    │   │       ├── DatabaseConnection.java   # Manajemen koneksi JDBC
    │   │       ├── PasswordSecurity.java     # jBCrypt hashing helper
    │   │       ├── CurrencyFormatter.java    # Format angka ke Rupiah
    │   │       └── UserSession.java          # Session management kasir/admin aktif
    │   │
    │   └── resources/com/posapp/
    │       ├── views/                        # File tampilan JavaFX FXML
    │       │   ├── login.fxml
    │       │   ├── main_layout.fxml
    │       │   ├── dashboard.fxml
    │       │   ├── pos.fxml
    │       │   ├── products.fxml
    │       │   ├── product_dialog.fxml
    │       │   ├── inventory.fxml
    │       │   ├── transaction_history.fxml
    │       │   ├── cancel_dialog.fxml
    │       │   ├── reports.fxml
    │       │   └── receipt_dialog.fxml
    │       │
    │       ├── styles/                       # Stylesheets CSS
    │       │   ├── main.css                  # Variabel warna & gaya komponen global
    │       │   ├── pos.css                   # Gaya khusus kasir (tombol besar & kontras)
    │       │   └── dashboard.css             # Gaya kartu metrik
    │       │
    │       └── images/                       # Ikon & logo antarmuka
    │
    └── test/java/com/posapp/                 # Pengujian Unit (JUnit 5)
        ├── model/                            # Test logika domain (Product, Transaction, Payment)
        ├── service/                          # Test logika bisnis (PosService, AuthService, InventoryService)
        └── util/                             # Test Java I/O (AppConfig, CurrencyFormatter)
```

---

## 2. Pemisahan Tanggung Jawab Antar-Paket

- **`com.posapp.model`:** Hanya berisi struktur data, enkapsulasi field, dan metode domain bisnis internal. Tidak boleh mengimpor kelas dari layer controller atau DAO.
- **`com.posapp.dao`:** Hanya bertugas membaca dan menulis ke database via SQL. Bebas dari logika tampilan JavaFX.
- **`com.posapp.service`:** Bertindak sebagai jembatan antara Controller dan DAO. Menangani transaksi atomik JDBC dan validasi tingkat tinggi.
- **`com.posapp.controller`:** Mengendalikan event pengguna. Tidak boleh mengeksekusi query database SQL langsung.
- **`com.posapp.util`:** Menyediakan fungsi pembacaan Java I/O (`AppConfig`), koneksi database (`DatabaseConnection`), dan helper keamanan.

