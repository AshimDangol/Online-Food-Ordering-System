package com.foodordering.model;

/**
 * Component interface for the Decorator pattern.
 * Defines the contract for menu items that can be dynamically
 * extended with extras (cheese, toppings, drinks).
 */
public interface MenuItem {
    /** @return Human-readable item description including decorations */
    String getDescription();

    /** @return Total price after all decorations are applied */
    double getPrice();

    /** @return Whether this item can currently be ordered (decorators keep the base flag) */
    default boolean isAvailable() {
        return true;
    }
}
