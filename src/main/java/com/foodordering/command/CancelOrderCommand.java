package com.foodordering.command;

import com.foodordering.facade.OrderFacade;
import com.foodordering.model.User;

/**
 * Command Pattern — Concrete Command.
 * Encapsulates the order cancellation operation.
 * Undo is not supported for cancellations (business rule).
 */
public class CancelOrderCommand implements OrderCommand {
    private OrderFacade facade;
    private String orderId;
    private User requester;

    public CancelOrderCommand(OrderFacade facade, String orderId, User requester) {
        this.facade = facade;
        this.orderId = orderId;
        this.requester = requester;
    }

    @Override
    public void execute() {
        facade.cancelOrder(orderId, requester);
        System.out.println("  Result: Order " + orderId + " cancellation processed.");
    }

    @Override
    public void undo() {
        System.out.println("  Cannot undo cancellation - order " + orderId + " is already cancelled.");
    }

    @Override
    public String getDescription() {
        return "Cancel Order " + orderId;
    }
}
