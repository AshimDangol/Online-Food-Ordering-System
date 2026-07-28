package com.foodordering.proxy;

import com.foodordering.model.Order;

import java.util.HashMap;
import java.util.Map;

/**
 * Proxy Pattern — RealSubject.
 * The actual order management logic that the proxy wraps.
 * Manages an in-memory store of orders and performs business operations.
 */
public class OrderService implements IOrderService {
    private Map<String, Order> orderStore;

    public OrderService() {
        this.orderStore = new HashMap<>();
    }

    @Override
    public void placeOrder(Order order) {
        orderStore.put(order.getOrderId(), order);
        System.out.println("  Order stored in system: " + order.getOrderId());
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = orderStore.get(orderId);
        if (order != null) {
            order.cancel();
            System.out.println("  Order " + orderId + " cancelled in system.");
        } else {
            System.out.println("  Order " + orderId + " not found.");
        }
    }

    @Override
    public String generateReport(String reportType) {
        long total = orderStore.size();
        long active = orderStore.values().stream()
                .filter(o -> !"DELIVERED".equals(o.getStatus()) && !"CANCELLED".equals(o.getStatus()))
                .count();
        long delivered = orderStore.values().stream()
                .filter(o -> "DELIVERED".equals(o.getStatus())).count();
        long cancelled = orderStore.values().stream()
                .filter(o -> "CANCELLED".equals(o.getStatus())).count();

        return "Order Summary Report:\n" +
               "  Total Orders: " + total + "\n" +
               "  Active Orders: " + active + "\n" +
               "  Delivered: " + delivered + "\n" +
               "  Cancelled: " + cancelled;
    }
}
