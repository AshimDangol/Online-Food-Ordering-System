package com.foodordering;

import com.foodordering.interactive.InteractiveMenu;

/**
 * Entry point for the Online Food Ordering System.
 * Launches the interactive console application which demonstrates
 * 11 GoF Design Patterns with H2 database persistence.
 */
public class Main {
    public static void main(String[] args) {
        InteractiveMenu menu = new InteractiveMenu();
        menu.start();
    }
}
