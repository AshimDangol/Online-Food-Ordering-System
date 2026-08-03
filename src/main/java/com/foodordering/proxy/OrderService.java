package com.foodordering.proxy;

import com.foodordering.db.NotificationDAO;
import com.foodordering.db.OrderDAO;
import com.foodordering.model.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxy Pattern — RealSubject.
 * The actual order management logic that the proxy wraps.
 *
 * <p>PostgreSQL is the single source of truth: cancellation, restoration,
 * and reporting always read the freshest copy of an order from the database
 * (the session cache is only a fallback for orders that failed to persist).
 * The state machine is still applied before any status change is written,
 * so invalid transitions (e.g. cancelling an out-for-delivery order) are
 * rejected and leave the database unchanged.
 */
public class OrderService implements IOrderService {
    private static final Map<String, Order> orderStore = new HashMap<>();

    @Override
    public void placeOrder(Order order) {
        orderStore.put(order.getOrderId(), order);
        System.out.println("  Order stored in system: " + order.getOrderId());
    }

    @Override
    public boolean cancelOrder(String orderId) {
        Order order = resolve(orderId);
        if (order == null) {
            System.out.println("  Order " + orderId + " not found in system store.");
            return false;
        }
        order.cancel();
        if ("CANCELLED".equals(order.getStatus())) {
            orderStore.put(orderId, order);
            // Only persist when the order exists in the database (orders that
            // never persisted have no row and no notifications to attach).
            if (new OrderDAO().updateStatus(orderId, "CANCELLED")) {
                new NotificationDAO().saveNotification(orderId, order.getCustomer().getName(),
                        "Order " + orderId + " cancelled.");
            }
            System.out.println("  Order " + orderId + " cancelled in system.");
            return true;
        }
        return false;
    }

    @Override
    public boolean restoreOrder(String orderId, String status) {
        Order order = resolve(orderId);
        if (order == null) {
            System.out.println("  Order " + orderId + " not found in system store.");
            return false;
        }
        order.setState(OrderDAO.fromStatus(status));
        orderStore.put(orderId, order);
        if (new OrderDAO().updateStatus(orderId, status)) {
            new NotificationDAO().saveNotification(orderId, order.getCustomer().getName(),
                    "Order " + orderId + " restored to " + status + ".");
        }
        System.out.println("  Order " + orderId + " restored to state: " + order.getStatus());
        return true;
    }

    /**
     * Resolves an order for an operation: the database row is authoritative;
     * the session store is used only when the order is not persisted yet.
     */
    private Order resolve(String orderId) {
        Order dbOrder = new OrderDAO().findByOrderId(orderId);
        if (dbOrder != null) return dbOrder;
        return orderStore.get(orderId);
    }

    @Override
    public String generateReport(String reportType) {
        List<Order> orders = new OrderDAO().findAll();
        long total = orders.size();
        long active = orders.stream()
                .filter(o -> !"DELIVERED".equals(o.getStatus()) && !"CANCELLED".equals(o.getStatus()))
                .count();
        long delivered = orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus())).count();
        long cancelled = orders.stream()
                .filter(o -> "CANCELLED".equals(o.getStatus())).count();

        StringBuilder sb = new StringBuilder("Order Summary Report:\n");
        sb.append("  Total Orders: ").append(total).append("\n");
        sb.append("  Active Orders: ").append(active).append("\n");
        sb.append("  Delivered: ").append(delivered).append("\n");
        sb.append("  Cancelled: ").append(cancelled);
        if (!"SUMMARY".equalsIgnoreCase(reportType)) {
            sb.append("\nDetailed Orders:\n");
            for (Order o : orders) {
                sb.append("  ").append(o.getOrderId())
                        .append(" | ").append(o.getCustomer().getName())
                        .append(" | ").append(o.getStatus())
                        .append(" | NPR ").append(String.format("%,.2f", o.getTotalAmount()))
                        .append("\n");
            }
        }
        return sb.toString();
    }
}
