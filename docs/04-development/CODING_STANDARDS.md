# Coding Standards & Clean Code Guidelines - POS_app

Dokumen ini mendefinisikan standar penulisan kode Java, arsitektur berbasis objek, penamaan, dan penanganan kesalahan untuk aplikasi **POS_app**.

---

## 1. Standar Penamaan (Naming Conventions)

| Elemen Kode | Konvensi | Contoh |
| :--- | :--- | :--- |
| **Package** | Seluruhnya huruf kecil, tanpa garis bawah (`lowercase`) | `com.posapp.model`, `com.posapp.dao.jdbc` |
| **Class & Interface** | PascalCase, kata benda deskriptif | `TransactionItem`, `ProductDao`, `AuthService` |
| **Method** | camelCase, diawali kata kerja | `calculateGrandTotal()`, `reduceStock()`, `findByBarcode()` |
| **Variable & Field** | camelCase, nama deskriptif (hindari singkatan 1 huruf) | `amountPaid`, `subtotalDiscount`, `invoiceNumber` |
| **Constant / Enum** | UPPER_SNAKE_CASE | `PaymentType.CASH`, `Role.ADMIN`, `MAX_ATTEMPTS` |
| **FXML File** | snake_case | `main_layout.fxml`, `transaction_history.fxml` |
| **CSS Class** | kebab-case | `.btn-primary`, `.text-danger`, `.metric-card` |

---

## 2. Prinsip OOP & Clean Code

1. **Encapsulation Mutlak:**
   - Seluruh atribut model wajib berstatus `private`.
   - Gunakan method getter untuk membaca dan method mutasi dengan validasi bisnis internal (misal: `reduceStock(int qty)`).
   - Lindungi koleksi dari modifikasi ilegal luar dengan mengembalikan `Collections.unmodifiableList(items)`.
2. **Single Responsibility Principle (SRP):**
   - Setiap kelas hanya memiliki satu alasan untuk berubah.
   - Controller JavaFX hanya menangani interaksi UI dan delegasi ke Service; tidak boleh menulis query SQL atau mengelola koneksi database.
   - DAO hanya menangani query SQL dan mapping ke model domain.
   - Service menangani logika bisnis, koordinasi transaksi, dan validasi tingkat tinggi.
3. **Hindari Primitive Obsession pada Status:**
   - Gunakan `enum` bertipe kuat (`Role`, `PaymentType`, `TransactionStatus`, `MovementType`) alih-alih angka ajaib (*magic numbers*) atau raw strings.
4. **Pencegahan Nilai Moneter Mengambang (No Floating-Point Money):**
   - Gunakan tipe `double` atau `BigDecimal` di Java dengan presisi terhitung rapi, dan simpan sebagai `DECIMAL(12, 2)` di MySQL.
5. **No God Objects:**
   - Batasi panjang kelas maksimal 300-400 baris. Jika sebuah kelas terlalu panjang, lakukan dekomposisi ke kelas pembantu (*helper/service*).

---

## 3. Penanganan Kesalahan (Exception Handling)

1. **Gunakan Try-With-Resources:**
   - Seluruh resource I/O (`FileInputStream`) dan JDBC (`Connection`, `PreparedStatement`, `ResultSet`) **wajib** ditutup menggunakan *try-with-resources* untuk mencegah *resource leakage*.
   ```java
   try (FileInputStream fis = new FileInputStream(file)) {
       properties.load(fis);
   } catch (IOException e) {
       // Tangani exception secara informatif
   }
   ```
2. **Jangan Menelan Exception (No Silent Catch):**
   - Dilarang membuat blok `catch` kosong:
     ```java
     // SALAH:
     catch (Exception e) {}
     
     // BENAR:
     catch (SQLException e) {
         logger.severe("Gagal mengeksekusi transaksi: " + e.getMessage());
         throw new DatabaseException("Kesalahan akses basis data.", e);
     }
     ```
3. **Pesan Kesalahan Ramah Pengguna:**
   - Tangkap exception teknis di level Controller dan tampilkan pesan yang mudah dipahami oleh staf kasir melalui dialog JavaFX (`Alert.AlertType.ERROR`).

---

## 4. Keamanan & Sanitasi Query Database

1. **PreparedStatement Wajib:**
   - Query SQL dinamis yang menerima input pengguna wajib menggunakan parameter binding:
   ```java
   // BENAR:
   String sql = "SELECT * FROM products WHERE barcode = ?";
   PreparedStatement stmt = conn.prepareStatement(sql);
   stmt.setString(1, barcode);

   // DILARANG KERAS (Vulnerable to SQL Injection):
   String sql = "SELECT * FROM products WHERE barcode = '" + barcode + "'";
   ```
2. **Hashing Password:**
   - Seluruh kata sandi pengguna wajib diverifikasi melalui library `jBCrypt` (`BCrypt.checkpw(plainPassword, hashedPassword)`). Password tidak boleh dicatat pada log atau disimpan plaintext.

