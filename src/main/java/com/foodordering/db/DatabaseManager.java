package com.foodordering.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton pattern — Manages the PostgreSQL database connection and schema.
 * Provides a single point of access to the PostgreSQL database.
 * Auto-creates the database if it does not exist, then creates tables
 * and seeds initial menu data on first run.
 */
public class DatabaseManager {
    private static final String DB_URL = env("FOOD_DB_URL", "jdbc:postgresql://localhost:5432/foodordering");
    private static final String ADMIN_DB_URL = env("FOOD_ADMIN_DB_URL", "jdbc:postgresql://localhost:5432/postgres");
    private static final String DB_USER = env("FOOD_DB_USER", "postgres");
    private static final String DB_PASS = env("FOOD_DB_PASS", "1234");

    /** Reads an environment variable, falling back to the given default. */
    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static DatabaseManager instance;
    private Connection connection;
    private boolean dbDownMessagePrinted;

    /** Private constructor — initializes the connection and creates tables if needed. */
    private DatabaseManager() {
        try {
            ensureDatabaseExists();
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            createTables();
        } catch (SQLException e) {
            printDbDownMessage();
        }
    }

    /** Creates the foodordering database if it does not already exist. */
    private void ensureDatabaseExists() {
        try (Connection adminConn = DriverManager.getConnection(ADMIN_DB_URL, DB_USER, DB_PASS);
             Statement stmt = adminConn.createStatement()) {
            stmt.execute("CREATE DATABASE foodordering");
        } catch (SQLException e) {
            // Database already exists — silently ignore
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
                dbDownMessagePrinted = false;
            }
        } catch (SQLException e) {
            connection = null;
            printDbDownMessage();
        }
        return connection;
    }

    /** Prints a friendly message (once per outage) when PostgreSQL cannot be reached. */
    private void printDbDownMessage() {
        if (!dbDownMessagePrinted) {
            System.err.println("  [DB] Cannot connect to PostgreSQL at " + DB_URL);
            System.err.println("       Is PostgreSQL running on port 5432 with database 'foodordering'?");
            dbDownMessagePrinted = true;
        }
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
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "base_price DOUBLE PRECISION NOT NULL, " +
                    "available BOOLEAN DEFAULT TRUE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id VARCHAR(20) PRIMARY KEY, " +
                    "customer_id VARCHAR(20) NOT NULL, " +
                    "customer_name VARCHAR(100), " +
                    "delivery_strategy VARCHAR(50), " +
                    "payment_method VARCHAR(20), " +
                    "subtotal DOUBLE PRECISION, " +
                    "tax_amount DOUBLE PRECISION, " +
                    "delivery_charge DOUBLE PRECISION, " +
                    "total_amount DOUBLE PRECISION, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (customer_id) REFERENCES users(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS order_items (" +
                    "id SERIAL PRIMARY KEY, " +
                    "order_id VARCHAR(20) NOT NULL, " +
                    "item_description VARCHAR(200), " +
                    "unit_price DOUBLE PRECISION, " +
                    "quantity INT, " +
                    "total_price DOUBLE PRECISION, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id SERIAL PRIMARY KEY, " +
                    "order_id VARCHAR(20) NOT NULL, " +
                    "recipient VARCHAR(100), " +
                    "message TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (order_id) REFERENCES orders(id))");

            try {
                stmt.execute("INSERT INTO menu_items (id, name, base_price, available) VALUES " +
                        "(1, 'Margherita Pizza', 450.0, TRUE), " +
                        "(2, 'Pepperoni Pizza', 500.0, TRUE), " +
                        "(3, 'White Sauce Pasta', 350.0, TRUE), " +
                        "(4, 'Chicken Burger', 350.0, TRUE), " +
                        "(5, 'Fries', 150.0, TRUE), " +
                        "(6, 'Momo (12 pcs)', 300.0, TRUE), " +
                        "(7, 'Chowmein', 200.0, TRUE), " +
                        "(8, 'Coke', 100.0, TRUE) " +
                        "ON CONFLICT (id) DO NOTHING");
                // Explicit-id inserts do not advance the SERIAL sequence, so sync it to
                // the current max id. Without this, the next addItem() would collide
                // with an existing primary key and fail.
                stmt.execute("SELECT setval('menu_items_id_seq', (SELECT COALESCE(MAX(id), 1) FROM menu_items))");
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
            stmt.execute("DROP TABLE IF EXISTS order_items CASCADE");
            stmt.execute("DROP TABLE IF EXISTS notifications CASCADE");
            stmt.execute("DROP TABLE IF EXISTS orders CASCADE");
            stmt.execute("DROP TABLE IF EXISTS menu_items CASCADE");
            stmt.execute("DROP TABLE IF EXISTS users CASCADE");
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
