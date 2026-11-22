# Dependency Guide & Build Configuration - POS_app

Dokumen ini menjelaskan seluruh dependensi pustaka (*third-party libraries*), plugin build Maven, dan alasan teknis pemilihannya pada file `pom.xml`.

---

## 1. Daftar Dependensi (`pom.xml`)

### 1.1 JavaFX 21 (`org.openjfx`)
- **Dependensi:**
  - `javafx-controls` (versi `21.0.2`): Komponen UI desktop (Button, TableView, TextField, Dialog, dll.).
  - `javafx-fxml` (versi `21.0.2`): Engine parser layout file XML (`.fxml`).
  - `javafx-graphics` (versi `21.0.2`): Core rendering engine JavaFX.
- **Tujuan:** Menyediakan antarmuka desktop modern, modular, dan responsif.

### 1.2 MySQL Connector/J (`com.mysql:mysql-connector-j`)
- **Versi:** `8.3.0`
- **Scope:** `compile`
- **Tujuan:** Driver JDBC resmi MySQL untuk menghubungkan aplikasi Java dengan database server MySQL 8.x, mendukung transaksi atomik dan fitur keamanan modern.

### 1.3 jBCrypt (`org.mindrot:jbcrypt`)
- **Versi:** `0.4`
- **Scope:** `compile`
- **Tujuan:** Pustaka standar industri untuk hashing satu arah (*one-way salted hash*) kata sandi akun kasir dan admin. Menjamin keamanan autentikasi tanpa membebani kompleksitas proyek.

### 1.4 JUnit 5 Jupiter (`org.junit.jupiter`)
- **Dependensi:**
  - `junit-jupiter-api` (versi `5.10.2`)
  - `junit-jupiter-engine` (versi `5.10.2`)
  - `junit-jupiter-params` (versi `5.10.2`)
- **Scope:** `test`
- **Tujuan:** Kerangka kerja pengujian unit otomatis untuk memverifikasi kalkulasi transaksi, aturan mutasi stok, validasi model, dan pembacaan Java I/O.

---

## 2. Plugin Maven

1. **`maven-compiler-plugin` (versi `3.13.0`):**
   - Mengatur level rilis compiler Java ke **Java 21 LTS** (`<release>21</release>`).
2. **`javafx-maven-plugin` (versi `0.0.8`):**
   - Memungkinkan menjalankan aplikasi langsung dari terminal dengan perintah:
     ```bash
     mvn javafx:run
     ```
   - Main class dikonfigurasi ke: `com.posapp.app.MainApp`.
3. **`maven-surefire-plugin` (versi `3.2.5`):**
   - Menjalankan seluruh test case JUnit 5 otomatis pada saat eksekusi:
     ```bash
     mvn test
     ```
4. **`maven-shade-plugin` (versi `3.5.2`):**
   - Mengemas aplikasi beserta seluruh dependensinya menjadi satu berkas *executable fat JAR* mandiri untuk kemudahan distribusi.

---

## 3. Alasan Menghindari Dependensi Berlebih (*Zero Bloat Policy*)

Proyek ini sengaja **TIDAK MENGGUNAKAN** framework besar seperti Spring Boot, Hibernate/JPA, atau Lombok:
1. **Fokus pada Pemahaman Fundamental OOP & JDBC:** Proyek ini ditujukan untuk tugas akhir mata kuliah Java OOP. Penggunaan JDBC murni memastikan mahasiswa memahami konsep koneksi, `PreparedStatement`, `ResultSet`, dan manajemen transaksi secara eksplisit.
2. **Performa Ringan:** Aplikasi kasir desktop dapat dibuka dalam hitungan detik tanpa *startup overhead* framework enterprise yang berat.
3. **Transparansi Aliran Data:** Setiap query SQL dan mutasi objek dapat ditelusuri dengan mudah tanpa ada keajaiban (*magic reflection*) di belakang layar.

