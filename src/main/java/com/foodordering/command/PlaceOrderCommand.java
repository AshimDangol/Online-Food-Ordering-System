package com.foodordering.command;

import com.foodordering.facade.OrderFacade;
import com.foodordering.model.Customer;
import com.foodordering.model.MenuItem;
import com.foodordering.observer.OrderObserver;
import com.foodordering.strategy.DeliveryStrategy;

import java.util.List;

/**
 * Command Pattern — Concrete Command.
 * Encapsulates the full order placement workflow.
 * Stores the generated order ID for undo support (cancels order).
 */
public class PlaceOrderCommand implements OrderCommand {
    private OrderFacade facade;
    private Customer customer;
    private List<MenuItem> items;
    private List<Integer> quantities;
    private DeliveryStrategy strategy;
    private double distanceKm;
    private String paymentMethod;
    private List<OrderObserver> observers;
    private String orderId;

    public PlaceOrderCommand(OrderFacade facade, Customer customer,
                              List<MenuItem> items, List<Integer> quantities,
                              DeliveryStrategy strategy, double distanceKm,
                              String paymentMethod, List<OrderObserver> observers) {
        this.facade = facade;
        this.customer = customer;
        this.items = items;
        this.quantities = quantities;
        this.strategy = strategy;
        this.distanceKm = distanceKm;
        this.paymentMethod = paymentMethod;
        this.observers = observers;
    }

    @Override
    public void execute() {
        orderId = facade.placeOrder(customer, items, quantities, strategy, distanceKm,
                                    paymentMethod, observers);
        System.out.println("  Result: Order placed successfully! ID: " + orderId);
    }

    @Override
    public void undo() {
        if (orderId != null) {
            System.out.println("  Undo: Cancelling order " + orderId);
            facade.cancelOrder(orderId, customer);
        }
    }

    @Override
    public String getDescription() {
        return "Place Order for " + customer.getName();
    }
}
