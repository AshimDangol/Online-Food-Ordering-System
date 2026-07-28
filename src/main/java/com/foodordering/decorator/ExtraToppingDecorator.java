package com.foodordering.decorator;

import com.foodordering.model.MenuItem;

/**
 * Decorator Pattern — Concrete Decorator.
 * Adds extra toppings (NPR 80) to a menu item.
 */
public class ExtraToppingDecorator extends ItemDecorator {
    public ExtraToppingDecorator(MenuItem item) {
        super(item);
    }

    @Override
    public String getDescription() {
        return item.getDescription() + " + Extra Toppings";
    }

    @Override
    public double getPrice() {
        return item.getPrice() + 80.0;
    }
}
