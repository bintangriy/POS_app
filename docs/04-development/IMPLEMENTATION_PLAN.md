# Implementation Plan & Roadmap - POS_app

Dokumen ini mendefinisikan tahapan implementasi bertahap untuk refactoring penyimpanan data dari database SQL/MySQL ke **Java I/O dan file CSV**.

---

## 1. Roadmap Tahapan Pengembangan

1. **Fase 1: Pembersihan Dependensi & Penyiapan Berkas CSV Data (SELESAI):**
   - Hapus dependensi `mysql-connector-j` pada `pom.xml`.
   - Buat direktori `data/` dan inisialisasi 6 berkas CSV awal (`users.csv`, `categories.csv`, `products.csv`, `transactions.csv`, `transaction_details.csv`, `stock_movements.csv`).
   - Buat `config/app.properties.example` dan `config/app.properties`.

2. **Fase 2: Implementasi Core Java I/O (SELESAI):**
   - `AppConfig.java`: pembacaan konfigurasi file via `FileInputStream`.
   - `CsvParser.java` & `CsvFormatter.java`: parser CSV mandiri yang mematuhi RFC 4180.
   - `CsvFileManager.java`: pembacaan (`BufferedReader`), penulisan atomik (`BufferedWriter` + `.tmp` swap), dan append file CSV.

3. **Fase 3: Domain Model & DAO CSV (SELESAI):**
   - Implementasi model domain (`User`, `Admin`, `Cashier`, `Product`, `Category`, `Transaction`, `TransactionItem`, `Payment`, `CashPayment`, `QrisPayment`, `StockMovement`).
   - Implementasi antarmuka DAO dan kelas konkrit CSV (`UserDaoCsv`, `CategoryDaoCsv`, `ProductDaoCsv`, `TransactionDaoCsv`, `InventoryDaoCsv`).

4. **Fase 4: Business Logic Services & Multi-File Consistency (SELESAI):**
   - Implementasi `AuthService`, `ProductService`, `PosService`, `InventoryService`, `TransactionService`, `DashboardService`, `ReportService`.
   - Penegakan pre-validation in-memory sebelum mutasi multi-file pada `PosService` dan `TransactionService`.

5. **Fase 5: JavaFX Desktop GUI & View FXML (SELESAI):**
   - Implementasi `MainApp.java`, `AppLauncher.java`, dan 11 controller JavaFX.
   - Implementasi 11 file FXML tampilan dan stylesheet `main.css`.

6. **Fase 6: Automated Testing & Verifikasi Quality Gate 2:**
   - Jalankan seluruh unit testing JUnit 5 (`mvn clean test`) untuk memverifikasi fungsionalitas CSV parsing, DAO, dan Service layer.
