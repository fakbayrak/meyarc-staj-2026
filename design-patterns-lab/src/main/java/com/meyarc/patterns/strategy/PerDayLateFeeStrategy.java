package com.meyarc.patterns.strategy;

import com.meyarc.patterns.singleton.LibraryConfig;

public class PerDayLateFeeStrategy implements LateFeeStrategy {

    @Override
    public double calculate(int daysLate) {
        return daysLate <= 0 ? 0.0 : daysLate * LibraryConfig.INSTANCE.getPerDayLateFee();
    }
}
