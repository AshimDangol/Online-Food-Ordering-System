package com.foodordering.db;

import com.foodordering.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Order persistence backed by PostgreSQL.
 * Handles saving orders with their line items, updating status,
 * and retrieving order history by customer or for all users.
 * Uses the OrderState pattern to restore state on retrieval.
 */
public class OrderDAO {

    /** @return A connection from the singleton DatabaseManager. */
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Persists an order and all its line items to the database.
     *
     * @param order The fully constructed Order domain object
     * @return true if the order was saved successfully
     */
    public boolean saveOrder(Order order) {
        String sql = "INSERT INTO orders (id, customer_id, customer_name, delivery_strategy, payment_method, " +
                "subtotal, tax_amount, delivery_charge, total_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, order.getOrderId());
            stmt.setString(2, order.getCustomer().getId());
            stmt.setString(3, order.getCustomer().getName());
            stmt.setString(4, order.getDeliveryStrategy() != null ? order.getDeliveryStrategy().getStrategyName() : null);
            stmt.setString(5, order.getPaymentMethod());
            stmt.setDouble(6, order.calculateTotal());
            stmt.setDouble(7, order.getTaxAmount());
            stmt.setDouble(8, order.getDeliveryCharge());
            stmt.setDouble(9, order.getTotalAmount());
            stmt.setString(10, order.getStatus());
            stmt.executeUpdate();

            saveOrderItems(order);
            return true;
        } catch (SQLException e) {
            System.err.println("  [DB] Save order error: " + e.getMessage());
            return false;
        }
    }

    /** Inserts each line item linked to the parent order. */
    private void saveOrderItems(Order order) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, item_description, unit_price, quantity, total_price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            for (OrderItem oi : order.getItems()) {
                stmt.setString(1, order.getOrderId());
                stmt.setString(2, oi.getDescription());
                stmt.setDouble(3, oi.getItem().getPrice());
                stmt.setInt(4, oi.getQuantity());
                stmt.setDouble(5, oi.getTotalPrice());
                stmt.executeUpdate();
            }
        }
    }

    /** Updates the status string of an order (e.g., CONFIRMED, DELIVERED). */
    public boolean updateStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("  [DB] Update status error: " + e.getMessage());
            return false;
        }
    }

    /** Returns all orders placed by a specific customer, newest first. */
    public List<Order> findByCustomerId(String customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapOrderSummary(rs));
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Find by customer error: " + e.getMessage());
        }
        return orders;
    }

    /** Returns all orders in the system, newest first. */
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        try (Statement stmt = getConn().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                orders.add(mapOrderSummary(rs));
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Find all error: " + e.getMessage());
        }
        return orders;
    }

    /**
     * Maps a database row to an Order domain object.
     * Restores the correct OrderState implementation based on the status column,
     * so the State pattern continues to work after deserialization.
     */
    private Order mapOrderSummary(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getString("customer_id"),
                rs.getString("customer_name"),
                "", "", "");
        Order order = new Order(rs.getString("id"), customer);
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        String status = rs.getString("status");
        order.setState(switch (status) {
            case "CONFIRMED" -> new com.foodordering.state.ConfirmedState();
            case "PREPARING" -> new com.foodordering.state.PreparingState();
            case "OUT_FOR_DELIVERY" -> new com.foodordering.state.OutForDeliveryState();
            case "DELIVERED" -> new com.foodordering.state.DeliveredState();
            case "CANCELLED" -> new com.foodordering.state.CancelledState();
            default -> new com.foodordering.state.PendingState();
        });
        return order;
    }
}
