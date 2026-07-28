package com.foodordering.model;

/**
 * Represents a menu item with a specific quantity inside an order.
 * Used by the Builder pattern when constructing complex orders.
 */
public class OrderItem {
    private MenuItem item;
    private int quantity;

    /**
     * @param item     The menu item (may be decorated)
     * @param quantity Number of this item ordered
     */
    public OrderItem(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() { return item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /** @return item price * quantity */
    public double getTotalPrice() {
        return item.getPrice() * quantity;
    }

    /** @return Description with quantity, e.g. "Pizza x 2" */
    public String getDescription() {
        return item.getDescription() + " x " + quantity;
    }

    @Override
    public String toString() {
        return String.format("%-30s NPR %,.2f", getDescription(), getTotalPrice());
    }
}
