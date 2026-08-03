package com.foodordering.adapter;

/**
 * Adapter Pattern — Concrete Adapter.
 * Adapts the Khalti gateway's native API to the unified {@link PaymentGateway} interface.
 */
public class KhaltiAdapter implements PaymentGateway {
    private final KhaltiGateway khalti;

    public KhaltiAdapter(KhaltiGateway khalti) {
        this.khalti = khalti;
    }

    @Override
    public boolean processPayment(double amount) {
        return khalti.khaltiPay(amount);
    }

    @Override
    public String getGatewayName() {
        return khalti.getServiceName();
    }
}
