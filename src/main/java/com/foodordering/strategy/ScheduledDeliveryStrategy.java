package com.foodordering.strategy;

/**
 * Strategy Pattern — Concrete Strategy.
 * Scheduled delivery: free of charge for pre-scheduled time slots.
 */
public class ScheduledDeliveryStrategy implements DeliveryStrategy {
    @Override
    public String getStrategyName() {
        return "Scheduled Delivery";
    }

    @Override
    public double calculateCharge(double distanceKm) {
        return 0.0;
    }

    @Override
    public String getEstimatedTime() {
        return "Scheduled time slot (Free)";
    }
}
