package com.foodordering.factory;

import com.foodordering.model.DeliveryPartner;
import com.foodordering.model.User;

/**
 * Factory Method Pattern — Concrete Creator.
 * Produces {@link DeliveryPartner} instances. The extra field is the vehicle number.
 */
public class DeliveryPartnerFactory extends UserFactory {
    @Override
    public User createUser(String id, String name, String email, String extra) {
        return new DeliveryPartner(id, name, email, extra);
    }
}
