package com.foodordering.factory;

import com.foodordering.model.User;

/**
 * Factory Method Pattern — Creator (abstract).
 * Defines the factory method {@link #createUser} that subclasses override
 * to produce specific User types (Customer, Admin, DeliveryPartner).
 * The {@link #createAndRegister} template method adds logging around creation.
 */
public abstract class UserFactory {

    /**
     * Factory method — subclasses provide the concrete implementation.
     * @param id    Unique identifier
     * @param name  User display name
     * @param email Email address
     * @param extra Extra data parsed by each concrete factory
     * @return A new User instance of the appropriate subclass
     */
    public abstract User createUser(String id, String name, String email, String extra);

    /**
     * Template method that creates a user and logs the result.
     * @return The newly created User
     */
    public User createAndRegister(String id, String name, String email, String extra) {
        User user = createUser(id, name, email, extra);
        System.out.println("User created: " + user);
        return user;
    }
}
