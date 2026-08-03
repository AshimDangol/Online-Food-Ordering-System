package com.foodordering.adapter;

/**
 * Adapter Pattern — Concrete Adapter.
 * Adapts the PayPal gateway's native API (which operates in USD) to the
 * unified {@link PaymentGateway} interface, converting NPR to USD internally.
 */
public class PayPalAdapter implements PaymentGateway {
    private static final double USD_TO_NPR_RATE = 135.0;

    private final PayPalGateway paypal;

    public PayPalAdapter(PayPalGateway paypal) {
        this.paypal = paypal;
    }

    @Override
    public boolean processPayment(double amountNpr) {
        double usdAmount = amountNpr / USD_TO_NPR_RATE;
        return paypal.paypalPay(usdAmount);
    }

    @Override
    public String getGatewayName() {
        return paypal.getServiceName();
    }
}
