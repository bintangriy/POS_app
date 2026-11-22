# Project Structure Guide - POS_app

Dokumen ini menjelaskan struktur folder, konvensi paket Java, berkas konfigurasi, dan direktori data CSV pada aplikasi **POS_app**.

---

## 1. Pohon Direktori Lengkap

```
POS_app/
├── pom.xml                                   # Konfigurasi Maven dependencies & plugins (No MySQL/JDBC)
├── README.md                                 # Panduan umum proyek & quick start
├── AGENTS.md                                 # Instruksi permanen AI Coding Agent & quality gates
│
├── config/
│   ├── app.properties.example                # Template konfigurasi Java I/O
│   └── app.properties                        # File konfigurasi aktif lokal
│
├── data/                                     # Direktori penyimpanan data berbasis Java I/O (CSV)
│   ├── users.csv                             # Data akun kasir dan admin
│   ├── categories.csv                        # Data kategori barang
│   ├── products.csv                          # Data master produk, harga, dan stok
│   ├── transactions.csv                      # Data header nota transaksi
│   ├── transaction_details.csv               # Data rincian belanja per transaksi (snapshot harga)
│   └── stock_movements.csv                   # Data audit log mutasi persediaan barang
│
├── docs/                                     # Dokumentasi rekayasa perangkat lunak
│   ├── 01-product/                           # PRD, User Stories, Scope
│   ├── 02-design/                            # Arsitektur, OOP Design, UI/UX Guidelines, UI Screen Spec
│   ├── 03-database/                          # Data Storage Spec, File Data Structure, Business Rules
│   ├── 04-development/                       # Project Structure, Plan, Coding Standards, Dependency Guide
│   ├── 05-quality/                           # Testing Strategy, Test Cases, Acceptance Criteria
│   └── 06-maintenance/                       # Setup, Troubleshooting, Changelog
│
└── src/
    ├── main/
    │   ├── java/com/posapp/
    │   │   ├── app/                          # MainApp dan AppLauncher
    │   │   ├── model/                        # Entitas Domain & Enums
    │   │   ├── dao/                          # Antarmuka DAO (UserDao, ProductDao, dll.)
    │   │   │   └── csv/                      # Implementasi DAO berbasis CSV (UserDaoCsv, dll.)
    │   │   ├── service/                      # Business Logic & Multi-File Consistency
    │   │   ├── controller/                   # JavaFX Controllers
    │   │   └── util/                         # CsvFileManager, CsvParser, CsvFormatter, AppConfig
    │   │
    │   └── resources/com/posapp/
    │       ├── views/                        # File FXML tampilan
    │       └── styles/                       # CSS tema Slate/Blue
    │
    └── test/java/com/posapp/                 # Pengujian Unit JUnit 5
        ├── model/                            # Test logika domain (Product, Transaction, Payment)
        ├── dao/                              # Test CRUD CSV via @TempDir
        ├── service/                          # Test PosService, TransactionService, AuthService, ReportService
        └── util/                             # Test CsvParser, CsvFormatter, AppConfig
```
