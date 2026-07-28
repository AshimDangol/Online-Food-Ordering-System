package com.library.strategy;

import com.library.model.Loan;

public class TeacherNoFineStrategy implements FineCalculationStrategy {
    @Override
    public double calculateFine(Loan loan) {
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "No Fine for Teachers ($0.00/day)";
    }
}
