package com.posapp.model;

import com.posapp.model.enums.MovementType;
import java.time.LocalDateTime;

/**
 * Entitas log mutasi persediaan barang untuk keperluan audit trail.
 */
public class StockMovement {
    private int id;
    private Product product;
    private User user;
    private MovementType movementType;
    private int quantity;
    private int stockBefore;
    private int stockAfter;
    private String remarks;
    private LocalDateTime createdAt;

    public StockMovement(int id, Product product, User user, MovementType movementType,
                         int quantity, int stockBefore, int stockAfter, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.product = product;
        this.user = user;
        this.movementType = movementType;
        this.quantity = quantity;
        this.stockBefore = stockBefore;
        this.stockAfter = stockAfter;
        this.remarks = remarks;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public StockMovement(Product product, User user, MovementType movementType,
                         int quantity, int stockBefore, int stockAfter, String remarks) {
        this(0, product, user, movementType, quantity, stockBefore, stockAfter, remarks, LocalDateTime.now());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStockBefore() { return stockBefore; }
    public void setStockBefore(int stockBefore) { this.stockBefore = stockBefore; }

    public int getStockAfter() { return stockAfter; }
    public void setStockAfter(int stockAfter) { this.stockAfter = stockAfter; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

