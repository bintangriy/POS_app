# Dependency Guide & Build Configuration - POS_app

Dokumen ini menjelaskan dependensi pustaka (*third-party libraries*) dan plugin build Maven yang digunakan pada berkas `pom.xml`.

---

## 1. Daftar Dependensi Aktif (`pom.xml`)

### 1.1 JavaFX 21 (`org.openjfx`)
- **Dependensi:** `javafx-controls`, `javafx-fxml`, `javafx-graphics` (versi `21.0.2`).
- **Tujuan:** Antarmuka desktop modern, modular, dan responsif.

### 1.2 jBCrypt (`org.mindrot:jbcrypt`)
- **Versi:** `0.4`
- **Scope:** `compile`
- **Tujuan:** Hashing kata sandi satu arah untuk keamanan akun kasir dan admin di berkas `data/users.csv`.

### 1.3 JUnit 5 Jupiter (`org.junit.jupiter`)
- **Dependensi:** `junit-jupiter-api`, `junit-jupiter-engine`, `junit-jupiter-params` (versi `5.10.2`).
- **Scope:** `test`
- **Tujuan:** Kerangka pengujian unit otomatis untuk Java I/O CSV, model domain, dan service layer.

---

## 2. Penghapusan Ketergantungan Database

- **Dependensi yang Dihapus:** `com.mysql:mysql-connector-j`.
- **Alasan:** Sesuai perubahan persyaratan akademik, aplikasi menggunakan penyimpanan berkas lokal berbasis CSV dan Java I/O murni tanpa ketergantungan pada database MySQL/JDBC.
