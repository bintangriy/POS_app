# Testing Strategy & Quality Assurance Plan - POS_app

Dokumen ini mendefinisikan strategi pengujian otomatis untuk memverifikasi keandalan penyimpanan berkas Java I/O CSV, integritas transaksi, dan logika bisnis (**Quality Gate 2**).

---

## 1. Piramida & Cakupan Pengujian

1. **Unit Testing (JUnit 5):**
   - Pengujian parsing & formatting CSV (koma, tanda kutip, escaped quotes).
   - Pengujian pembacaan konfigurasi `AppConfig` via `FileInputStream`.
   - Pengujian invarian model `Product`, `Transaction`, `Payment`.
2. **Integration Testing (CSV DAO & Service Layer):**
   - Pengujian seluruh operasi CRUD DAO berbasis CSV menggunakan direktori sementara (`@TempDir`) agar data asli aplikasi tidak terpengaruh saat pengujian berjalan.
   - Pengujian orkestrasi checkout kasir dan pembatalan transaksi oleh Admin.
3. **Manual Visual Testing (JavaFX GUI):**
   - Memastikan binding FXML, responsivitas antarmuka, dan dialog modal bekerja lancar.

---

## 2. Menjalankan Automated Tests

```bash
mvn test
```
Seluruh pengujian unit wajib menghasilkan status hijau (*exit code 0*).
