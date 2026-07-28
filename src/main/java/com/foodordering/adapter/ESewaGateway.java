package com.foodordering.adapter;

/**
 * Adapter Pattern — Adaptee.
 * eSewa's native payment API with its own interface.
 * The {@link PaymentAdapter} wraps this to match {@link PaymentGateway}.
 */
public class ESewaGateway {
    public boolean eSewaPay(double amount) {
        System.out.println("  [eSewa] Processing NPR " + String.format("%,.2f", amount) + "...");
        return true;
    }

    public String getServiceName() {
        return "eSewa";
    }
}
