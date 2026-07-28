package com.foodordering.db;

import com.foodordering.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for User persistence.
 * Handles registration, authentication, and lookup of
 * Customer, Admin, and DeliveryPartner entities in the H2 database.
 */
public class UserDAO {

    /** @return A connection from the singleton DatabaseManager. */
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Inserts a new user into the database with the given password.
     * Maps role-specific fields (phone, address, department, vehicle) based on User subclass.
     *
     * @param user     The User domain object (Customer, Admin, or DeliveryPartner)
     * @param password The plaintext password to store
     * @return true if registration succeeded
     */
    public boolean registerUser(User user, String password) {
        String sql = "INSERT INTO users (id, name, email, password, role, phone, address, department, vehicle_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, password);
            stmt.setString(5, user.getRole());
            if (user instanceof Customer c) {
                stmt.setString(6, c.getPhone());
                stmt.setString(7, c.getAddress());
                stmt.setString(8, null);
                stmt.setString(9, null);
            } else if (user instanceof Admin a) {
                stmt.setString(6, null);
                stmt.setString(7, null);
                stmt.setString(8, a.getDepartment());
                stmt.setString(9, null);
            } else if (user instanceof DeliveryPartner d) {
                stmt.setString(6, null);
                stmt.setString(7, null);
                stmt.setString(8, null);
                stmt.setString(9, d.getVehicleNumber());
            } else {
                stmt.setString(6, null);
                stmt.setString(7, null);
                stmt.setString(8, null);
                stmt.setString(9, null);
            }
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("  [DB] Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates a user by email and password.
     *
     * @param email    The user's email address
     * @param password The user's password
     * @return The matching User domain object, or null if authentication fails
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Auth error: " + e.getMessage());
        }
        return null;
    }

    /** Finds a user by their unique ID. */
    public User findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Find error: " + e.getMessage());
        }
        return null;
    }

    /** Finds a user by their email address. */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Find error: " + e.getMessage());
        }
        return null;
    }

    /** Returns all users with the given role. */
    public List<User> findAllByRole(String role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Find error: " + e.getMessage());
        }
        return users;
    }

    /** Returns all registered users. */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement stmt = getConn().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Find all error: " + e.getMessage());
        }
        return users;
    }

    /**
     * Maps a database ResultSet row to the appropriate User subclass.
     * Inspects the role column to determine whether to create
     * a Customer, Admin, or DeliveryPartner instance.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String role = rs.getString("role");
        switch (role) {
            case "CUSTOMER":
                return new Customer(id, name, email,
                        rs.getString("phone") != null ? rs.getString("phone") : "N/A",
                        rs.getString("address") != null ? rs.getString("address") : "N/A");
            case "ADMIN":
                return new Admin(id, name, email,
                        rs.getString("department") != null ? rs.getString("department") : "General");
            case "DELIVERY":
                return new DeliveryPartner(id, name, email,
                        rs.getString("vehicle_number") != null ? rs.getString("vehicle_number") : "N/A");
            default:
                return new Customer(id, name, email, "N/A", "N/A");
        }
    }
}
