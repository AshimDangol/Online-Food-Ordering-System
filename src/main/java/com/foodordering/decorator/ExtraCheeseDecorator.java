package com.foodordering.decorator;

import com.foodordering.model.MenuItem;

/**
 * Decorator Pattern — Concrete Decorator.
 * Adds extra cheese (NPR 50) to a menu item.
 */
public class ExtraCheeseDecorator extends ItemDecorator {
    public ExtraCheeseDecorator(MenuItem item) {
        super(item);
    }

    @Override
    public String getDescription() {
        return item.getDescription() + " + Extra Cheese";
    }

    @Override
    public double getPrice() {
        return item.getPrice() + 50.0;
    }
}
