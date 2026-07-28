package com.foodordering.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton pattern — Manages the H2 database connection and schema.
 * Provides a single point of access to the embedded H2 database.
 * Auto-creates tables and seeds initial menu data on first run.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./data/foodordering;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    private static DatabaseManager instance;
    private Connection connection;

    /** Private constructor — initializes the connection and creates tables if needed. */
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            createTables();
        } catch (SQLException e) {
            System.err.println("  [DB] Failed to initialize database: " + e.getMessage());
        }
    }

    /** Returns the singleton instance (thread-safe via synchronized). */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /** Returns the active database connection, reconnecting if necessary. */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Failed to reconnect: " + e.getMessage());
        }
        return connection;
    }

    /** Creates all required tables and seeds initial menu items. */
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(20) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "phone VARCHAR(20), " +
                    "address VARCHAR(200), " +
                    "department VARCHAR(100), " +
                    "vehicle_number VARCHAR(50), " +
                    "available BOOLEAN DEFAULT TRUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS menu_items (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "base_price DOUBLE NOT NULL, " +
                    "available BOOLEAN DEFAULT TRUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id VARCHAR(20) PRIMARY KEY, " +
                    "customer_id VARCHAR(20) NOT NULL, " +
                    "customer_name VARCHAR(100), " +
                    "delivery_strategy VARCHAR(50), " +
                    "payment_method VARCHAR(20), " +
                    "subtotal DOUBLE, " +
                    "tax_amount DOUBLE, " +
                    "delivery_charge DOUBLE, " +
                    "total_amount DOUBLE, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (customer_id) REFERENCES users(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "order_id VARCHAR(20) NOT NULL, " +
                    "item_description VARCHAR(200), " +
                    "unit_price DOUBLE, " +
                    "quantity INT, " +
                    "total_price DOUBLE, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "order_id VARCHAR(20) NOT NULL, " +
                    "recipient VARCHAR(100), " +
                    "message TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id))");

            try {
                stmt.execute("MERGE INTO menu_items (id, name, base_price, available) VALUES " +
                        "(1, 'Margherita Pizza', 450.0, TRUE), " +
                        "(2, 'Pepperoni Pizza', 500.0, TRUE), " +
                        "(3, 'White Sauce Pasta', 350.0, TRUE), " +
                        "(4, 'Chicken Burger', 350.0, TRUE), " +
                        "(5, 'Fries', 150.0, TRUE), " +
                        "(6, 'Momo (12 pcs)', 300.0, TRUE), " +
                        "(7, 'Chowmein', 200.0, TRUE), " +
                        "(8, 'Coke', 100.0, TRUE)");
            } catch (SQLException e) {
                // Ignore duplicate key on re-run — menu items already seeded
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Failed to create tables: " + e.getMessage());
        }
    }

    /** Drops all tables and recreates them — useful for testing. */
    public void resetDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            createTables();
        } catch (SQLException e) {
            System.err.println("  [DB] Failed to reset: " + e.getMessage());
        }
    }

    /** Closes the database connection gracefully. */
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("  [DB] Shutdown error: " + e.getMessage());
        }
    }
}
