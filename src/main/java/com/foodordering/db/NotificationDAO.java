package com.foodordering.db;

import java.sql.*;

/**
 * Data access object for notification persistence backed by PostgreSQL.
 * Logs observer pattern notifications to the database
 * and provides retrieval for user-facing notification views.
 */
public class NotificationDAO {

    /** @return A connection from the singleton DatabaseManager. */
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Persists a notification linked to an order.
     *
     * @param orderId   The order this notification relates to
     * @param recipient Display name of the recipient (customer, kitchen, etc.)
     * @param message   The notification message content
     */
    public void saveNotification(String orderId, String recipient, String message) {
        String sql = "INSERT INTO notifications (order_id, recipient, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setString(2, recipient);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("  [DB] Save notification error: " + e.getMessage());
        }
    }

    /**
     * Re-points notifications from an old display name to a new one.
     * Keeps a user's notification history visible after they change their name.
     *
     * @param oldName The previous name stored as recipient
     * @param newName The new display name to store instead
     */
    public void renameRecipient(String oldName, String newName) {
        String sql = "UPDATE notifications SET recipient = ? WHERE recipient = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("  [DB] Rename notification recipient error: " + e.getMessage());
        }
    }

    /**
     * Prints all notifications for a given recipient to the console.
     * Uses SQL LIKE matching so "%" retrieves all notifications.
     *
     * @param recipient The recipient name or "%" for all
     */
    public void printNotificationsForUser(String recipient) {
        String sql = "SELECT * FROM notifications WHERE recipient LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, recipient);
            ResultSet rs = stmt.executeQuery();
            boolean hasAny = false;
            while (rs.next()) {
                hasAny = true;
                System.out.println("  [" + rs.getTimestamp("created_at") + "] " + rs.getString("message"));
            }
            if (!hasAny) {
                System.out.println("  No notifications found.");
            }
        } catch (Exception e) {
            System.err.println("  [DB] Load notifications error: " + e.getMessage());
        }
    }
}
