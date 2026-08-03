package com.foodordering.db;

import com.foodordering.model.BaseMenuItem;
import com.foodordering.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for menu item persistence backed by PostgreSQL.
 * Provides CRUD operations on the menu_items table,
 * including availability toggling for admin management.
 */
public class MenuItemDAO {

    /** @return A connection from the singleton DatabaseManager. */
    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    /** Returns all menu items that are currently marked as available. */
    public List<MenuItem> findAllAvailable() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items WHERE available = TRUE ORDER BY id";
        try (Statement stmt = getConn().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                items.add(new BaseMenuItem(rs.getString("name"), rs.getDouble("base_price")));
            }
        } catch (Exception e) {
            System.err.println("  [DB] Menu load error: " + e.getMessage());
        }
        return items;
    }

    /** Returns all menu items regardless of availability. */
    public List<MenuItem> findAll() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu_items ORDER BY id";
        try (Statement stmt = getConn().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                items.add(new BaseMenuItem(rs.getString("name"), rs.getDouble("base_price")));
            }
        } catch (Exception e) {
            System.err.println("  [DB] Menu load error: " + e.getMessage());
        }
        return items;
    }

    /** Adds a new menu item to the database. */
    public boolean addItem(String name, double price) {
        String sql = "INSERT INTO menu_items (name, base_price, available) VALUES (?, ?, TRUE)";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("  [DB] Add menu item error: " + e.getMessage());
            return false;
        }
    }

    /** Toggles whether a menu item is available for ordering. */
    public boolean toggleAvailability(String name, boolean available) {
        String sql = "UPDATE menu_items SET available = ? WHERE name = ?";
        try (PreparedStatement stmt = getConn().prepareStatement(sql)) {
            stmt.setBoolean(1, available);
            stmt.setString(2, name);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("  [DB] Toggle availability error: " + e.getMessage());
            return false;
        }
    }
}
