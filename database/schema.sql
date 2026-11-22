-- ==============================================================================
-- POS_app - Database Schema Definition (DDL)
-- Engine: MySQL 8.x
-- Karakteristik: Strict constraints, DECIMAL untuk uang, Foreign Keys, Audit Trail
-- ==============================================================================

CREATE DATABASE IF NOT EXISTS pos_app_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE pos_app_db;

-- ------------------------------------------------------------------------------
-- 1. Table: users
-- Menyimpan akun pengguna untuk otentikasi kasir dan admin
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'CASHIER') NOT NULL,
    terminal_id VARCHAR(50) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 2. Table: categories
-- Pengelompokan jenis produk
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_name (name)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 3. Table: products
-- Master data produk, harga modal, harga jual, dan stok saat ini
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    barcode VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    buy_price DECIMAL(12, 2) NOT NULL,
    sell_price DECIMAL(12, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    min_stock_alert INT NOT NULL DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_product_buy_price CHECK (buy_price >= 0),
    CONSTRAINT chk_product_sell_price CHECK (sell_price >= buy_price),
    CONSTRAINT chk_product_stock CHECK (stock >= 0), -- Mencegah stok negatif pada level database
    CONSTRAINT chk_product_min_stock CHECK (min_stock_alert >= 0),
    INDEX idx_product_barcode (barcode),
    INDEX idx_product_name (name),
    INDEX idx_product_stock (stock)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 4. Table: transactions
-- Header transaksi penjualan kasir
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    grand_total DECIMAL(12, 2) NOT NULL,
    payment_type ENUM('CASH', 'QRIS') NOT NULL,
    amount_paid DECIMAL(12, 2) NOT NULL,
    change_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    reference_number VARCHAR(100) NULL,
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'COMPLETED',
    cancelled_by INT NULL,
    cancelled_at DATETIME NULL,
    cancellation_reason VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_transactions_cancelled_by FOREIGN KEY (cancelled_by)
        REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_trans_grand_total CHECK (grand_total >= 0),
    INDEX idx_trans_invoice (invoice_number),
    INDEX idx_trans_date (transaction_date),
    INDEX idx_trans_status (status)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 5. Table: transaction_details (transaction_items)
-- Rincian produk dalam transaksi belanja dengan snapshot harga
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transaction_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,         -- Snapshot harga jual saat transaksi terjadi
    buy_price_snapshot DECIMAL(12, 2) NOT NULL, -- Snapshot harga modal untuk hitung laba kotor akurat
    discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    line_total DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_details_transaction FOREIGN KEY (transaction_id)
        REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_details_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_details_qty CHECK (quantity > 0),
    CONSTRAINT chk_details_unit_price CHECK (unit_price >= 0),
    INDEX idx_details_trans_id (transaction_id),
    INDEX idx_details_prod_id (product_id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------------------------
-- 6. Table: stock_movements
-- Log audit mutasi persediaan barang (Stock-in, Penjualan, Koreksi, Retur Pembatalan)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS stock_movements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    movement_type ENUM('STOCK_IN', 'ADJUSTMENT', 'SALE', 'CANCELLATION_RETURN') NOT NULL,
    quantity INT NOT NULL,
    stock_before INT NOT NULL,
    stock_after INT NOT NULL,
    remarks VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movements_product FOREIGN KEY (product_id)
        REFERENCES products(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_movements_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_movements_prod_id (product_id),
    INDEX idx_movements_type (movement_type),
    INDEX idx_movements_date (created_at)
) ENGINE=InnoDB;

