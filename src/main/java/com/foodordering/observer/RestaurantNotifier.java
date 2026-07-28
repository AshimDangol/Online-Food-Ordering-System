package com.foodordering.observer;

import com.foodordering.model.Order;

/**
 * Observer Pattern — Concrete Observer.
 * Sends order status notifications to the restaurant kitchen.
 */
public class RestaurantNotifier implements OrderObserver {
    @Override
    public void update(Order order, String message) {
        System.out.println("  [NOTIFICATION to Kitchen] " + message);
    }
}
