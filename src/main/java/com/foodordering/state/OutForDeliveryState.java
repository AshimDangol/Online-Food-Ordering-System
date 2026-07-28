package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — Order is with a delivery partner en route.
 * Can transition to: DELIVERED. Cancellation is no longer allowed.
 */
public class OutForDeliveryState implements OrderState {
    @Override
    public void confirm(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already out for delivery.");
    }

    @Override
    public void prepare(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is already out for delivery.");
    }

    @Override
    public void deliver(Order order) {
        System.out.println("  Order " + order.getOrderId() + " is still out for delivery.");
    }

    @Override
    public void complete(Order order) {
        System.out.println("  Order " + order.getOrderId() + " has been delivered!");
        order.setState(new DeliveredState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  Cannot cancel - order " + order.getOrderId() + " is already out for delivery.");
    }

    @Override
    public String getStatus() { return "OUT_FOR_DELIVERY"; }
}
