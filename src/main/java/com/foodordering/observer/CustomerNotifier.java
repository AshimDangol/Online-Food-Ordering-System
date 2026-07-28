package com.foodordering.observer;

import com.foodordering.model.Order;

/**
 * Observer Pattern — Concrete Observer.
 * Sends order status notifications to the customer.
 */
public class CustomerNotifier implements OrderObserver {
    @Override
    public void update(Order order, String message) {
        System.out.println("  [NOTIFICATION to " + order.getCustomer().getName() + "] " + message);
    }
}
