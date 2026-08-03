package com.foodordering.model;

/**
 * Represents a customer who places food orders.
 * Created by {@link com.foodordering.factory.CustomerFactory}.
 */
public class Customer extends User {
    private String phone;
    private String address;

    /**
     * @param id      Unique identifier
     * @param name    Customer name
     * @param email   Email address
     * @param phone   Contact number
     * @param address Delivery address
     */
    public Customer(String id, String name, String email, String phone, String address) {
        super(id, name, email, "CUSTOMER");
        this.phone = phone;
        this.address = address;
    }

    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
}
