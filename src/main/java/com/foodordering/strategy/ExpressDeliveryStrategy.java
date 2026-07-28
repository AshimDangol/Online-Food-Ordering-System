package com.foodordering.strategy;

import com.foodordering.config.RestaurantConfig;

/**
 * Strategy Pattern — Concrete Strategy.
 * Express delivery: per-km rate + NPR 100 premium, faster (15-20 min).
 */
public class ExpressDeliveryStrategy implements DeliveryStrategy {
    @Override
    public String getStrategyName() {
        return "Express Delivery";
    }

    @Override
    public double calculateCharge(double distanceKm) {
        return (RestaurantConfig.getInstance().getDeliveryFeePerKm() * distanceKm) + 100.0;
    }

    @Override
    public String getEstimatedTime() {
        return "15-20 minutes";
    }
}
