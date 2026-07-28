package com.library.strategy;

import com.library.model.Loan;

public interface FineCalculationStrategy {
    double calculateFine(Loan loan);
    String getStrategyName();
}
