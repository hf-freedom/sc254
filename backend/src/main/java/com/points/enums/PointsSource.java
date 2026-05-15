package com.points.enums;

public enum PointsSource {
    CONSUMPTION("消费", 1000, 50000),
    SIGN_IN("签到", 50, 1000),
    REVIEW("评价", 200, 5000);

    private final String description;
    private final int dailyLimit;
    private final int totalLimit;

    PointsSource(String description, int dailyLimit, int totalLimit) {
        this.description = description;
        this.dailyLimit = dailyLimit;
        this.totalLimit = totalLimit;
    }

    public String getDescription() { return description; }
    public int getDailyLimit() { return dailyLimit; }
    public int getTotalLimit() { return totalLimit; }
}
