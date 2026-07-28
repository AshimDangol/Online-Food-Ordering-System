package com.foodordering.strategy;

/**
 * Strategy Pattern — Strategy interface.
 * Defines a family of interchangeable delivery algorithms.
 * Each concrete strategy calculates charges and estimated times differently.
 */
public interface DeliveryStrategy {
    /** @return Human-readable strategy name */
    String getStrategyName();

    /**
     * Calculates the delivery charge based on distance.
     * @param distanceKm Distance from restaurant to customer
     * @return Delivery charge in NPR
     */
    double calculateCharge(double distanceKm);

    /** @return Estimated delivery time as a string */
    String getEstimatedTime();
}
