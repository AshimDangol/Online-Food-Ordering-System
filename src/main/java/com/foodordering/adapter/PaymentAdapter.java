package com.foodordering.adapter;

import java.util.Map;

/**
 * Adapter Pattern — Client-facing adapter factory.
 * Creates the correct concrete adapter (Khalti/eSewa/PayPal) for the
 * requested gateway type, so clients interact with a single
 * {@code processPayment(amount)} method regardless of the gateway.
 */
public class PaymentAdapter implements PaymentGateway {
    private final PaymentGateway gateway;

    /**
     * Creates the adapter for the specified payment gateway type.
     * @param type One of "KHALTI", "ESEWA", or "PAYPAL"
     * @throws IllegalArgumentException if the gateway type is unknown
     */
    public PaymentAdapter(String type) {
        this.gateway = create(type);
    }

    private PaymentGateway create(String type) {
        Map<String, PaymentGateway> gateways = Map.of(
                "KHALTI", new KhaltiAdapter(new KhaltiGateway()),
                "ESEWA", new ESewaAdapter(new ESewaGateway()),
                "PAYPAL", new PayPalAdapter(new PayPalGateway()));
        PaymentGateway adapter = gateways.get(type.toUpperCase());
        if (adapter == null) {
            throw new IllegalArgumentException("Unknown gateway: " + type);
        }
        return adapter;
    }

    @Override
    public boolean processPayment(double amount) {
        return gateway.processPayment(amount);
    }

    @Override
    public String getGatewayName() {
        return gateway.getGatewayName();
    }
}
