# Troubleshooting Guide - POS_app

Dokumen ini merangkum kendala umum yang mungkin terjadi pada aplikasi **POS_app** berbasis Java I/O dan langkah penyelesaiannya.

---

## 1. Kendala Java I/O & File Data

### Kendala 1.1: `RuntimeException: File konfigurasi 'config/app.properties' tidak ditemukan!`
- **Penyebab:** Berkas `config/app.properties` belum dibuat.
- **Solusi:** Jalankan perintah `cp config/app.properties.example config/app.properties`.

### Kendala 1.2: `IOException: Direktori data/ tidak ditemukan atau tidak dapat diakses`
- **Penyebab:** Izin akses folder atau direktori `data/` belum terbuat.
- **Solusi:** `CsvFileManager` secara otomatis memanggil `dir.mkdirs()`. Pastikan user OS memiliki izin baca dan tulis pada direktori kerja proyek.

### Kendala 1.3: Data di tabel kosong setelah aplikasi dijalankan
- **Penyebab:** File `data/*.csv` terhapus atau kosong tanpa baris data awal.
- **Solusi:** Periksa berkas `data/users.csv`, `data/categories.csv`, dan `data/products.csv`. Pastikan baris header dan data awal tersedia sesuai dokumentasi.

---

## 2. Kendala JavaFX Runtime

### Kendala 2.1: `JavaFX runtime components are missing`
- **Solusi:** Jalankan aplikasi menggunakan plugin Maven resmi:
  ```bash
  mvn javafx:run
  ```
