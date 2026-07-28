package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — Order has been confirmed by the restaurant.
 * Can transition to: PREPARING or CANCELLED.
 */
public class ConfirmedState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already confirmed.");
    }

    @Override
    public void prepare(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is now being prepared.");
        order.setState(new PreparingState());
    }

    @Override
    public void deliver(Order order) {
        System.out.println("  Cannot deliver - order " + order.getOrderId() + " is not yet prepared.");
    }

    @Override
    public void complete(Order order) {
        System.out.println("  Cannot complete - order " + order.getOrderId() + " is not yet prepared.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Order " + order.getOrderId() + " cancelled after confirmation.");
        order.setState(new CancelledState());
    }

    @Override
    public String getStatus() { return "CONFIRMED"; }
}
