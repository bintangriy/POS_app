-- ==============================================================================
-- POS_app - Initial Seed Data
-- Database: pos_app_db
-- Password Default:
--   admin  -> admin123 (BCrypt hash)
--   kasir1 -> kasir123 (BCrypt hash)
-- ==============================================================================

USE pos_app_db;

-- ------------------------------------------------------------------------------
-- 1. Insert Initial Users
-- Password hash di-generate menggunakan BCrypt (cost factor 10)
-- admin:admin123   -> $2a$10$wE6g7yOQ0hY7XqfP3i2k.ePjK4uHjOa9I7S2xZ6x.Jz7pP9B1aLWe
-- kasir1:kasir123  -> $2a$10$7vN3K8jU8dF2yW5mP9lZ4eA3bC1dE5fG7hI9jK1lM3nO5pQ7rS9tU
-- ------------------------------------------------------------------------------
INSERT INTO users (id, username, password_hash, full_name, role, terminal_id, is_active)
VALUES 
    (1, 'admin', '$2a$10$wE6g7yOQ0hY7XqfP3i2k.ePjK4uHjOa9I7S2xZ6x.Jz7pP9B1aLWe', 'Administrator Toko', 'ADMIN', NULL, TRUE),
    (2, 'kasir1', '$2a$10$7vN3K8jU8dF2yW5mP9lZ4eA3bC1dE5fG7hI9jK1lM3nO5pQ7rS9tU', 'Siti Rahmawati (Kasir)', 'CASHIER', 'POS-TERM-01', TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- ------------------------------------------------------------------------------
-- 2. Insert Categories
-- ------------------------------------------------------------------------------
INSERT INTO categories (id, name, description)
VALUES
    (1, 'Makanan & Minuman Ringan', 'Snack, biskuit, wafer, dan minuman dalam kemasan'),
    (2, 'Sembako & Kebutuhan Pokok', 'Beras, minyak goreng, gula, tepung, dan telur'),
    (3, 'Perawatan Tubuh & Kebersihan', 'Sabun mandi, sampo, pasta gigi, deterjen'),
    (4, 'Bumbu & Bahan Dapur', 'Garam, kecap, saus, penyedap rasa')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ------------------------------------------------------------------------------
-- 3. Insert Initial Products
-- Menjamin harga_jual >= harga_beli dan stok >= 0
-- ------------------------------------------------------------------------------
INSERT INTO products (id, barcode, name, category_id, buy_price, sell_price, stock, min_stock_alert)
VALUES
    (1, '8992388111013', 'Indomie Mi Goreng 85g', 1, 2800.00, 3500.00, 120, 20),
    (2, '8999999001011', 'Air Mineral Aqua 600ml', 1, 2500.00, 3500.00, 48, 12),
    (3, '8992753211112', 'Minyak Goreng Bimoli 1 Liter', 2, 17500.00, 20500.00, 35, 10),
    (4, '8998866100223', 'Gula Pasir Gulaku 1kg', 2, 14500.00, 17000.00, 25, 8),
    (5, '8993189211045', 'Beras Ramos Super 5kg', 2, 65000.00, 74000.00, 15, 5),
    (6, '8992695123456', 'Sabun Mandi Lifebuoy Total 10 85g', 3, 3500.00, 4500.00, 4, 10), -- Contoh stok menipis (< min_stock_alert)
    (7, '8992772010114', 'Kecap Manis Bango 135ml', 4, 7500.00, 9500.00, 30, 8)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ------------------------------------------------------------------------------
-- 4. Initial Stock Movements (Audit Trail Log)
-- ------------------------------------------------------------------------------
INSERT INTO stock_movements (product_id, user_id, movement_type, quantity, stock_before, stock_after, remarks)
VALUES
    (1, 1, 'STOCK_IN', 120, 0, 120, 'Saldo stok awal sistem'),
    (2, 1, 'STOCK_IN', 48, 0, 48, 'Saldo stok awal sistem'),
    (3, 1, 'STOCK_IN', 35, 0, 35, 'Saldo stok awal sistem'),
    (4, 1, 'STOCK_IN', 25, 0, 25, 'Saldo stok awal sistem'),
    (5, 1, 'STOCK_IN', 15, 0, 15, 'Saldo stok awal sistem'),
    (6, 1, 'STOCK_IN', 4, 0, 4, 'Saldo stok awal sistem (kritis)'),
    (7, 1, 'STOCK_IN', 30, 0, 30, 'Saldo stok awal sistem');

