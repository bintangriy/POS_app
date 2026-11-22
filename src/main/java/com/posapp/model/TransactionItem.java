package com.posapp.model;

/**
 * Model item baris rincian belanja dalam satu transaksi.
 * Memuat snapshot harga jual dan harga modal beli untuk pelaporan yang akurat.
 */
public class TransactionItem {
    private int id;
    private int transactionId;
    private Product product;
    private int quantity;
    private double unitPrice;          // Snapshot harga jual
    private double buyPriceSnapshot;   // Snapshot harga beli modal (HPP)
    private double discountAmount;
    private double lineTotal;

    public TransactionItem(Product product, int quantity, double unitPrice, double discountAmount) {
        if (product == null) throw new IllegalArgumentException("Produk tidak boleh null.");
        if (quantity <= 0) throw new IllegalArgumentException("Kuantitas harus lebih dari 0.");
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.buyPriceSnapshot = product.getBuyPrice();
        this.discountAmount = Math.max(discountAmount, 0);
        calculateLineTotal();
    }

    public TransactionItem(int id, int transactionId, Product product, int quantity, 
                           double unitPrice, double buyPriceSnapshot, double discountAmount, double lineTotal) {
        this.id = id;
        this.transactionId = transactionId;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.buyPriceSnapshot = buyPriceSnapshot;
        this.discountAmount = discountAmount;
        this.lineTotal = lineTotal;
    }

    public void calculateLineTotal() {
        this.lineTotal = (this.quantity * this.unitPrice) - this.discountAmount;
    }

    public void addQuantity(int additional) {
        if (additional <= 0) throw new IllegalArgumentException("Penambahan kuantitas harus positif.");
        this.quantity += additional;
        calculateLineTotal();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Kuantitas harus positif.");
        this.quantity = quantity;
        calculateLineTotal();
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateLineTotal();
    }

    public double getBuyPriceSnapshot() { return buyPriceSnapshot; }
    public void setBuyPriceSnapshot(double buyPriceSnapshot) { this.buyPriceSnapshot = buyPriceSnapshot; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        calculateLineTotal();
    }

    public double getLineTotal() { return lineTotal; }
}

