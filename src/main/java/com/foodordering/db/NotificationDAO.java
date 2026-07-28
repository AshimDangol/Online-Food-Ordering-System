package com.foodordering.db;

import java.sql.*;

public class NotificationDAO {
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public void saveNotification(String orderId, String recipient, String message) {
        String sql = "INSERT INTO notifications (order_id, recipient, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, orderId);
            stmt.setString(2, recipient);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("  [DB] Save notification error: " + e.getMessage());
        }
    }

    public void printNotificationsForUser(String recipient) {
        String sql = "SELECT * FROM notifications WHERE recipient = ? ORDER BY created_at DESC";
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
        } catch (SQLException e) {
            System.err.println("  [DB] Load notifications error: " + e.getMessage());
        }
    }
}
