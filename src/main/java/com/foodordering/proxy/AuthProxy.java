package com.foodordering.proxy;

import com.foodordering.model.Order;
import com.foodordering.model.User;

/**
 * Proxy Pattern — Controls access to sensitive order operations.
 * Restricts report generation to ADMIN users and cancellation to
 * CUSTOMER/ADMIN roles. All requests are logged with user details.
 */
public class AuthProxy implements IOrderService {
    private OrderService realService;
    private User currentUser;

    /**
     * @param currentUser The user on whose behalf operations are performed
     */
    public AuthProxy(User currentUser) {
        this.realService = new OrderService();
        this.currentUser = currentUser;
    }

    @Override
    public void placeOrder(Order order) {
        System.out.println("  [AuthProxy] Access granted to " + currentUser.getName()
                + " (" + currentUser.getRole() + ") for placing order.");
        realService.placeOrder(order);
    }

    @Override
    public void cancelOrder(String orderId) {
        if (!"CUSTOMER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            System.out.println("  [AuthProxy] ACCESS DENIED: " + currentUser.getRole()
                    + " cannot cancel orders.");
            return;
        }
        System.out.println("  [AuthProxy] Access granted to " + currentUser.getName()
                + " (" + currentUser.getRole() + ") for cancellation.");
        realService.cancelOrder(orderId);
    }

    @Override
    public String generateReport(String reportType) {
        if (!"ADMIN".equals(currentUser.getRole())) {
            System.out.println("  [AuthProxy] ACCESS DENIED: Only ADMIN can generate reports.");
            return "Access Denied";
        }
        System.out.println("  [AuthProxy] Access granted to " + currentUser.getName()
                + " (ADMIN) for report generation.");
        return realService.generateReport(reportType);
    }
}
