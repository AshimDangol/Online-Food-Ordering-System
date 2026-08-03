package com.foodordering.adapter;

/**
 * Adapter Pattern — Concrete Adapter.
 * Adapts the eSewa gateway's native API to the unified {@link PaymentGateway} interface.
 */
public class ESewaAdapter implements PaymentGateway {
    private final ESewaGateway esewa;

    public ESewaAdapter(ESewaGateway esewa) {
        this.esewa = esewa;
    }

    @Override
    public boolean processPayment(double amount) {
        return esewa.eSewaPay(amount);
    }

    @Override
    public String getGatewayName() {
        return esewa.getServiceName();
    }
}
