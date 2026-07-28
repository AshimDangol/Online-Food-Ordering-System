package com.library.command;

import java.util.Stack;

public class CommandInvoker {
    private final Stack<Command> commandHistory = new Stack<>();

    public boolean executeCommand(Command command) {
        System.out.println("  Executing: " + command.getDescription());
        boolean success = command.execute();
        if (success) {
            commandHistory.push(command);
        }
        return success;
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command command = commandHistory.pop();
            command.undo();
        } else {
            System.out.println("  No commands to undo.");
        }
    }

    public int getHistorySize() {
        return commandHistory.size();
    }
}
