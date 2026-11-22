# Developer Environment Setup Guide - POS_app

Panduan instalasi dan menjalankan aplikasi desktop **POS_app** berbasis **Java I/O dan file CSV**.

---

## 1. Prasyarat Sistem

1. **Java Development Kit (JDK):** Versi 21 LTS atau lebih tinggi.
2. **Apache Maven:** Versi 3.8+ (tersedia melalui PATH).
3. **Catatan Penting:** Tidak memerlukan instalasi database server MySQL, database service, ataupun konfigurasi port jaringan.

---

## 2. Langkah Menjalankan Aplikasi

1. **Clone Repository & Masuk ke Folder Proyek:**
   ```bash
   cd POS_app
   ```

2. **Setup Konfigurasi Aplikasi (Java I/O):**
   Salin template konfigurasi jika belum ada:
   ```bash
   cp config/app.properties.example config/app.properties
   ```

3. **Verifikasi File Data Awal:**
   Pastikan direktori `data/` telah berisi berkas data awal:
   - `data/users.csv`
   - `data/categories.csv`
   - `data/products.csv`
   - `data/transactions.csv`
   - `data/transaction_details.csv`
   - `data/stock_movements.csv`

4. **Kompilasi & Jalankan Pengujian:**
   ```bash
   mvn clean test
   ```

5. **Jalankan Aplikasi Desktop JavaFX:**
   ```bash
   mvn javafx:run
   ```

---

## 3. Akun Bawaan untuk Pengujian

- **Admin:** Username `admin` | Password `admin123`
- **Kasir:** Username `kasir1` | Password `kasir123`
