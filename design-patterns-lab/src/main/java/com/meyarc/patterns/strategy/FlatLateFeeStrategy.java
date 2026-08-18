package com.meyarc.patterns.strategy;

import com.meyarc.patterns.singleton.LibraryConfig;

public class FlatLateFeeStrategy implements LateFeeStrategy {

    @Override
    public double calculate(int daysLate) {
        return daysLate <= 0 ? 0.0 : LibraryConfig.INSTANCE.getFlatLateFee();
    }
}
