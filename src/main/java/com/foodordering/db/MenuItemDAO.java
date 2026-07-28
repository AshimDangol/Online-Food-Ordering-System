package com.foodordering.db;

import com.foodordering.model.BaseMenuItem;
import com.foodordering.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public List<MenuItem> findAllAvailable() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE available = TRUE ORDER BY id";
        try (Statement stmt = getConn().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                items.add(new BaseMenuItem(rs.getString("name"), rs.getDouble("base_price")));
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Menu load error: " + e.getMessage());
        }
        return items;
    }

    public List<MenuItem> findAll() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items ORDER BY id";
        try (Statement stmt = getConn().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                items.add(new BaseMenuItem(rs.getString("name"), rs.getDouble("base_price")));
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Menu load error: " + e.getMessage());
        }
        return items;
    }

    public boolean addItem(String name, double price) {
        String sql = "INSERT INTO menu_items (name, base_price, available) VALUES (?, ?, TRUE)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("  [DB] Add menu item error: " + e.getMessage());
            return false;
        }
    }

    public boolean toggleAvailability(String name, boolean available) {
        String sql = "UPDATE menu_items SET available = ? WHERE name = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setString(2, name);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("  [DB] Toggle availability error: " + e.getMessage());
            return false;
        }
    }
}
