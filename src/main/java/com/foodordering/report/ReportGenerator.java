package com.foodordering.report;

import com.foodordering.model.Order;
import java.util.List;

/**
 * Generates formatted business reports on order data.
 * Provides summaries of total orders, revenue, and status breakdowns.
 * Used as part of the reporting functional requirement.
 */
public class ReportGenerator {

    /**
     * Prints a formatted summary of all orders to the console.
     * Includes order ID, customer, status, and amount per order,
     * followed by aggregate statistics.
     *
     * @param orders List of all orders in the system
     */
    public void generateOrderReport(List<Order> orders) {
        System.out.println("=========================================");
        System.out.println("  REPORT - ORDER SUMMARY");
        System.out.println("=========================================");
        System.out.println(String.format("%-15s %-20s %-15s %-12s",
                "Order ID", "Customer", "Status", "Amount"));
        System.out.println("-------------------------------------------------------------");

        double totalRevenue = 0;
        int delivered = 0, cancelled = 0, active = 0;

        for (Order order : orders) {
            String line = String.format("%-15s %-20s %-15s NPR %,.2f",
                    order.getOrderId(),
                    order.getCustomer().getName(),
                    order.getStatus(),
                    order.getTotalAmount());
            System.out.println(line);

            totalRevenue += order.getTotalAmount();
            switch (order.getStatus()) {
                case "DELIVERED": delivered++; break;
                case "CANCELLED": cancelled++; break;
                default: active++; break;
            }
        }

        System.out.println("-------------------------------------------------------------");
        System.out.println("Total Orders   : " + orders.size());
        System.out.println("Active Orders  : " + active);
        System.out.println("Delivered      : " + delivered);
        System.out.println("Cancelled      : " + cancelled);
        System.out.println("Total Revenue  : NPR " + String.format("%,.2f", totalRevenue));
        System.out.println();
    }
}
