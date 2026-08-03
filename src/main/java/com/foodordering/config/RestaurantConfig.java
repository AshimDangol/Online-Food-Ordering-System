package com.foodordering.config;

import com.foodordering.interactive.ConsoleStyle;

import java.util.Arrays;

/**
 * Singleton Pattern — Ensures only one configuration instance exists.
 * Centralizes restaurant-wide settings (name, address, tax rate, delivery fees).
 * Accessed globally via {@link #getInstance()}.
 */
public class RestaurantConfig {
    private static RestaurantConfig instance;

    private final String restaurantName;
    private final String address;
    private final String phone;
    private final String operatingHours;
    private final double taxRate;
    private final double deliveryFeePerKm;

    /** Private constructor prevents external instantiation. */
    private RestaurantConfig() {
        this.restaurantName = "FoodieExpress";
        this.address = "123 Food Street, Kathmandu";
        this.phone = "+977-1-4XXXXXX";
        this.operatingHours = "10:00 AM - 10:00 PM";
        this.taxRate = 0.13;
        this.deliveryFeePerKm = 20.0;
    }

    /**
     * Returns the singleton instance (thread-safe via holder class idiom).
     * @return The shared RestaurantConfig instance
     */
    public static RestaurantConfig getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final RestaurantConfig INSTANCE = new RestaurantConfig();
    }

    public String getRestaurantName() { return restaurantName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getOperatingHours() { return operatingHours; }
    public double getTaxRate() { return taxRate; }
    public double getDeliveryFeePerKm() { return deliveryFeePerKm; }

    /** Displays configuration details in formatted console output. */
    public void display() {
        String header = "SINGLETON \u2014 RESTAURANT CONFIG";
        String[] rows = {
                "Restaurant : " + restaurantName,
                "Address    : " + address,
                "Phone      : " + phone,
                "Hours      : " + operatingHours,
                "Tax Rate   : " + (taxRate * 100) + "%",
                "Delivery   : " + "NPR " + String.format("%,.2f", deliveryFeePerKm) + "/km"
        };
        int inner = Math.max(header.length() + 2,
                Arrays.stream(rows).mapToInt(String::length).max().orElse(0));
        String bar = "\u2500".repeat(inner + 2);
        String border = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u250C" + bar + "\u2510");
        String mid = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u251C" + bar + "\u2524");
        String bottom = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2514" + bar + "\u2518");

        System.out.println();
        System.out.println(border);
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2502 ")
                + ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN,
                        String.format("%-" + inner + "s", header)))
                + ConsoleStyle.paint(ConsoleStyle.CYAN, " \u2502"));
        System.out.println(mid);
        for (String row : rows) {
            String[] parts = row.split(": ", 2);
            System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2502 ")
                    + ConsoleStyle.paint(ConsoleStyle.DIM,
                            String.format("%-" + inner + "s",
                                    parts[0] + ": " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_WHITE, parts[1])))
                    + ConsoleStyle.paint(ConsoleStyle.CYAN, " \u2502"));
        }
        System.out.println(bottom);
    }
}
