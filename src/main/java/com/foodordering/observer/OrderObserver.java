package com.foodordering.observer;

import com.foodordering.model.Order;

/**
 * Observer Pattern — Observer interface.
 * Defines the update method called when an Order's state changes.
 * Implementations send notifications to specific parties (customer,
 * kitchen, delivery partner).
 */
public interface OrderObserver {
    /**
     * Called by the Order (Subject) when its state changes.
     * @param order   The order that changed
     * @param message Description of what changed
     */
    void update(Order order, String message);
}
