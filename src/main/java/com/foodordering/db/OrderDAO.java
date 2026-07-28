package com.foodordering.db;

import com.foodordering.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

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
            stmt.setDouble(7, 0); // tax stored separately if needed
            stmt.setDouble(8, 0); // delivery charge stored separately if needed
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

    private Order mapOrderSummary(ResultSet rs) throws SQLException {
        Customer customer = new Customer(
                rs.getString("customer_id"),
                rs.getString("customer_name"),
                "", "", "");
        Order order = new Order(rs.getString("id"), customer);
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        // Set status via state
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
