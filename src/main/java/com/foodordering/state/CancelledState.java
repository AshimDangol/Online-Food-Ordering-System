package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — Terminal state.
 * Order has been cancelled. No further transitions allowed.
 */
public class CancelledState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("  Cannot confirm - order " + order.getOrderId() + " is cancelled.");
    }

    @Override
    public void prepare(Order order) {
        System.out.println("  Cannot prepare - order " + order.getOrderId() + " is cancelled.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("  Cannot deliver - order " + order.getOrderId() + " is cancelled.");
    }

    @Override
    public void complete(Order order) {
        System.out.println("  Cannot complete - order " + order.getOrderId() + " is cancelled.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already cancelled.");
    }

    @Override
    public String getStatus() { return "CANCELLED"; }
}
