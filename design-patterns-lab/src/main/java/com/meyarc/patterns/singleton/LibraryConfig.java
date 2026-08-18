package com.meyarc.patterns.singleton;

public enum LibraryConfig {

    INSTANCE;

    private final int maxBorrowDays = 14;
    private final double perDayLateFee = 2.5;
    private final double flatLateFee = 10.0;

    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }

    public double getPerDayLateFee() {
        return perDayLateFee;
    }

    public double getFlatLateFee() {
        return flatLateFee;
    }
}
