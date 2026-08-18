package com.meyarc.patterns.strategy;

public interface LateFeeStrategy {
    double calculate(int daysLate);
}
