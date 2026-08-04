package com.foodordering.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for notification persistence backed by PostgreSQL.
 * Logs observer pattern notifications to the database and provides
 * retrieval for user-facing notification views.
 *
 * <p>Notifications are keyed by the recipient's user id ({@code recipient_id})
 * so history survives name changes and two users with the same display name
 * never leak notifications to each other. A denormalized display name
 * ({@code recipient}) is kept for reporting and legacy rows.
 */
public class NotificationDAO {

    /** @return A connection from the singleton DatabaseManager. */
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Persists a notification linked to an order.
     *
     * @param orderId       The order this notification relates to
     * @param recipientId   The recipient user's id (may be null for legacy rows)
     * @param recipientName Display name of the recipient (customer, kitchen, etc.)
     * @param message       The notification message content
     */
    public void saveNotification(String orderId, String recipientId, String recipientName, String message) {
        String sql = "INSERT INTO notifications (order_id, recipient, recipient_id, message) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setString(2, recipientName);
            stmt.setString(3, recipientId);
            stmt.setString(4, message);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("  [DB] Save notification error: " + e.getMessage());
        }
    }

    /**
     * Re-points a user's notifications to a new display name (after a rename).
     * Matching is by user id, never by name, so other users with the same
     * name are unaffected.
     *
     * @param userId  The user whose notifications are renamed
     * @param newName The new display name to store instead
     */
    public void renameRecipient(String userId, String newName) {
        String sql = "UPDATE notifications SET recipient = ? WHERE recipient_id = ?";
        try (PreparedStatement st = getConn().prepareStatement(sql)) {
            st.setString(1, newName);
            st.setString(2, userId);
            st.executeUpdate();
        } catch (Exception e) {
            System.err.println("  [DB] Rename notification recipient error: " + e.getMessage());
        }
    }

    /**
     * Returns the notification messages visible to a user.
     * Matches by user id, falling back to the exact display name for
     * legacy rows that predate id-keyed notifications. Names are compared
     * exactly (no LIKE) so '%' and '_' inside a name cannot match other rows.
     *
     * @param userId The recipient user id
     * @param name   The recipient display name
     * @return Notification messages, newest first
     */
    public List<String> listNotifications(String userId, String name) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT message FROM notifications " +
                "WHERE recipient_id = ? OR recipient = ? ORDER BY created_at DESC";
        try (PreparedStatement st = getConn().prepareStatement(sql)) {
            st.setString(1, userId);
            st.setString(2, name);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                messages.add(rs.getString("message"));
            }
        } catch (Exception e) {
            System.err.println("  [DB] Load notifications error: " + e.getMessage());
        }
        return messages;
    }

    /** Prints a user's notifications to the console. */
    public void printNotificationsForUser(String userId, String name) {
        List<String> messages = listNotifications(userId, name);
        if (messages.isEmpty()) {
            System.out.println("  No notifications found.");
            return;
        }
        for (String message : messages) {
            System.out.println("  \u2022 " + message);
        }
    }

    /**
     * Prints every notification in the system (admin view).
     * Note: the plain SELECT no longer relies on a '%' LIKE sentinel.
     */
    public void printAllNotifications() {
        String sql = "SELECT created_at, message FROM notifications ORDER BY created_at DESC";
        boolean hasAny = false;
        try (Statement st = getConn().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                hasAny = true;
                System.out.println("  [" + rs.getTimestamp("created_at") + "] " + rs.getString("message"));
            }
        } catch (Exception e) {
            System.err.println("  [DB] Load notifications error: " + e.getMessage());
        }
        if (!hasAny) {
            System.out.println("  No notifications found.");
        }
    }
}