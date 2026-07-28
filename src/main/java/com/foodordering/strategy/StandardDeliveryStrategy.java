package com.foodordering.strategy;

import com.foodordering.config.RestaurantConfig;

/**
 * Strategy Pattern — Concrete Strategy.
 * Standard delivery: base rate per km with 30-45 min estimate.
 */
public class StandardDeliveryStrategy implements DeliveryStrategy {
    @Override
    public String getStrategyName() {
        return "Standard Delivery";
    }

    @Override
    public double calculateCharge(double distanceKm) {
        return RestaurantConfig.getInstance().getDeliveryFeePerKm() * distanceKm;
    }

    @Override
    public String getEstimatedTime() {
        return "30-45 minutes";
    }
}
