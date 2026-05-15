package com.points.entity;

import java.time.LocalDateTime;

public class ExchangeRecord {
    private String recordId;
    private String userId;
    private String productId;
    private String productName;
    private int pointsUsed;
    private String status;
    private LocalDateTime createdAt;

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getPointsUsed() { return pointsUsed; }
    public void setPointsUsed(int pointsUsed) { this.pointsUsed = pointsUsed; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
