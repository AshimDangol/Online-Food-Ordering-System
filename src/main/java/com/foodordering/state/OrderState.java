package com.foodordering.state;

import com.foodordering.model.Order;

/**
 * State Pattern — State interface.
 * Defines the behavior for each order lifecycle state.
 * Each state implements the valid transitions and rejects invalid ones.
 * States: PENDING → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED
 * Cancellation is allowed from PENDING, CONFIRMED, and PREPARING states.
 */
public interface OrderState {
    void confirm(Order order);
    void prepare(Order order);
    void deliver(Order order);
    void complete(Order order);
    void cancel(Order order);
    String getStatus();
}
