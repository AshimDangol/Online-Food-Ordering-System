package com.foodordering.decorator;

import com.foodordering.model.MenuItem;

/**
 * Decorator Pattern — Abstract Decorator.
 * Serves as the base class for all item decorators.
 * Wraps a {@link MenuItem} and delegates to it while adding behavior.
 * Subclasses override {@link #getDescription()} and {@link #getPrice()}
 * to extend the wrapped item.
 */
public abstract class ItemDecorator implements MenuItem {
    protected MenuItem item;

    /**
     * @param item The MenuItem being wrapped (may already be decorated)
     */
    public ItemDecorator(MenuItem item) {
        this.item = item;
    }

    @Override
    public abstract String getDescription();

    @Override
    public abstract double getPrice();
}
