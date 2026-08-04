package com.foodordering.proxy;

import com.foodordering.model.Order;
import com.foodordering.model.User;

/**
 * Proxy Pattern — Controls access to sensitive order operations.
 * Restricts report generation to ADMIN users and cancellation to
 * CUSTOMER/ADMIN roles. All requests are logged with user details.
 */
public class AuthProxy implements IOrderService {
    private final OrderService realService;
    private final User currentUser;

    /**
     * @param currentUser The user on whose behalf operations are performed
     * @param realService Shared OrderService instance for data consistency
     */
    public AuthProxy(User currentUser, OrderService realService) {
        this.realService = realService;
        this.currentUser = currentUser;
    }

    /**
     * Creates an AuthProxy with a new OrderService (for backward compatibility/testing).
     * @param currentUser The user on whose behalf operations are performed
     */
    public AuthProxy(User currentUser) {
        this(currentUser, new OrderService());
    }

    @Override
    public boolean placeOrder(Order order) {
        if (!"CUSTOMER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            System.out.println("  [AuthProxy] ACCESS DENIED: " + currentUser.getRole()
                    + " cannot place orders.");
            return false;
        }
        System.out.println("  [AuthProxy] Access granted to " + currentUser.getName()
                + " (" + currentUser.getRole() + ") for placing order.");
        realService.placeOrder(order);
        return true;
    }

    @Override
    public boolean cancelOrder(String orderId) {
        if (!"CUSTOMER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            System.out.println("  [AuthProxy] ACCESS DENIED: " + currentUser.getRole()
                    + " cannot cancel orders.");
            return false;
        }
        System.out.println("  [AuthProxy] Access granted to " + currentUser.getName()
                + " (" + currentUser.getRole() + ") for cancellation.");
        return realService.cancelOrder(orderId);
    }

    @Override
    public boolean restoreOrder(String orderId, String status) {
        if (!"CUSTOMER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            System.out.println("  [AuthProxy] ACCESS DENIED: " + currentUser.getRole()
                    + " cannot restore orders.");
            return false;
        }
        System.out.println("  [AuthProxy] Access granted to " + currentUser.getName()
                + " (" + currentUser.getRole() + ") for order restoration.");
        return realService.restoreOrder(orderId, status);
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
