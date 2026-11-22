# System Architecture & Technical Design - POS_app

Dokumen ini menjelaskan arsitektur sistem berlapis (*Layered Architecture*), aliran data, integrasi Java I/O untuk penyimpanan berkas CSV, serta strategi konsistensi data multi-file pada **POS_app**.

---

## 1. Pola Arsitektur Berlapis (Layered Architecture)

Aplikasi POS_app memisahkan tanggung jawab kode secara tegas ke dalam 6 tingkatan:

```mermaid
flowchart TD
    subgraph View_Layer["1. Presentation Layer (JavaFX)"]
        UI_FXML["FXML Views & CSS Styles"]
        CTRL["JavaFX Controllers\n(Login, Dashboard, Pos, Product, Inventory, History, Report)"]
    end

    subgraph Service_Layer["2. Business Logic Layer"]
        SVC["Services\n(AuthService, DashboardService, PosService, ProductService, InventoryService, ReportService)"]
    end

    subgraph Domain_Layer["3. Domain / Model Layer"]
        MDL["Entities & Enums\n(Product, Transaction, TransactionItem, User, Payment, etc.)"]
    end

    subgraph Dao_Layer["4. Data Access Layer (Abstraction)"]
        DAO_INT["DAO Interfaces\n(ProductDao, TransactionDao, UserDao, CategoryDao, InventoryDao)"]
        DAO_CSV["CSV DAO Implementations\n(ProductDaoCsv, TransactionDaoCsv, UserDaoCsv, CategoryDaoCsv, InventoryDaoCsv)"]
    end

    subgraph Storage_Manager["5. CSV Storage Engine"]
        MGR["CsvFileManager & CsvParser / CsvFormatter\n(Atomic File Swap, Thread-Safe Access)"]
    end

    subgraph Java_IO["6. Java I/O (Local Filesystem)"]
        IO["java.io.File, FileReader, BufferedReader\nFileWriter, BufferedWriter, FileInputStream"]
        CSV_FILES["data/*.csv (users, products, transactions, dll.)"]
    end

    UI_FXML --> CTRL
    CTRL --> SVC
    SVC --> MDL
    SVC --> DAO_INT
    DAO_CSV ..|> DAO_INT
    DAO_CSV --> MGR
    MGR --> IO
    IO --> CSV_FILES
    CTRL --> MDL
```

---

## 2. Tanggung Jawab Masing-Masing Lapisan

1. **Presentation Layer (`com.posapp.controller`):** Menangani interaksi UI JavaFX, membaca form FXML, memvalidasi format input tampilan, dan menampilkan data ke tabel. Dilarang mengakses berkas CSV secara langsung.
2. **Business Logic Layer (`com.posapp.service`):** Memproses aturan bisnis, kalkulasi pembayaran, validasi stok sebelum checkout, dan mengorkestrasi mutasi multi-file.
3. **Domain Layer (`com.posapp.model`):** Entitas murni (`Product`, `Transaction`, `User`, `Payment`, dll.) yang memelihara integritas data internal. Bebas dari dependensi UI maupun I/O.
4. **Data Access Layer (`com.posapp.dao.csv`):** Mengimplementasikan antarmuka DAO untuk memetakan baris CSV ke objek domain.
5. **Storage Manager (`com.posapp.util.CsvFileManager`):** Menyediakan method `readAll()`, `writeAll()` dengan teknik *atomic swap*, dan `append()` yang aman.
6. **Java I/O:** Membaca data menggunakan `FileReader`/`BufferedReader` dan menulis data menggunakan `FileWriter`/`BufferedWriter` dalam blok `try-with-resources`.

---

## 3. Strategi Konsistensi Data Multi-File

Untuk menjaga keutuhan data saat operasi yang melibatkan banyak file (misalnya saat checkout kasir: menyimpan transaksi, detail transaksi, memperbarui stok produk, dan mencatat mutasi):

1. **Pre-Validation In-Memory:** Memeriksa seluruh ketersediaan stok fisik di memori sebelum menulis ke file manapun. Jika ada satu item yang stoknya kurang, proses dibatalkan sebelum berkas tersentuh.
2. **Atomic Temporary File Swap (`.tmp`):** Penulisan pembaruan stok pada `products.csv` dilakukan ke `products.csv.tmp` terlebih dahulu. Setelah selesai tanpa galat, file diganti secara atomik (`Files.move` dengan opsi `REPLACE_EXISTING`).
3. **Sequential Append:** Header nota (`transactions.csv`), rincian item (`transaction_details.csv`), dan log audit (`stock_movements.csv`) ditambahkan secara sekuensial.
