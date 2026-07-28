package com.foodordering.factory;

import com.foodordering.model.Customer;
import com.foodordering.model.User;

/**
 * Factory Method Pattern — Concrete Creator.
 * Produces {@link Customer} instances. Parses the extra field
 * as "phone|address" to populate customer-specific fields.
 */
public class CustomerFactory extends UserFactory {
    @Override
    public User createUser(String id, String name, String email, String extra) {
        String[] parts = extra.split("\\|");
        String phone = parts.length > 0 ? parts[0] : "N/A";
        String address = parts.length > 1 ? parts[1] : "N/A";
        return new Customer(id, name, email, phone, address);
    }
}
