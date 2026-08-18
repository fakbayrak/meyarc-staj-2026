package com.meyarc.patterns.strategy;

public class BookReturn {

    private final LateFeeStrategy lateFeeStrategy;

    public BookReturn(LateFeeStrategy lateFeeStrategy) {
        this.lateFeeStrategy = lateFeeStrategy;
    }

    public double calculateFee(int daysLate) {
        return lateFeeStrategy.calculate(daysLate);
    }
}
