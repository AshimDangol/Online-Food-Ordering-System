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
    public boolean execute() {
        boolean cancelled = facade.cancelOrder(orderId, requester);
        if (cancelled) {
            System.out.println(ConsoleStyle.paint(ConsoleStyle.GREEN,
                    "  Result: Order " + orderId + " cancellation processed."));
        } else {
            System.out.println(ConsoleStyle.paint(ConsoleStyle.RED,
                    "  Result: Order " + orderId + " could not be cancelled \u2014 state machine rejected it."));
        }
        return cancelled;
    }

    @Override
    public boolean undo() {
        boolean restored = facade.restoreOrder(orderId, previousStatus, requester);
        if (restored) {
            System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN,
                    "  Undo: Order " + orderId + " restored to " + previousStatus + "."));
        } else {
            System.out.println(ConsoleStyle.paint(ConsoleStyle.RED,
                    "  Undo: Order " + orderId + " could not be restored."));
        }
        return restored;
    }

    @Override
    public String getDescription() {
        return "Cancel Order " + orderId;
    }
}
