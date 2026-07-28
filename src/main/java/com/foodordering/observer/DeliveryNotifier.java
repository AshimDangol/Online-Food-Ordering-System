package com.foodordering.observer;

import com.foodordering.model.Order;

/**
 * Observer Pattern — Concrete Observer.
 * Sends order status notifications to delivery partners.
 */
public class DeliveryNotifier implements OrderObserver {
    @Override
    public void update(Order order, String message) {
        System.out.println("  [NOTIFICATION to Delivery Partner] " + message);
    }
}
