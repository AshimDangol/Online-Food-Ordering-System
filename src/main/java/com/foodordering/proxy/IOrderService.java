package com.foodordering.proxy;

import com.foodordering.model.Order;

/**
 * Proxy Pattern — Subject interface.
 * Defines the contract for order operations that the proxy controls access to.
 */
public interface IOrderService {
    /** Places an order. @return true if the order was accepted and stored */
    boolean placeOrder(Order order);
    boolean cancelOrder(String orderId);
    boolean restoreOrder(String orderId, String status);
    String generateReport(String reportType);
}
