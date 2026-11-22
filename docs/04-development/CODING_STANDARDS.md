# Coding Standards & Clean Code Guidelines - POS_app

Dokumen ini mendefinisikan standar penulisan kode Java, penamaan, enkapsulasi, penanganan berkas Java I/O, dan clean code untuk aplikasi **POS_app**.

---

## 1. Standar Penamaan (Naming Conventions)

| Elemen Kode | Konvensi | Contoh |
| :--- | :--- | :--- |
| **Package** | Seluruhnya huruf kecil (`lowercase`) | `com.posapp.model`, `com.posapp.dao.csv` |
| **Class & Interface** | PascalCase, kata benda deskriptif | `TransactionItem`, `ProductDaoCsv`, `CsvFileManager` |
| **Method** | camelCase, diawali kata kerja | `calculateGrandTotal()`, `reduceStock()`, `writeAll()` |
| **Variable & Field** | camelCase, deskriptif | `amountPaid`, `subtotalDiscount`, `invoiceNumber` |
| **Constant / Enum** | UPPER_SNAKE_CASE | `PaymentType.CASH`, `Role.ADMIN`, `MovementType.SALE` |
| **CSV File** | snake_case, ekstensi `.csv` | `products.csv`, `transaction_details.csv` |
| **FXML File** | snake_case, ekstensi `.fxml` | `main_layout.fxml`, `transaction_history.fxml` |

---

## 2. Aturan Khusus Java I/O & CSV

1. **Wajib Menggunakan Try-With-Resources:**
   - Seluruh resource I/O (`FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, `FileInputStream`) **wajib** dibuka dalam blok `try (...)` agar otomatis tertutup secara aman.
2. **Dilarang Menggunakan String.split(",") Secara Naif:**
   - Parsing CSV wajib melalui `CsvParser.parseLine()` agar nilai yang mengandung koma di dalam tanda kutip tidak terpotong secara keliru.
3. **Atomic File Write Pattern:**
   - Pembaruan berkas penuh (seperti update data produk) harus ditulis ke berkas sementara `.tmp` terlebih dahulu sebelum me-replace file asli.
