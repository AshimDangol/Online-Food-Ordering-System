package com.foodordering.adapter;

/**
 * Adapter Pattern — Adapter.
 * Wraps incompatible payment gateway interfaces (Khalti, eSewa, PayPal)
 * behind the unified {@link PaymentGateway} interface.
 * Clients interact with a single {@code processPayment(amount)} method
 * regardless of which gateway is actually being used.
 */
public class PaymentAdapter implements PaymentGateway {
    private static final double USD_TO_NPR_RATE = 135.0;

    private final String type;
    private Object gateway;

    /**
     * Creates an adapter for the specified payment gateway type.
     * @param type One of "KHALTI", "ESEWA", or "PAYPAL"
     */
    public PaymentAdapter(String type) {
        this.type = type.toUpperCase();
        initGateway();
    }

    private void initGateway() {
        switch (type) {
            case "KHALTI" -> gateway = new KhaltiGateway();
            case "ESEWA" -> gateway = new ESewaGateway();
            case "PAYPAL" -> gateway = new PayPalGateway();
            default -> throw new IllegalArgumentException("Unknown gateway: " + type);
        }
    }

    @Override
    public boolean processPayment(double amount) {
        switch (type) {
            case "KHALTI":
                return ((KhaltiGateway) gateway).khaltiPay(amount);
            case "ESEWA":
                return ((ESewaGateway) gateway).eSewaPay(amount);
            case "PAYPAL":
                double usdAmount = amount / USD_TO_NPR_RATE;
                return ((PayPalGateway) gateway).paypalPay(usdAmount);
            default:
                return false;
        }
    }

    @Override
    public String getGatewayName() {
        return switch (type) {
            case "KHALTI" -> ((KhaltiGateway) gateway).getServiceName();
            case "ESEWA" -> ((ESewaGateway) gateway).getServiceName();
            case "PAYPAL" -> ((PayPalGateway) gateway).getServiceName();
            default -> "Unknown";
        };
    }
}
