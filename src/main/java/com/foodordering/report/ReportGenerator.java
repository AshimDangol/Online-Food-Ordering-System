package com.foodordering.report;

import com.foodordering.interactive.ConsoleStyle;
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
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u250C" + "\u2500".repeat(32) + "\u2510"));
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2502 ")
                + ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN, "REPORT \u2014 ORDER SUMMARY"))
                + ConsoleStyle.paint(ConsoleStyle.CYAN, " \u2502"));
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2514" + "\u2500".repeat(32) + "\u2518"));

        System.out.println(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN,
                String.format("  %-15s %-20s %-15s %-12s",
                        "Order ID", "Customer", "Status", "Amount")));
        System.out.println(ConsoleStyle.paint(ConsoleStyle.DIM, "  " + "\u2500".repeat(63)));

        double totalRevenue = 0;
        int delivered = 0, cancelled = 0, active = 0;

        for (Order order : orders) {
            String badgeCell = ConsoleStyle.paint(ConsoleStyle.statusColor(order.getStatus()),
                    String.format("%-15s", ConsoleStyle.statusSymbol(order.getStatus())));
            String line = String.format("  %-15s %-20s %s NPR %,.2f",
                    order.getOrderId(),
                    order.getCustomer().getName(),
                    badgeCell,
                    order.getTotalAmount());
            System.out.println(line);

            totalRevenue += order.getTotalAmount();
            switch (order.getStatus()) {
                case "DELIVERED": delivered++; break;
                case "CANCELLED": cancelled++; break;
                default: active++; break;
            }
        }

        System.out.println(ConsoleStyle.paint(ConsoleStyle.DIM, "  " + "\u2500".repeat(63)));
        System.out.println("  Total Orders   : " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_WHITE, String.valueOf(orders.size())));
        System.out.println("  Active Orders  : " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_WHITE, String.valueOf(active)));
        System.out.println("  Delivered      : " + ConsoleStyle.paint(ConsoleStyle.GREEN, String.valueOf(delivered)));
        System.out.println("  Cancelled      : " + ConsoleStyle.paint(ConsoleStyle.RED, String.valueOf(cancelled)));
        System.out.println("  Total Revenue  : " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_YELLOW,
                "NPR " + String.format("%,.2f", totalRevenue)));
        System.out.println();
    }
}
