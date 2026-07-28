package com.foodordering.proxy;

import com.foodordering.model.Order;

/**
 * Proxy Pattern — Subject interface.
 * Defines the contract for order operations that the proxy controls access to.
 */
public interface IOrderService {
    void placeOrder(Order order);
    void cancelOrder(String orderId);
    String generateReport(String reportType);
}
