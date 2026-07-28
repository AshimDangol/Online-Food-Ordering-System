package com.foodordering.adapter;

/**
 * Adapter Pattern — Adaptee.
 * Khalti's native payment API with its own interface.
 * The {@link PaymentAdapter} wraps this to match {@link PaymentGateway}.
 */
public class KhaltiGateway {
    public boolean khaltiPay(double amountNpr) {
        System.out.println("  [Khalti] Processing NPR " + String.format("%,.2f", amountNpr) + "...");
        return true;
    }

    public String getServiceName() {
        return "Khalti";
    }
}
