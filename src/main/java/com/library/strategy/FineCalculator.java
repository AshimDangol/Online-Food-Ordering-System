package com.library.strategy;

import com.library.model.Loan;

public class FineCalculator {
    private FineCalculationStrategy strategy;

    public void setStrategy(FineCalculationStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFine(Loan loan) {
        return strategy.calculateFine(loan);
    }

    public String getStrategyName() {
        return strategy.getStrategyName();
    }
}
