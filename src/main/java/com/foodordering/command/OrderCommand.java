package com.foodordering.command;

/**
 * Command Pattern — Command interface.
 * Encapsulates an order operation as an object, enabling parameterization,
 * queuing, logging, and undo support.
 */
public interface OrderCommand {
    /** Executes the command. */
    void execute();

    /** Reverses the command if supported. */
    void undo();

    /** @return Human-readable description for logging/history */
    String getDescription();
}
