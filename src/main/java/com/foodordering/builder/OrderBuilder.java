package com.foodordering.builder;

import com.foodordering.model.*;
import com.foodordering.observer.OrderObserver;
import com.foodordering.strategy.DeliveryStrategy;

import java.util.UUID;

/**
 * Builder Pattern — Constructs complex Order objects step by step.
 * Allows chaining of item additions, delivery strategy, payment method,
 * and observer registration before calling {@link #build()}.
 * Automatically calculates subtotal, tax (13%), and delivery charges.
 */
public class OrderBuilder {
    private Order order;
    private double deliveryCharge;
    private double taxAmount;

    /**
     * Starts building an order for the given customer.
     * @param customer The customer placing the order
     */
    public OrderBuilder(Customer customer) {
        this.order = new Order(generateOrderId(), customer);
    }

    /** Generates a unique order ID with ORD- prefix. */
    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /** Adds a menu item with the given quantity to the order. */
    public OrderBuilder addItem(MenuItem item, int quantity) {
        order.addItem(new OrderItem(item, quantity));
        return this;
    }

    /** Sets the delivery strategy and calculates the charge based on distance. */
    public OrderBuilder setDeliveryStrategy(DeliveryStrategy strategy, double distanceKm) {
        order.setDeliveryStrategy(strategy);
        this.deliveryCharge = strategy.calculateCharge(distanceKm);
        return this;
    }

    /** Sets the payment method (e.g., KHALTI, ESEWA, PAYPAL). */
    public OrderBuilder setPaymentMethod(String paymentMethod) {
        order.setPaymentMethod(paymentMethod);
        return this;
    }

    /** Registers an observer to receive order status notifications. */
    public OrderBuilder addObserver(OrderObserver observer) {
        order.attach(observer);
        return this;
    }

    /**
     * Finalizes the order by computing subtotal, tax, delivery charge, and total.
     * @return The fully constructed Order
     */
    public Order build() {
        double subtotal = order.calculateTotal();
        this.taxAmount = subtotal * 0.13;
        double total = subtotal + taxAmount + deliveryCharge;
        order.setTotalAmount(total);
        return order;
    }

    public double getDeliveryCharge() { return deliveryCharge; }
    public double getTaxAmount() { return taxAmount; }
}
