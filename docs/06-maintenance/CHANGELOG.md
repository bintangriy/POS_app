# Changelog & Version History - POS_app

Seluruh perubahan penting pada spesifikasi, arsitektur, dan kode aplikasi **POS_app** dicatat dalam dokumen ini.

---

## [1.1.0-pos-enhancement] - 2026-11-22 - Peningkatan UX Kasir & Auto Barcode

### Ditambahkan (Added)
- **Katalog Produk Cepat Kasir:** Menambahkan panel tabel katalog produk pada sisi kiri layar kasir (`pos.fxml`) dilengkapi pencarian filter *real-time* dan tombol inline **`[ ➕ ]`** per baris untuk memasukkan produk ke keranjang belanja tanpa perlu memindai barcode.
- **Dukungan Double-Click:** Memungkinkan kasir menambahkan produk ke keranjang belanja cukup dengan klik ganda (*double-click*) pada baris produk katalog.
- **Kontrol Kuantitas Inline di Keranjang:** Menambahkan tombol cepat **`[ + ]`** dan **`[ - ]`** langsung di dalam baris item tabel keranjang belanja untuk menambah/mengurangi kuantitas secara instan.
- **Generator Barcode Otomatis:** Tombol **`[ ➕ Auto Barcode ]`** pada modal master produk (`product_dialog.fxml`) yang secara otomatis menghasilkan 13 digit kode unik standar retail (`899...`) jika produk UMKM tidak memiliki kemasan barcode pabrik.

---

## [1.0.0-csv] - 2026-11-22 - Refactoring Java I/O (CSV Storage)

### Diubah (Changed)
- **Mekanisme Persistensi:** Menghapus seluruh ketergantungan pada MySQL, SQL, JDBC, dan database server. Penyimpanan data kini murni berbasis **Java I/O dan berkas CSV** pada direktori `data/`.
- **Implementasi DAO:** Menggantikan kelas `*DaoJdbc` menjadi `*DaoCsv` (`UserDaoCsv`, `CategoryDaoCsv`, `ProductDaoCsv`, `TransactionDaoCsv`, `InventoryDaoCsv`) dengan tetap mempertahankan kontrak antarmuka DAO yang sama persis.
- **Utilitas Akses Berkas:** Menggantikan `DatabaseConnection` menjadi `CsvFileManager` yang memanfaatkan `File`, `FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, dan `IOException` dengan pola *atomic temporary file swap* (`.tmp`).
- **Dependensi Maven:** Menghapus `mysql-connector-j` dari `pom.xml`.
- **Dokumentasi Terkait:**
  - `docs/03-database/DATABASE.md` ditransformasikan menjadi `docs/03-database/DATA_STORAGE.md`.
  - `docs/03-database/ERD.md` ditransformasikan menjadi `docs/03-database/FILE_DATA_STRUCTURE.md`.
  - `database/schema.sql` dan `database/seed.sql` digantikan dengan berkas data CSV aktif di `data/*.csv`.

### Ditambahkan (Added)
- **Java I/O Engine:** `CsvParser.java` dan `CsvFormatter.java` (RFC 4180 compliant) yang mampu menangani field berkoma di dalam tanda kutip dan karakter escaped quote (`""`).
- **Data Awal CSV:** 6 file awal di `data/` (`users.csv`, `categories.csv`, `products.csv`, `transactions.csv`, `transaction_details.csv`, `stock_movements.csv`).
- **Automated Tests:** Suite pengujian unit lengkap JUnit 5 untuk parsing CSV, invarian domain, mutasi stok, transaksi kasir, dan pembatalan nota.
