package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — Terminal state.
 * Order has been successfully delivered. No further transitions allowed.
 */
public class DeliveredState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already delivered.");
    }

    @Override
    public void prepare(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already delivered.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already delivered.");
    }

    @Override
    public void complete(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already delivered and completed.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Cannot cancel - order " + order.getOrderId() + " is already delivered.");
    }

    @Override
    public String getStatus() { return "DELIVERED"; }
}
