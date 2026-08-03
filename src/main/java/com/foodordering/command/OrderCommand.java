package com.foodordering.command;

/**
 * Command Pattern — Command interface.
 * Encapsulates an order operation as an object, enabling parameterization,
 * queuing, logging, and undo support. {@code execute()} returns whether the
 * operation succeeded so the invoker only records successful commands.
 */
public interface OrderCommand {
    /** Executes the command. @return true if the operation succeeded */
    boolean execute();

    /** Reverses the command if supported. @return true if the undo succeeded */
    boolean undo();

    /** @return Human-readable description for logging/history */
    String getDescription();
}
