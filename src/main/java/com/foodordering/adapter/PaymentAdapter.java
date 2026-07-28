package com.foodordering.adapter;

/**
 * Adapter Pattern — Adapter.
 * Wraps incompatible payment gateway interfaces (Khalti, eSewa, PayPal)
 * behind the unified {@link PaymentGateway} interface.
 * Clients interact with a single {@code processPayment(amount)} method
 * regardless of which gateway is actually being used.
 */
public class PaymentAdapter implements PaymentGateway {
    private KhaltiGateway khaltiGateway;
    private ESewaGateway eSewaGateway;
    private PayPalGateway payPalGateway;
    private String type;

    /**
     * Creates an adapter for the specified payment gateway type.
     * @param type One of "KHALTI", "ESEWA", or "PAYPAL"
     */
    public PaymentAdapter(String type) {
        this.type = type;
        switch (type.toUpperCase()) {
            case "KHALTI":
                khaltiGateway = new KhaltiGateway();
                break;
            case "ESEWA":
                eSewaGateway = new ESewaGateway();
                break;
            case "PAYPAL":
                payPalGateway = new PayPalGateway();
                break;
            default:
                throw new IllegalArgumentException("Unknown gateway: " + type);
        }
    }

    @Override
    public boolean processPayment(double amount) {
        switch (type.toUpperCase()) {
            case "KHALTI":
                return khaltiGateway.khaltiPay(amount);
            case "ESEWA":
                return eSewaGateway.eSewaPay(amount);
            case "PAYPAL":
                double usdAmount = amount / 135.0;
                return payPalGateway.paypalPay(usdAmount);
            default:
                return false;
        }
    }

    @Override
    public String getGatewayName() {
        switch (type.toUpperCase()) {
            case "KHALTI":  return khaltiGateway.getServiceName();
            case "ESEWA":   return eSewaGateway.getServiceName();
            case "PAYPAL":  return payPalGateway.getServiceName();
            default:        return "Unknown";
        }
    }
}
