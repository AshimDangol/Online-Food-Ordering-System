package com.foodordering.model;

/**
 * Represents a delivery partner who delivers orders.
 * Created by {@link com.foodordering.factory.DeliveryPartnerFactory}.
 * Notified via the Observer pattern when orders are ready for delivery.
 */
public class DeliveryPartner extends User {
    private String vehicleNumber;
    private boolean available;

    /**
     * @param id            Unique identifier
     * @param name          Partner name
     * @param email         Email address
     * @param vehicleNumber Vehicle registration number
     */
    public DeliveryPartner(String id, String name, String email, String vehicleNumber) {
        super(id, name, email, "DELIVERY");
        this.vehicleNumber = vehicleNumber;
        this.available = true;
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
