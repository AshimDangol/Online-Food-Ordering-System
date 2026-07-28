package com.foodordering.decorator;

import com.foodordering.model.MenuItem;

/**
 * Decorator Pattern — Concrete Decorator.
 * Adds a named drink (NPR 100) to a menu item.
 */
public class DrinkDecorator extends ItemDecorator {
    private String drinkName;

    /**
     * @param item      The item to decorate
     * @param drinkName Name of the drink (e.g., "Coke", "Fanta")
     */
    public DrinkDecorator(MenuItem item, String drinkName) {
        super(item);
        this.drinkName = drinkName;
    }

    @Override
    public String getDescription() {
        return item.getDescription() + " + " + drinkName;
    }

    @Override
    public double getPrice() {
        return item.getPrice() + 100.0;
    }
}
