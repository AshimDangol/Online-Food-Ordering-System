package com.foodordering.factory;

import com.foodordering.model.Admin;
import com.foodordering.model.User;

/**
 * Factory Method Pattern — Concrete Creator.
 * Produces {@link Admin} instances. The extra field is the department name.
 */
public class AdminFactory extends UserFactory {
    @Override
    public User createUser(String id, String name, String email, String extra) {
        return new Admin(id, name, email, extra);
    }
}
