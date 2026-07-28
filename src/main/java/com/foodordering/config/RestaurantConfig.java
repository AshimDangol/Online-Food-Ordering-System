package com.foodordering.config;

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
     * Returns the singleton instance, creating it lazily if needed.
     * @return The shared RestaurantConfig instance
     */
    public static RestaurantConfig getInstance() {
        if (instance == null) {
            instance = new RestaurantConfig();
        }
        return instance;
    }

    public String getRestaurantName() { return restaurantName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getOperatingHours() { return operatingHours; }
    public double getTaxRate() { return taxRate; }
    public double getDeliveryFeePerKm() { return deliveryFeePerKm; }

    /** Displays configuration details in formatted console output. */
    public void display() {
        System.out.println("=========================================");
        System.out.println("  SINGLETON PATTERN - RESTAURANT CONFIG");
        System.out.println("=========================================");
        System.out.println("Restaurant   : " + restaurantName);
        System.out.println("Address      : " + address);
        System.out.println("Phone        : " + phone);
        System.out.println("Hours        : " + operatingHours);
        System.out.println("Tax Rate     : " + (taxRate * 100) + "%");
        System.out.println("Delivery Fee : NPR " + String.format("%,.2f", deliveryFeePerKm) + " per km");
        System.out.println();
    }
}
