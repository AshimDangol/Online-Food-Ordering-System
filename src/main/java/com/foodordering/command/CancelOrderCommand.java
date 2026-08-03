package com.foodordering.command;

import com.foodordering.facade.OrderFacade;
import com.foodordering.interactive.ConsoleStyle;
import com.foodordering.model.User;

/**
 * Command Pattern — Concrete Command.
 * Encapsulates the order cancellation operation.
 * Undo restores the order to its previous lifecycle state.
 */
public class CancelOrderCommand implements OrderCommand {
    private final OrderFacade facade;
    private final String orderId;
    private final User requester;
    private final String previousStatus;

    public CancelOrderCommand(OrderFacade facade, String orderId, User requester, String previousStatus) {
        this.facade = facade;
        this.orderId = orderId;
        this.requester = requester;
        this.previousStatus = previousStatus;
    }

    @Override
    public void execute() {
        facade.cancelOrder(orderId, requester);
        System.out.println(ConsoleStyle.paint(ConsoleStyle.GREEN,
                "  Result: Order " + orderId + " cancellation processed."));
    }

    @Override
    public void undo() {
        facade.restoreOrder(orderId, previousStatus, requester);
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN,
                "  Undo: Order " + orderId + " restored to " + previousStatus + "."));
    }

    @Override
    public String getDescription() {
        return "Cancel Order " + orderId;
    }
}
