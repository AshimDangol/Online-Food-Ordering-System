package com.foodordering.model;

/**
 * ConcreteComponent in the Decorator pattern.
 * Represents a basic menu item with no extras.
 * Serves as the base that decorators wrap around.
 */
public class BaseMenuItem implements MenuItem {
    private String name;
    private double price;

    /**
     * @param name  Item name (e.g. "Margherita Pizza")
     * @param price Base price in NPR
     */
    public BaseMenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String getDescription() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }
}
