package com.foodordering.command;

import java.util.Stack;

/**
 * Command Pattern — Invoker.
 * Executes commands and maintains a history stack for undo support.
 * Decouples the command execution from the caller.
 */
public class CommandInvoker {
    private Stack<OrderCommand> history;

    public CommandInvoker() {
        this.history = new Stack<>();
    }

    /** Executes a command and adds it to the undo history. */
    public void executeCommand(OrderCommand command) {
        System.out.println("  Executing: " + command.getDescription());
        command.execute();
        history.push(command);
    }

    /** Undoes the most recently executed command. */
    public void undoLastCommand() {
        if (!history.isEmpty()) {
            OrderCommand command = history.pop();
            System.out.println("  Undoing: " + command.getDescription());
            command.undo();
        } else {
            System.out.println("  No commands to undo.");
        }
    }

    public int getHistorySize() {
        return history.size();
    }
}
