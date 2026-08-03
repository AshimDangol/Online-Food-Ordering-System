package com.foodordering.model;

/**
 * Represents a system administrator with elevated privileges.
 * Created by {@link com.foodordering.factory.AdminFactory}.
 * Used by the Proxy pattern to authorize sensitive operations.
 */
public class Admin extends User {
    private String department;

    /**
     * @param id         Unique identifier
     * @param name       Admin name
     * @param email      Email address
     * @param department Department the admin manages
     */
    public Admin(String id, String name, String email, String department) {
        super(id, name, email, "ADMIN");
        this.department = department;
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }
}
