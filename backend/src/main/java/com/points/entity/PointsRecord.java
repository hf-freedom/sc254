package com.points.entity;

import com.points.enums.PointsSource;

import java.time.LocalDateTime;

public class PointsRecord {
    private String recordId;
    private String userId;
    private int points;
    private PointsSource source;
    private String description;
    private String orderId;
    private LocalDateTime createdAt;
    private boolean expired;

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public PointsSource getSource() { return source; }
    public void setSource(PointsSource source) { this.source = source; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
}
