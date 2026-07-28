package com.foodordering.model;

/**
 * Abstract base class for all system users.
 * Part of the user management subsystem.
 * Subclasses define specific roles via the Factory Method pattern.
 */
public abstract class User {
    private String id;
    private String name;
    private String email;
    private String role;

    /**
     * Constructs a User with the given credentials.
     * @param id    Unique user identifier
     * @param name  Display name
     * @param email Email address
     * @param role  System role (CUSTOMER, ADMIN, DELIVERY)
     */
    public User(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    @Override
    public String toString() {
        return String.format("%s [%s] - %s", name, role, email);
    }
}
