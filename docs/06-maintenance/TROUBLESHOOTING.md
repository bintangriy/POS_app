# Troubleshooting Guide - POS_app

Dokumen ini merangkum masalah umum (*common issues*) yang mungkin terjadi saat setup atau eksekusi aplikasi **POS_app** beserta langkah penyelesaiannya (*solutions*).

---

## 1. Permasalahan Terkait Java I/O & Konfigurasi

### Masalah 1.1: `RuntimeException: File konfigurasi 'config/app.properties' tidak ditemukan!`
- **Penyebab:** File `config/app.properties` belum dibuat di root direktori proyek.
- **Solusi:**
  1. Jalankan perintah di terminal root:
     ```bash
     cp config/app.properties.example config/app.properties
     ```
  2. Buka file `config/app.properties` dan pastikan kredensial `db.user` dan `db.password` telah diisi dengan benar.

### Masalah 1.2: `IllegalStateException: Konfigurasi wajib hilang atau kosong: db.host`
- **Penyebab:** Salah satu properti wajib (`db.host`, `db.port`, `db.name`, `db.user`) terhapus atau bernilai kosong di dalam file `config/app.properties`.
- **Solusi:** Periksa kembali file `config/app.properties` dan lengkapi nilai yang hilang sesuai dengan format pada `config/app.properties.example`.

---

## 2. Permasalahan Database MySQL & JDBC

### Masalah 2.1: `CommunicationsException: Communications link failure`
- **Penyebab:** Layanan MySQL server tidak aktif atau port database salah.
- **Solusi:**
  1. Pastikan service MySQL berjalan:
     ```bash
     sudo systemctl status mysql
     # Jika belum aktif:
     sudo systemctl start mysql
     ```
  2. Periksa apakah port pada `config/app.properties` (`db.port=3306`) sesuai dengan port MySQL lokal Anda.

### Masalah 2.2: `SQLException: Access denied for user 'root'@'localhost'`
- **Penyebab:** Password MySQL pada `config/app.properties` tidak sesuai dengan password user MySQL lokal.
- **Solusi:** Perbarui nilai `db.password` pada `config/app.properties` dengan password MySQL yang valid.

### Masalah 2.3: `SQLException: Check constraint 'chk_product_stock' is violated`
- **Penyebab:** Operasi pengurangan stok mencoba menghasilkan nilai negatif ($< 0$).
- **Solusi:** Ini adalah fitur pengaman database yang bekerja semestinya untuk mencegah stok minus. Periksa kuantitas transaksi kasir agar tidak melebihi sisa stok fisik di database.

---

## 3. Permasalahan JavaFX & Maven Build

### Masalah 3.1: `Error: JavaFX runtime components are missing, and are required to run this application`
- **Penyebab:** Menjalankan `java` secara langsung tanpa menyertakan modul JavaFX.
- **Solusi:** Selalu jalankan aplikasi melalui plugin Maven JavaFX resmi:
  ```bash
  mvn javafx:run
  ```
  Atau gunakan wrapper main class `AppLauncher.java`.

### Masalah 3.2: `LoadException: Location is not set` saat memuat FXML
- **Penyebab:** Jalur path resource FXML pada `FXMLLoader` salah atau file FXML tidak berada dalam `src/main/resources/com/posapp/views/`.
- **Solusi:** Pastikan pemanggilan menggunakan path absolut classpath:
  ```java
  getClass().getResource("/com/posapp/views/login.fxml");
  ```

