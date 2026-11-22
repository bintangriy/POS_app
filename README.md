# POS_app - Point of Sale & Inventory Management

Aplikasi desktop kasir (*Point of Sale*) dan manajemen persediaan barang (*Inventory Management*) yang dikembangkan untuk UMKM ritel. Dibangun dengan bahasa murni **Java 21**, **JavaFX**, arsitektur **OOP murni**, dan penyimpanan data mandiri berbasis **Java I/O dan file CSV**.

Proyek ini merupakan pemenuhan tugas akhir mata kuliah **Java Object-Oriented Programming (OOP)**.

---

## 1. Fitur Utama Aplikasi

- **Authentication:** Login dan logout berbasis hash BCrypt dengan pembedaan peran `Admin` dan `Cashier`. Data tersimpan di `data/users.csv`.
- **Dashboard:** Ringkasan omset hari ini, total transaksi, total produk, dan peringatan produk dengan stok menipis (*low stock alert*).
- **Product Management:** CRUD produk lengkap dengan barcode unik, harga beli (HPP), harga jual, stok, dan batas minimum stok pada `data/products.csv`.
- **Point of Sale (Kasir):** Scanning barcode, keranjang belanja dinamis, kalkulasi kembalian tunai dan verifikasi QRIS, cetak nota, serta pemotongan stok otomatis.
- **Inventory Management:** Pencatatan stok masuk (*Restock*), penyesuaian stok fisik (*Opname*), dan riwayat mutasi persediaan pada `data/stock_movements.csv`.
- **Transaction History:** Riwayat lengkap transaksi dengan filter tanggal dan fitur **pembatalan transaksi oleh Admin** yang secara otomatis mengembalikan stok barang (*stock restoration*).
- **Reports:** Laporan penjualan harian, bulanan, produk terlaris, valuasi aset persediaan, dan laba kotor (*Gross Profit*).

---

## 2. Penerapan Java I/O (CSV Storage)

Aplikasi tidak lagi menggunakan database server luar (MySQL/JDBC), melainkan menerapkan mekanisme penyimpanan berkas mandiri:
- `java.io.File`: Mengelola berkas fisik dan atomic temporary swap (`.tmp`).
- `java.io.FileReader` & `java.io.BufferedReader`: Membaca data CSV baris per baris secara efisien dalam blok `try-with-resources`.
- `java.io.FileWriter` & `java.io.BufferedWriter`: Menulis dan menambahkan baris data ke file CSV.
- `java.io.FileInputStream`: Membaca konfigurasi aplikasi dari `config/app.properties`.
- `CsvParser` & `CsvFormatter`: Parser mandiri yang aman terhadap karakter koma di dalam teks bertanda kutip.

---

## 3. Stack Teknologi

- **Bahasa:** Java 21 LTS
- **GUI:** JavaFX 21 (FXML + CSS)
- **Build Tool:** Apache Maven
- **Penyimpanan:** Berkas CSV lokal via Java I/O (`data/*.csv`)
- **Security:** jBCrypt
- **Testing:** JUnit 5 (Jupiter)

---

## 4. Cara Menjalankan Aplikasi

1. **Setup Konfigurasi:**
   ```bash
   cp config/app.properties.example config/app.properties
   ```
2. **Kompilasi & Uji Otomatis:**
   ```bash
   mvn clean test
   ```
3. **Jalankan Aplikasi Desktop:**
   ```bash
   mvn javafx:run
   ```

---

## 5. Akun Pengguna Bawaan (Default Seed)

| Peran | Username | Password Default | Akses Menu |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Seluruh menu (Dashboard, Produk, Kasir, Stok, Riwayat, Laporan, Pembatalan) |
| **Kasir** | `kasir1` | `kasir123` | Dashboard Kasir, Menu Kasir (POS), Riwayat Transaksi Shift |

---

## 6. Navigasi Dokumentasi

Dokumentasi lengkap proyek dapat diakses pada tautan berikut:
- **Product:** [`docs/01-product/PRD.md`](docs/01-product/PRD.md) | [`docs/01-product/USER_STORIES.md`](docs/01-product/USER_STORIES.md) | [`docs/01-product/SCOPE.md`](docs/01-product/SCOPE.md)
- **Desain & OOP:** [`docs/02-design/OOP_DESIGN.md`](docs/02-design/OOP_DESIGN.md) | [`docs/02-design/ARCHITECTURE.md`](docs/02-design/ARCHITECTURE.md) | [`docs/02-design/UI_SCREEN_SPECIFICATION.md`](docs/02-design/UI_SCREEN_SPECIFICATION.md)
- **Struktur Data Berkas:** [`docs/03-database/DATA_STORAGE.md`](docs/03-database/DATA_STORAGE.md) | [`docs/03-database/FILE_DATA_STRUCTURE.md`](docs/03-database/FILE_DATA_STRUCTURE.md) | [`docs/03-database/BUSINESS_RULES.md`](docs/03-database/BUSINESS_RULES.md)
- **Pengembangan & Standar:** [`docs/04-development/PROJECT_STRUCTURE.md`](docs/04-development/PROJECT_STRUCTURE.md) | [`docs/04-development/CODING_STANDARDS.md`](docs/04-development/CODING_STANDARDS.md) | [`docs/04-development/DEPENDENCY_GUIDE.md`](docs/04-development/DEPENDENCY_GUIDE.md)
- **Kualitas:** [`docs/05-quality/TESTING.md`](docs/05-quality/TESTING.md) | [`docs/05-quality/TEST_CASES.md`](docs/05-quality/TEST_CASES.md) | [`docs/05-quality/ACCEPTANCE_CRITERIA.md`](docs/05-quality/ACCEPTANCE_CRITERIA.md)
- **Maintenance:** [`docs/06-maintenance/SETUP.md`](docs/06-maintenance/SETUP.md) | [`docs/06-maintenance/TROUBLESHOOTING.md`](docs/06-maintenance/TROUBLESHOOTING.md) | [`docs/06-maintenance/CHANGELOG.md`](docs/06-maintenance/CHANGELOG.md)
