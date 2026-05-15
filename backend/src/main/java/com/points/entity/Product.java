package com.points.entity;

import java.time.LocalDateTime;

public class Product {
    private String productId;
    private String name;
    private int pointsRequired;
    private int stock;
    private int lockedStock;
    private LocalDateTime createdAt;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getLockedStock() { return lockedStock; }
    public void setLockedStock(int lockedStock) { this.lockedStock = lockedStock; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
