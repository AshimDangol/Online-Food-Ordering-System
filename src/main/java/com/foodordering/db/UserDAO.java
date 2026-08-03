package com.foodordering.db;

import com.foodordering.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

/**
 * Data access object for User persistence.
 * Handles registration, authentication, and lookup of
 * Customer, Admin, and DeliveryPartner entities in the PostgreSQL database.
 */
public class UserDAO {

    /** @return A connection from the singleton DatabaseManager. */
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Inserts a new user into the database with the given password.
     * Passwords are stored salted and hashed as "salt:hexhash".
     * Maps role-specific fields (phone, address, department, vehicle) based on User subclass.
     *
     * @param user     The User domain object (Customer, Admin, or DeliveryPartner)
     * @param password The plaintext password to hash and store
     * @return true if registration succeeded
     */
    public boolean registerUser(User user, String password) {
        String sql = "INSERT INTO users (id, name, email, password, role, phone, address, department, vehicle_number, available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashPassword(password));
            stmt.setString(5, user.getRole());
            if (user instanceof Customer c) {
                stmt.setString(6, c.getPhone());
                stmt.setString(7, c.getAddress());
                stmt.setString(8, null);
                stmt.setString(9, null);
                stmt.setBoolean(10, true);
            } else if (user instanceof Admin a) {
                stmt.setString(6, null);
                stmt.setString(7, null);
                stmt.setString(8, a.getDepartment());
                stmt.setString(9, null);
                stmt.setBoolean(10, true);
            } else if (user instanceof DeliveryPartner d) {
                stmt.setString(6, null);
                stmt.setString(7, null);
                stmt.setString(8, null);
                stmt.setString(9, d.getVehicleNumber());
                stmt.setBoolean(10, d.isAvailable());
            } else {
                stmt.setString(6, null);
                stmt.setString(7, null);
                stmt.setString(8, null);
                stmt.setString(9, null);
                stmt.setBoolean(10, true);
            }
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("  [DB] Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates a user by email and password.
     * Compares the salted SHA-256 hash of the supplied password against
     * the stored value. Legacy plaintext rows (no "salt:" prefix) are
     * still accepted for backward compatibility.
     *
     * @param email    The user's email address
     * @param password The user's password
     * @return The matching User domain object, or null if authentication fails
     */
    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("password");
                if (matchesPassword(password, stored)) {
                    return mapUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("  [DB] Auth error: " + e.getMessage());
        }
        return null;
    }

    private static final int HASH_ITERATIONS = 10_000;

    /** Hashes a password with a random salt: returns "salt:iterations:hexhash". */
    private String hashPassword(String password) {
        String salt = Long.toHexString(new SecureRandom().nextLong());
        return salt + ":" + HASH_ITERATIONS + ":" + sha256HexIterated(salt + password, HASH_ITERATIONS);
    }

    /**
     * Verifies a plaintext password against a stored hash. Supports the current
     * "salt:iterations:hexhash" format, the older single-iteration "salt:hexhash"
     * rows, and legacy plaintext rows created before hashing was introduced.
     */
    private boolean matchesPassword(String password, String stored) {
        if (stored == null) return false;
        String[] parts = stored.split(":");
        if (parts.length == 3) {
            int iterations;
            try {
                iterations = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                iterations = 1;
            }
            return sha256HexIterated(parts[0] + password, iterations).equalsIgnoreCase(parts[2]);
        }
        if (parts.length == 2) {
            return sha256HexIterated(parts[0] + password, 1).equalsIgnoreCase(parts[1]);
        }
        // Legacy row stored in plaintext before hashing was introduced
        return stored.equals(password);
    }

    /** Iterated SHA-256 hex digest of the given input. */
    private String sha256HexIterated(String input, int iterations) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            for (int i = 1; i < iterations; i++) {
                md.reset();
                digest = md.digest(digest);
            }
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /**
     * Updates a user's profile fields (name, email, and role-specific details)
     * in the database. The email column is UNIQUE, so duplicate emails fail.
     *
     * @param user The User domain object carrying the new values
     * @return true if exactly one row was updated
     */
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ?, department = ?, vehicle_number = ?, available = ? WHERE id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            if (user instanceof Customer c) {
                stmt.setString(3, c.getPhone());
                stmt.setString(4, c.getAddress());
                stmt.setString(5, null);
                stmt.setString(6, null);
                stmt.setBoolean(7, true);
            } else if (user instanceof Admin a) {
                stmt.setString(3, null);
                stmt.setString(4, null);
                stmt.setString(5, a.getDepartment());
                stmt.setString(6, null);
                stmt.setBoolean(7, true);
            } else if (user instanceof DeliveryPartner d) {
                stmt.setString(3, null);
                stmt.setString(4, null);
                stmt.setString(5, null);
                stmt.setString(6, d.getVehicleNumber());
                stmt.setBoolean(7, d.isAvailable());
            } else {
                stmt.setString(3, null);
                stmt.setString(4, null);
                stmt.setString(5, null);
                stmt.setString(6, null);
                stmt.setBoolean(7, true);
            }
            stmt.setString(8, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("  [DB] Profile update error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Changes a user's password by storing a fresh salted hash.
     *
     * @param userId      The unique user identifier
     * @param newPassword The new plaintext password to hash and store
     * @return true if exactly one row was updated
     */
    public boolean updatePassword(String userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, hashPassword(newPassword));
            stmt.setString(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("  [DB] Password update error: " + e.getMessage());
            return false;
        }
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
        } catch (Exception e) {
            System.err.println("  [DB] Find error: " + e.getMessage());
        }
        return null;
    }

    /** Deletes a user by ID (test cleanup; orders must be removed first). */
    public boolean deleteUser(String id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("  [DB] Delete user error: " + e.getMessage());
            return false;
        }
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
                DeliveryPartner partner = new DeliveryPartner(id, name, email,
                        rs.getString("vehicle_number") != null ? rs.getString("vehicle_number") : "N/A");
                partner.setAvailable(rs.getBoolean("available"));
                return partner;
            default:
                return new Customer(id, name, email, "N/A", "N/A");
        }
    }
}
