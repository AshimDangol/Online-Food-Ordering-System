package com.foodordering.facade;

import com.foodordering.adapter.PaymentAdapter;
import com.foodordering.adapter.PaymentGateway;
import com.foodordering.builder.OrderBuilder;
import com.foodordering.model.*;
import com.foodordering.observer.OrderObserver;
import com.foodordering.proxy.AuthProxy;
import com.foodordering.proxy.IOrderService;
import com.foodordering.proxy.OrderService;
import com.foodordering.strategy.DeliveryStrategy;

import java.util.*;

/**
 * Facade Pattern — Provides a simplified interface to the complex
 * order-processing subsystem. Hides the details of builder construction,
 * payment adaptation, proxy authorization, and state management.
 * Clients call {@link #placeOrder} or {@link #cancelOrder} without
 * needing to understand the underlying pattern interactions.
 */
public class OrderFacade {
    private final IOrderService orderService;
    private final OrderService sharedOrderService;
    private final Map<String, Order> orders;

    /**
     * @param currentUser The user performing operations (used for authorization)
     */
    public OrderFacade(User currentUser) {
        this.sharedOrderService = new OrderService();
        this.orderService = new AuthProxy(currentUser, sharedOrderService);
        this.orders = new HashMap<>();
    }

    /**
     * Places a complete order: builds it, processes payment via adapter,
     * persists via proxy, and returns the generated order ID.
     *
     * @return The generated order ID, or null if payment failed
     */
    public String placeOrder(Customer customer, List<MenuItem> menuItems, List<Integer> quantities,
                              DeliveryStrategy strategy, double distanceKm,
                              String paymentMethod, List<OrderObserver> observers) {

        OrderBuilder builder = new OrderBuilder(customer);
        for (OrderObserver observer : observers) {
            builder.addObserver(observer);
        }

        for (int i = 0; i < menuItems.size(); i++) {
            builder.addItem(menuItems.get(i), quantities.get(i));
        }

        builder.setDeliveryStrategy(strategy, distanceKm);
        builder.setPaymentMethod(paymentMethod);

        Order order = builder.build();

        // Adapter pattern: unify payment gateways
        PaymentGateway payment = new PaymentAdapter(paymentMethod);
        System.out.println();
        System.out.println("  Payment Method: " + payment.getGatewayName());
        System.out.println("  Amount: NPR " + String.format("%,.2f", order.getTotalAmount()));
        boolean paid = payment.processPayment(order.getTotalAmount());

        if (paid) {
            // Proxy pattern: authorize and persist. If the proxy denies the
            // request (e.g. a delivery partner attempting to place an order),
            // no order is registered and no ID is returned.
            boolean placed = orderService.placeOrder(order);
            if (placed) {
                orders.put(order.getOrderId(), order);
                return order.getOrderId();
            }
        }
        return null;
    }

    /** Cancels an order via the shared proxy for access control. */
    public boolean cancelOrder(String orderId, User requester) {
        AuthProxy proxy = new AuthProxy(requester, sharedOrderService);
        boolean cancelled = proxy.cancelOrder(orderId);
        // Also remove from local cache if it was there
        orders.remove(orderId);
        return cancelled;
    }

    /** Restores an order to a previous state (used by Command undo). */
    public boolean restoreOrder(String orderId, String status, User requester) {
        AuthProxy proxy = new AuthProxy(requester, sharedOrderService);
        boolean restored = proxy.restoreOrder(orderId, status);
        orders.remove(orderId);
        return restored;
    }

    /** Returns the current status of an order. */
    public String trackOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null) {
            return "Order " + orderId + " is " + order.getStatus();
        }
        return "Order not found.";
    }

    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
}