package com.library.strategy;

import com.library.model.Loan;

public class StudentDiscountStrategy implements FineCalculationStrategy {
    private static final double STANDARD_RATE = 0.50;
    private static final double DISCOUNT_RATE = 0.5;

    @Override
    public double calculateFine(Loan loan) {
        long daysOverdue = loan.getDaysOverdue();
        return daysOverdue * STANDARD_RATE * (1 - DISCOUNT_RATE);
    }

    @Override
    public String getStrategyName() {
        double discounted = STANDARD_RATE * (1 - DISCOUNT_RATE);
        return "Student Discount ($" + String.format("%.2f/day)", discounted);
    }
}
