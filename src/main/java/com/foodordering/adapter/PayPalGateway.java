package com.foodordering.adapter;

/**
 * Adapter Pattern — Adaptee.
 * PayPal's native payment API (operates in USD).
 * The {@link PaymentAdapter} wraps this, converting NPR to USD internally.
 */
public class PayPalGateway {
    public boolean paypalPay(double amountUsd) {
        System.out.println("  [PayPal] Processing $" + String.format("%,.2f", amountUsd) + "...");
        return true;
    }

    public String getServiceName() {
        return "PayPal";
    }
}
