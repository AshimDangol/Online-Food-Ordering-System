package com.foodordering.adapter;

/**
 * Adapter Pattern — Target interface.
 * Defines the unified payment interface that the system uses.
 * Concrete adapters translate this interface to specific gateway APIs.
 */
public interface PaymentGateway {
    /** Processes a payment for the given amount. @return true if successful */
    boolean processPayment(double amount);

    /** @return Display name of this payment gateway */
    String getGatewayName();
}
