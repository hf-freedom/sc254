package com.points.entity;

import com.points.enums.MemberLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private int availablePoints;
    private int totalPoints;
    private int usedPoints;
    private int owedPoints;
    private MemberLevel currentLevel;
    private LocalDateTime levelExpireTime;
    private List<LocalDateTime> signInDates = new ArrayList<>();
    private boolean inRiskControl;
    private boolean recentAbnormalPoints;
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.currentLevel = MemberLevel.BRONZE;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(int availablePoints) { this.availablePoints = availablePoints; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public int getUsedPoints() { return usedPoints; }
    public void setUsedPoints(int usedPoints) { this.usedPoints = usedPoints; }
    public int getOwedPoints() { return owedPoints; }
    public void setOwedPoints(int owedPoints) { this.owedPoints = owedPoints; }
    public MemberLevel getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(MemberLevel currentLevel) { this.currentLevel = currentLevel; }
    public LocalDateTime getLevelExpireTime() { return levelExpireTime; }
    public void setLevelExpireTime(LocalDateTime levelExpireTime) { this.levelExpireTime = levelExpireTime; }
    public List<LocalDateTime> getSignInDates() { return signInDates; }
    public void setSignInDates(List<LocalDateTime> signInDates) { this.signInDates = signInDates; }
    public boolean isInRiskControl() { return inRiskControl; }
    public void setInRiskControl(boolean inRiskControl) { this.inRiskControl = inRiskControl; }
    public boolean isRecentAbnormalPoints() { return recentAbnormalPoints; }
    public void setRecentAbnormalPoints(boolean recentAbnormalPoints) { this.recentAbnormalPoints = recentAbnormalPoints; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
