package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — Order is being prepared in the kitchen.
 * Can transition to: OUT_FOR_DELIVERY or CANCELLED.
 */
public class PreparingState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already confirmed and being prepared.");
    }

    @Override
    public void prepare(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already being prepared.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is out for delivery.");
        order.setState(new OutForDeliveryState());
    }

    @Override
    public void complete(Order order) {
        System.out.println("  Cannot complete - order " + order.getOrderId() + " is not yet delivered.");
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Order " + order.getOrderId() + " cancelled while being prepared.");
        order.setState(new CancelledState());
    }

    @Override
    public String getStatus() { return "PREPARING"; }
}
