package com.library.command;

public interface Command {
    boolean execute();
    void undo();
    String getDescription();
}
