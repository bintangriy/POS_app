package com.posapp.model;

/**
 * Entitas domain produk dengan aturan validasi harga dan mutasi stok terkontrol.
 */
public class Product {
    private int id;
    private String barcode;
    private String name;
    private Category category;
    private double buyPrice;
    private double sellPrice;
    private int stock;
    private int minStockAlert;

    public Product(int id, String barcode, String name, Category category,
                   double buyPrice, double sellPrice, int stock, int minStockAlert) {
        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Harga tidak boleh bernilai negatif.");
        }
        if (sellPrice < buyPrice) {
            throw new IllegalArgumentException("Harga jual (" + sellPrice + ") tidak boleh lebih rendah dari harga beli (" + buyPrice + ").");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stok awal tidak boleh negatif.");
        }
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.category = category;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stock = stock;
        this.minStockAlert = Math.max(minStockAlert, 0);
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah pengurangan stok harus lebih dari 0.");
        }
        if (this.stock < quantity) {
            throw new IllegalStateException("Stok tidak mencukupi untuk produk '" + this.name + "'. Sisa: " + this.stock + ", Diminta: " + quantity);
        }
        this.stock -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Penambahan stok harus lebih dari 0.");
        }
        this.stock += quantity;
    }

    public boolean isLowStock() {
        return this.stock <= this.minStockAlert;
    }

    public double calculateProfitMargin() {
        return this.sellPrice - this.buyPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) {
        if (buyPrice < 0) throw new IllegalArgumentException("Harga beli tidak boleh negatif.");
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) {
        if (sellPrice < this.buyPrice) throw new IllegalArgumentException("Harga jual tidak boleh lebih rendah dari harga beli.");
        this.sellPrice = sellPrice;
    }

    public int getStock() { return stock; }
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stok tidak boleh negatif.");
        this.stock = stock;
    }

    public int getMinStockAlert() { return minStockAlert; }
    public void setMinStockAlert(int minStockAlert) { this.minStockAlert = minStockAlert; }

    @Override
    public String toString() {
        return name + " (" + barcode + ")";
    }
}

