package com.points.enums;

public enum MemberLevel {
    BRONZE("青铜", 0, 0.95, 0),
    SILVER("白银", 1000, 0.90, 30),
    GOLD("黄金", 5000, 0.85, 60),
    PLATINUM("铂金", 20000, 0.80, 90),
    DIAMOND("钻石", 50000, 0.75, 180);

    private final String name;
    private final int requiredPoints;
    private final double discount;
    private final int validityDays;

    MemberLevel(String name, int requiredPoints, double discount, int validityDays) {
        this.name = name;
        this.requiredPoints = requiredPoints;
        this.discount = discount;
        this.validityDays = validityDays;
    }

    public String getName() { return name; }
    public int getRequiredPoints() { return requiredPoints; }
    public double getDiscount() { return discount; }
    public int getValidityDays() { return validityDays; }

    public static MemberLevel getLevelByPoints(int totalPoints) {
        MemberLevel[] levels = values();
        for (int i = levels.length - 1; i >= 0; i--) {
            if (totalPoints >= levels[i].requiredPoints) {
                return levels[i];
            }
        }
        return BRONZE;
    }
}
