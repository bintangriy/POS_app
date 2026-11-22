# POS_app - Point of Sale & Inventory Management

Aplikasi desktop kasir (*Point of Sale*) dan manajemen persediaan barang (*Inventory Management*) yang dikembangkan untuk UMKM ritel. Dibangun dengan bahasa murni **Java 21**, **JavaFX**, arsitektur **OOP murni**, dan database **MySQL** via **JDBC**.

Proyek ini merupakan pemenuhan tugas akhir mata kuliah **Java Object-Oriented Programming (OOP)**.

---

## 1. Fitur Utama Aplikasi

- **Authentication:**
  - Login dan logout berbasis enkripsi password BCrypt.
  - Role-Based Access Control (RBAC): Peran `Admin` (akses penuh manajerial) dan `Cashier` (operasional kasir).
- **Dashboard:**
  - Ringkasan total penjualan dan omset hari ini.
  - Jumlah transaksi yang berhasil diproses.
  - Total item katalog produk.
  - Daftar produk dengan stok menipis (*low stock alert*).
- **Product Management:**
  - CRUD produk lengkap dengan barcode unik, harga beli (HPP), harga jual, stok, dan batas minimum stok.
  - CRUD kategori barang.
  - Pencarian cepat produk berbasis barcode dan teks nama barang.
- **Point of Sale (Kasir):**
  - Scanning barcode dan pencarian produk otomatis.
  - Keranjang belanja dinamis (tambah, edit kuantitas, hapus item).
  - Perhitungan otomatis subtotal, diskon, dan grand total.
  - Input pembayaran (Tunai dengan hitung kembalian instan, atau QRIS dengan kode referensi).
  - Penyimpanan transaksi kasir secara atomik (*atomic transaction*) dan pemotongan stok otomatis.
- **Inventory Management:**
  - Pencatatan stok barang masuk (*Stock-in* / Restock).
  - Penyesuaian stok opname fisik (*Stock Adjustment*) dengan catatan alasan.
  - Audit trail lengkap riwayat mutasi persediaan barang (`stock_movements`).
  - Indikator peringatan dini stok menipis.
- **Transaction History:**
  - Riwayat lengkap transaksi dengan filter rentang tanggal dan pencarian faktur.
  - Detail rincian barang per transaksi.
  - Fitur **pembatalan transaksi oleh Admin** yang secara otomatis mengembalikan stok barang ke inventaris (*stock restoration*).
- **Reports:**
  - Laporan penjualan harian dan bulanan.
  - Laporan produk terlaris (*Top-selling products*).
  - Laporan persediaan stok barang.
  - Ringkasan estimasi keuntungan kotor (*Gross Profit = Penjualan - HPP*).

---

## 2. Penerapan Java I/O

Aplikasi menerapkan konsep **Java I/O** secara eksplisit dan terfokus pada:

> **Membaca Konfigurasi Aplikasi dan Koneksi Database dari File Eksternal (`config/app.properties`).**

Komponen Java I/O yang digunakan:
- `java.io.File`: Memvalidasi keberadaan fisik file konfigurasi di sistem file lokal.
- `java.io.FileInputStream`: Membuka stream pembacaan file konfigurasi secara native.
- `java.io.IOException`: Menangani kesalahan I/O (file tidak ditemukan atau format rusak).
- `java.util.Properties`: Memuat pasangan *key-value* konfigurasi database.

Dengan mekanisme ini, kredensial database **tidak pernah di-hardcode** di dalam kode sumber aplikasi Java, mematuhi prinsip keamanan perangkat lunak profesional.

---

## 3. Stack Teknologi & Prasyarat

- **Bahasa:** Java 21 (LTS)
- **GUI:** JavaFX 21 (FXML + CSS)
- **Build Tool:** Apache Maven
- **Database:** MySQL 8.x
- **Data Access:** JDBC (MySQL Connector/J) dengan `PreparedStatement`
- **Security:** jBCrypt
- **Testing:** JUnit 5 (Jupiter)

---

## 4. Panduan Instalasi & Menjalankan Aplikasi

### Langkah 1: Setup Database MySQL
Buka terminal dan eksekusi skrip DDL dan data awal (seed):
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

### Langkah 2: Setup File Konfigurasi (Java I/O)
Salin file template konfigurasi menjadi file aktif:
```bash
cp config/app.properties.example config/app.properties
```
Buka file `config/app.properties` dan sesuaikan user serta password MySQL lokal Anda:
```properties
db.host=localhost
db.port=3306
db.name=pos_app_db
db.user=root
db.password=password_mysql_anda
```

### Langkah 3: Build & Jalankan Aplikasi
```bash
# Kompilasi proyek
mvn clean compile

# Menjalankan aplikasi JavaFX
mvn javafx:run

# Menjalankan automated test suite
mvn test
```

---

## 5. Akun Pengguna Bawaan (Default Seed)

| Peran | Username | Password Default | Akses Menu |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Seluruh menu (Dashboard, Produk, Kasir, Stok, Riwayat, Laporan, Pembatalan) |
| **Kasir** | `kasir1` | `kasir123` | Dashboard Ringkas, Menu Kasir (POS), Riwayat Transaksi Shift |

---

## 6. Navigasi Dokumentasi Proyek

Dokumentasi lengkap proyek disusun secara terstruktur di dalam direktori [`docs/`](docs/):

- **01 - Product:**
  - [PRD.md](file:///home/bintang/JavaProject/POS_app/docs/01-product/PRD.md): Product Requirements Document.
  - [USER_STORIES.md](file:///home/bintang/JavaProject/POS_app/docs/01-product/USER_STORIES.md): User Stories lengkap dengan Acceptance Criteria.
  - [SCOPE.md](file:///home/bintang/JavaProject/POS_app/docs/01-product/SCOPE.md): Batasan in-scope, out-of-scope, dan MoSCoW.
- **02 - Design:**
  - [ARCHITECTURE.md](file:///home/bintang/JavaProject/POS_app/docs/02-design/ARCHITECTURE.md): Arsitektur berlapis, aliran data, dan integrasi Java I/O.
  - [OOP_DESIGN.md](file:///home/bintang/JavaProject/POS_app/docs/02-design/OOP_DESIGN.md): **(Disetujui)** Rancangan 4 pilar OOP, UML Class & Sequence Diagram.
  - [UI_UX_GUIDELINES.md](file:///home/bintang/JavaProject/POS_app/docs/02-design/UI_UX_GUIDELINES.md): Anti-AI-Slop policy, palet warna, dan shortcut keyboard.
  - [UI_SCREEN_SPECIFICATION.md](file:///home/bintang/JavaProject/POS_app/docs/02-design/UI_SCREEN_SPECIFICATION.md): Spesifikasi setiap layar FXML dan controller.
- **03 - Database:**
  - [DATABASE.md](file:///home/bintang/JavaProject/POS_app/docs/03-database/DATABASE.md): Kamus data, struktur tabel, dan tipe data DECIMAL.
  - [ERD.md](file:///home/bintang/JavaProject/POS_app/docs/03-database/ERD.md): Diagram ERD Mermaid dan kardinalitas relasi.
  - [BUSINESS_RULES.md](file:///home/bintang/JavaProject/POS_app/docs/03-database/BUSINESS_RULES.md): Aturan integritas transaksi, stok, dan pembatalan nota.
- **04 - Development:**
  - [PROJECT_STRUCTURE.md](file:///home/bintang/JavaProject/POS_app/docs/04-development/PROJECT_STRUCTURE.md): Rincian struktur package Java dan folder resources.
  - [IMPLEMENTATION_PLAN.md](file:///home/bintang/JavaProject/POS_app/docs/04-development/IMPLEMENTATION_PLAN.md): Tahapan roadmap implementasi bertahap.
  - [CODING_STANDARDS.md](file:///home/bintang/JavaProject/POS_app/docs/04-development/CODING_STANDARDS.md): Konvensi penamaan, clean code, dan error handling.
  - [DEPENDENCY_GUIDE.md](file:///home/bintang/JavaProject/POS_app/docs/04-development/DEPENDENCY_GUIDE.md): Panduan dependensi `pom.xml`.
- **05 - Quality:**
  - [TESTING.md](file:///home/bintang/JavaProject/POS_app/docs/05-quality/TESTING.md): Strategi pengujian otomatis berbasis JUnit 5.
  - [TEST_CASES.md](file:///home/bintang/JavaProject/POS_app/docs/05-quality/TEST_CASES.md): Skenario test cases untuk kalkulasi, transaksi, dan Java I/O.
  - [ACCEPTANCE_CRITERIA.md](file:///home/bintang/JavaProject/POS_app/docs/05-quality/ACCEPTANCE_CRITERIA.md): Kriteria penerimaan fitur akhir.
- **06 - Maintenance:**
  - [SETUP.md](file:///home/bintang/JavaProject/POS_app/docs/06-maintenance/SETUP.md): Panduan setup lingkungan kerja untuk developer baru.
  - [TROUBLESHOOTING.md](file:///home/bintang/JavaProject/POS_app/docs/06-maintenance/TROUBLESHOOTING.md): Solusi permasalahan umum koneksi, konfigurasi, dan build.
  - [CHANGELOG.md](file:///home/bintang/JavaProject/POS_app/docs/06-maintenance/CHANGELOG.md): Catatan perubahan versi dan milestone.
