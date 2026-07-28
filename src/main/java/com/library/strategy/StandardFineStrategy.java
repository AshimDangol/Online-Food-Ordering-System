package com.library.strategy;

import com.library.model.Loan;

public class StandardFineStrategy implements FineCalculationStrategy {
    private static final double STANDARD_RATE = 0.50;

    @Override
    public double calculateFine(Loan loan) {
        long daysOverdue = loan.getDaysOverdue();
        return daysOverdue * STANDARD_RATE;
    }

    @Override
    public String getStrategyName() {
        return "Standard Fine ($" + String.format("%.2f/day)", STANDARD_RATE);
    }
}
