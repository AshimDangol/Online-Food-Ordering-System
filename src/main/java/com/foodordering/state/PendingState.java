package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — Initial state.
 * Order has been placed but not yet confirmed.
 * Can transition to: CONFIRMED or CANCELLED.
 */
public class PendingState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("  Order " + order.getOrderId() + " confirmed.");
        order.setState(new ConfirmedState());
    }

    @Override
    public void prepare(Order order) {
        System.out.println("  Cannot prepare - order " + order.getOrderId() + " is still pending.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("  Cannot deliver - order " + order.getOrderId() + " is still pending.");
    }

    @Override
    public void complete(Order order) {
        System.out.println("  Cannot complete - order " + order.getOrderId() + " is still pending.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Order " + order.getOrderId() + " cancelled.");
        order.setState(new CancelledState());
    }

    @Override
    public String getStatus() { return "PENDING"; }
}
