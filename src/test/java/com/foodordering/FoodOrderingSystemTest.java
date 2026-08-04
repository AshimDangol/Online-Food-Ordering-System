package com.foodordering;

import com.foodordering.adapter.PaymentAdapter;
import com.foodordering.builder.OrderBuilder;
import com.foodordering.command.*;
import com.foodordering.config.RestaurantConfig;
import com.foodordering.decorator.*;
import com.foodordering.facade.OrderFacade;
import com.foodordering.factory.*;
import com.foodordering.model.BaseMenuItem;
import com.foodordering.model.Customer;
import com.foodordering.model.MenuItem;
import com.foodordering.model.Order;
import com.foodordering.model.Admin;
import com.foodordering.model.User;
import com.foodordering.model.DeliveryPartner;
import com.foodordering.observer.*;
import com.foodordering.proxy.AuthProxy;
import com.foodordering.proxy.IOrderService;
import com.foodordering.proxy.OrderService;
import com.foodordering.report.ReportGenerator;
import com.foodordering.strategy.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * JUnit test cases verifying all 11 design pattern implementations
 * and their integration within the Online Food Ordering System.
 * Includes individual pattern tests plus a full integration workflow test.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FoodOrderingSystemTest {

    // ===================== SINGLETON TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("Singleton - RestaurantConfig returns same instance")
    void testSingleton() {
        RestaurantConfig c1 = RestaurantConfig.getInstance();
        RestaurantConfig c2 = RestaurantConfig.getInstance();
        assertSame(c1, c2, "Singleton should return the same instance");
        assertEquals("FoodieExpress", c1.getRestaurantName());
        assertEquals(0.13, c1.getTaxRate(), 0.001);
    }

    // ===================== FACTORY METHOD TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("Factory Method - Create different user types")
    void testFactoryMethod() {
        UserFactory cf = new CustomerFactory();
        User customer = cf.createUser("U001", "Test", "t@t.com", "9800000000|Ktm");
        assertTrue(customer instanceof Customer);
        assertEquals("CUSTOMER", customer.getRole());

        UserFactory af = new AdminFactory();
        User admin = af.createUser("A001", "Admin", "a@t.com", "IT");
        assertTrue(admin instanceof Admin);
        assertEquals("ADMIN", admin.getRole());

        UserFactory df = new DeliveryPartnerFactory();
        User delivery = df.createUser("D001", "Dev", "d@t.com", "BA 1 PA 1234");
        assertTrue(delivery instanceof DeliveryPartner);
        assertEquals("DELIVERY", delivery.getRole());
    }

    // ===================== BUILDER TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("Builder - Build order with items, strategy, payment")
    void testBuilder() {
        Customer c = new Customer("U002", "Test", "t@t.com", "9800000000", "Ktm");
        MenuItem pizza = new BaseMenuItem("Pizza", 500.0);
        MenuItem drink = new BaseMenuItem("Drink", 100.0);

        OrderBuilder builder = new OrderBuilder(c)
                .addItem(pizza, 2)
                .addItem(drink, 1)
                .setDeliveryStrategy(new StandardDeliveryStrategy(), 5.0)
                .setPaymentMethod("KHALTI");

        Order order = builder.build();

        assertNotNull(order.getOrderId());
        assertTrue(order.getOrderId().startsWith("ORD-"));
        assertEquals(c, order.getCustomer());
        assertEquals(2, order.getItems().size());
        assertEquals("KHALTI", order.getPaymentMethod());
        assertEquals(1100.0, order.calculateTotal(), 0.01);
        assertTrue(order.getTotalAmount() > 1100.0);
    }

    // ===================== DECORATOR TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("Decorator - Add extras to menu items")
    void testDecorator() {
        MenuItem base = new BaseMenuItem("Burger", 300.0);
        assertEquals(300.0, base.getPrice());
        assertEquals("Burger", base.getDescription());

        MenuItem withCheese = new ExtraCheeseDecorator(base);
        assertEquals(350.0, withCheese.getPrice());
        assertTrue(withCheese.getDescription().contains("Extra Cheese"));

        MenuItem withToppings = new ExtraToppingDecorator(withCheese);
        assertEquals(430.0, withToppings.getPrice());
        assertTrue(withToppings.getDescription().contains("Extra Toppings"));

        MenuItem full = new DrinkDecorator(withToppings, "Coke");
        assertEquals(530.0, full.getPrice());
        assertTrue(full.getDescription().contains("Coke"));
    }

    // ===================== STRATEGY TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("Strategy - Different delivery strategies calculate correctly")
    void testStrategy() {
        DeliveryStrategy standard = new StandardDeliveryStrategy();
        DeliveryStrategy express = new ExpressDeliveryStrategy();
        DeliveryStrategy scheduled = new ScheduledDeliveryStrategy();

        double dist = 5.0;

        assertEquals(100.0, standard.calculateCharge(dist), 0.01);
        assertEquals(200.0, express.calculateCharge(dist), 0.01);
        assertEquals(0.0, scheduled.calculateCharge(dist), 0.01);

        assertEquals("Standard Delivery", standard.getStrategyName());
        assertEquals("Express Delivery", express.getStrategyName());
        assertEquals("Scheduled Delivery", scheduled.getStrategyName());
    }

    // ===================== ADAPTER TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("Adapter - Payment gateways process through unified interface")
    void testAdapter() {
        PaymentAdapter khalti = new PaymentAdapter("KHALTI");
        assertEquals("Khalti", khalti.getGatewayName());
        assertTrue(khalti.processPayment(1000.0));

        PaymentAdapter esewa = new PaymentAdapter("ESEWA");
        assertEquals("eSewa", esewa.getGatewayName());
        assertTrue(esewa.processPayment(1000.0));

        PaymentAdapter paypal = new PaymentAdapter("PAYPAL");
        assertEquals("PayPal", paypal.getGatewayName());
        assertTrue(paypal.processPayment(1000.0));

        assertThrows(IllegalArgumentException.class, () -> new PaymentAdapter("BITCOIN"));
    }

    // ===================== OBSERVER TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("Observer - Notify all registered observers")
    void testObserver() {
        Customer c = new Customer("U003", "Observe", "o@t.com", "9800000000", "Ktm");
        Order order = new Order("ORD-TEST-OBS", c);

        List<String> messages = new ArrayList<>();
        OrderObserver logger = new OrderObserver() {
            @Override
            public void update(Order o, String msg) {
                messages.add(msg);
            }
        };

        order.attach(logger);

        order.notifyObservers("Test notification");
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("Test notification"));

        order.detach(logger);
        order.notifyObservers("Should not appear");
        assertEquals(1, messages.size());
    }

    // ===================== COMMAND TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(8)
    @DisplayName("Command - Execute and undo order commands")
    void testCommand() {
        Admin admin = new Admin("A001", "Admin", "a@t.com", "Ops");
        Customer cust = new Customer("U004", "CmdUser", "c@t.com", "9800000000", "Ktm");
        OrderFacade facade = new OrderFacade(admin);
        CommandInvoker invoker = new CommandInvoker();

        List<MenuItem> items = Arrays.asList(new BaseMenuItem("Item1", 100.0));
        List<Integer> qty = Arrays.asList(1);
        ArrayList<OrderObserver> obs = new ArrayList<>();

        OrderCommand place = new PlaceOrderCommand(
                facade, cust, items, qty,
                new StandardDeliveryStrategy(), 1.0, "KHALTI", obs);

        invoker.executeCommand(place);
        assertEquals(1, invoker.getHistorySize());

        invoker.undoLastCommand();
        assertEquals(0, invoker.getHistorySize());
    }

    // ===================== STATE TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(9)
    @DisplayName("State - Order lifecycle state transitions")
    void testState() {
        Customer c = new Customer("U005", "StateUser", "s@t.com", "9800000000", "Ktm");
        Order order = new Order("ORD-TEST-STATE", c);

        assertEquals("PENDING", order.getStatus());

        order.confirm();
        assertEquals("CONFIRMED", order.getStatus());

        order.prepare();
        assertEquals("PREPARING", order.getStatus());

        order.deliver();
        assertEquals("OUT_FOR_DELIVERY", order.getStatus());

        order.complete();
        assertEquals("DELIVERED", order.getStatus());

        order.complete();
        assertEquals("DELIVERED", order.getStatus());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    @DisplayName("State - Cancel from PENDING state")
    void testStateCancelFromPending() {
        Customer c = new Customer("U006", "CancelUser", "x@t.com", "9800000000", "Ktm");
        Order order = new Order("ORD-TEST-CANCEL", c);
        order.cancel();
        assertEquals("CANCELLED", order.getStatus());
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    @DisplayName("State - Cannot transition from DELIVERED to other states")
    void testStateDeliveredIsFinal() {
        Customer c = new Customer("U007", "FinalUser", "f@t.com", "9800000000", "Ktm");
        Order order = new Order("ORD-TEST-FINAL", c);
        order.confirm();
        order.prepare();
        order.deliver();
        order.complete();
        assertEquals("DELIVERED", order.getStatus());
        order.confirm();
        assertEquals("DELIVERED", order.getStatus());
        order.cancel();
        assertEquals("DELIVERED", order.getStatus());
    }

    // ===================== FACADE TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(12)
    @DisplayName("Facade - Simplified order placement flow")
    void testFacade() {
        Admin admin = new Admin("A002", "Admin", "a@t.com", "IT");
        Customer cust = new Customer("U008", "FacadeUser", "fu@t.com", "9800000000", "Ktm");
        OrderFacade facade = new OrderFacade(admin);

        List<MenuItem> items = Arrays.asList(new BaseMenuItem("Momo", 300.0));
        List<Integer> qty = Arrays.asList(2);
        ArrayList<OrderObserver> obs = new ArrayList<>();
        obs.add(new CustomerNotifier());

        String orderId = facade.placeOrder(cust, items, qty,
                new StandardDeliveryStrategy(), 3.0, "KHALTI", obs);

        assertNotNull(orderId);
        assertTrue(orderId.startsWith("ORD-"));

        Order order = facade.getOrder(orderId);
        assertNotNull(order);
        assertEquals(cust, order.getCustomer());

        String tracking = facade.trackOrder(orderId);
        assertTrue(tracking.contains(orderId));
    }

    // ===================== PROXY TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(13)
    @DisplayName("Proxy - Access control for sensitive operations")
    void testProxy() {
        Customer customer = new Customer("U009", "NormalUser", "n@t.com", "9800000000", "Ktm");
        Admin admin = new Admin("A003", "AdminUser", "ad@t.com", "IT");

        IOrderService proxyCustomer = new AuthProxy(customer);
        String report = proxyCustomer.generateReport("SUMMARY");
        assertEquals("Access Denied", report);

        IOrderService proxyAdmin = new AuthProxy(admin);
        String adminReport = proxyAdmin.generateReport("SUMMARY");
        assertNotEquals("Access Denied", adminReport);
        assertTrue(adminReport.contains("Total Orders"));
    }

    // ===================== REPORT GENERATOR TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(14)
    @DisplayName("Report - Generate order summary report")
    void testReportGenerator() {
        ReportGenerator rg = new ReportGenerator();
        List<Order> emptyOrders = new ArrayList<>();
        assertDoesNotThrow(() -> rg.generateOrderReport(emptyOrders));
    }

    // ===================== INTEGRATION TEST =====================

    @Test
    @org.junit.jupiter.api.Order(15)
    @DisplayName("Integration - Full system workflow")
    void testFullIntegration() {
        RestaurantConfig config = RestaurantConfig.getInstance();
        assertNotNull(config);

        UserFactory cf = new CustomerFactory();
        Customer customer = (Customer) cf.createUser("U010", "Integration",
                "int@t.com", "9800000000|Ktm");
        assertNotNull(customer);

        MenuItem item1 = new BaseMenuItem("Pasta", 400.0);
        MenuItem item2 = new ExtraCheeseDecorator(new BaseMenuItem("Pizza", 500.0));

        OrderBuilder builder = new OrderBuilder(customer)
                .addItem(item1, 1)
                .addItem(item2, 2)
                .setDeliveryStrategy(new ExpressDeliveryStrategy(), 4.0)
                .setPaymentMethod("PAYPAL");

        Order order = builder.build();
        assertNotNull(order);
        assertTrue(order.getTotalAmount() > 0);
        orderHistoryDummy.add(order);

        order.confirm();
        assertEquals("CONFIRMED", order.getStatus());
        order.prepare();
        assertEquals("PREPARING", order.getStatus());
        order.deliver();
        assertEquals("OUT_FOR_DELIVERY", order.getStatus());
        order.complete();
        assertEquals("DELIVERED", order.getStatus());

        assertNotNull(order.getDeliveryStrategy());
        assertTrue(order.getDeliveryStrategy() instanceof ExpressDeliveryStrategy);

        ReportGenerator rg = new ReportGenerator();
        assertDoesNotThrow(() -> rg.generateOrderReport(orderHistoryDummy));
    }

    private static List<Order> orderHistoryDummy = new ArrayList<>();

    // ===================== DATABASE TESTS =====================

    @Test
    @org.junit.jupiter.api.Order(16)
    @DisplayName("Database - User registration and authentication")
    void testDatabaseUserAuth() {
        assumeTrue(com.foodordering.db.DatabaseManager.getInstance().getConnection() != null,
                "PostgreSQL not available - skipping database test");
        String ts = String.valueOf(System.currentTimeMillis());
        String testId = "DB-AUTH-" + ts.substring(ts.length() - 6);
        String testEmail = "dbtest" + ts.substring(ts.length() - 6) + "@test.com";

        try {
            UserFactory cf = new CustomerFactory();
            Customer customer = (Customer) cf.createUser(testId, "DB User", testEmail,
                    "9800000000|Test Address");

            com.foodordering.db.UserDAO userDAO = new com.foodordering.db.UserDAO();
            boolean registered = userDAO.registerUser(customer, "password123");
            assertTrue(registered, "User should be registered");

            User authenticated = userDAO.authenticate(testEmail, "password123");
            assertNotNull(authenticated, "Should authenticate with correct password");
            assertEquals("DB User", authenticated.getName());

            User wrong = userDAO.authenticate(testEmail, "wrongpassword");
            assertNull(wrong, "Should not authenticate with wrong password");
        } finally {
            new com.foodordering.db.UserDAO().deleteUser(testId);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(17)
    @DisplayName("Database - Save and retrieve orders")
    void testDatabaseOrderPersistence() {
        assumeTrue(com.foodordering.db.DatabaseManager.getInstance().getConnection() != null,
                "PostgreSQL not available - skipping database test");
        String ts = String.valueOf(System.currentTimeMillis());
        String custId = "DB-ORD-" + ts.substring(ts.length() - 6);
        String email = "ordertest" + ts.substring(ts.length() - 6) + "@test.com";

        com.foodordering.db.UserDAO userDAO = new com.foodordering.db.UserDAO();
        com.foodordering.db.OrderDAO orderDAO = new com.foodordering.db.OrderDAO();

        Order order = null;
        try {
            UserFactory cf = new CustomerFactory();
            Customer cust = (Customer) cf.createUser(custId, "Order User", email,
                    "9800000000|Ktm");
            userDAO.registerUser(cust, "pass");

            Customer orderCustomer = (Customer) userDAO.findById(custId);
            assertNotNull(orderCustomer);

            MenuItem item = new BaseMenuItem("Test Item", 200.0);
            OrderBuilder builder = new OrderBuilder(orderCustomer)
                    .addItem(item, 2)
                    .setDeliveryStrategy(new StandardDeliveryStrategy(), 5.0)
                    .setPaymentMethod("KHALTI");
            order = builder.build();
            String savedOrderId = order.getOrderId();

            boolean saved = orderDAO.saveOrder(order);
            assertTrue(saved, "Order should be saved to database");

            List<Order> orders = orderDAO.findByCustomerId(custId);
            assertFalse(orders.isEmpty(), "Should find saved orders");
            assertTrue(orders.stream().anyMatch(o -> o.getOrderId().equals(savedOrderId)),
                    "Saved order should be retrievable");

            Order reloaded = orderDAO.findByOrderId(savedOrderId);
            assertNotNull(reloaded, "Order should be retrievable by ID");
            assertEquals(1, reloaded.getItems().size(), "Reloaded order should keep its line items");
            assertEquals("Test Item x 2", reloaded.getItems().get(0).getDescription(),
                    "Quantity suffix must not be doubled on reload");
        } finally {
            if (order != null) orderDAO.deleteOrder(order.getOrderId());
            userDAO.deleteUser(custId);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(18)
    @DisplayName("Database - Update profile and change password")
    void testDatabaseProfileUpdate() {
        assumeTrue(com.foodordering.db.DatabaseManager.getInstance().getConnection() != null,
                "PostgreSQL not available - skipping database test");
        String ts = String.valueOf(System.currentTimeMillis());
        String id = "DB-PRF-" + ts.substring(ts.length() - 6);
        String email = "prof" + ts.substring(ts.length() - 6) + "@test.com";

        com.foodordering.db.UserDAO userDAO = new com.foodordering.db.UserDAO();
        try {
            UserFactory cf = new CustomerFactory();
            Customer customer = (Customer) cf.createUser(id, "Profile User", email,
                    "9800000000|Ktm");
            assertTrue(userDAO.registerUser(customer, "oldpass"));

            customer.setName("Updated Name");
            customer.setPhone("9811111111");
            customer.setAddress("New Address");
            assertTrue(userDAO.updateProfile(customer), "Profile should be updated");

            User fetched = userDAO.findById(id);
            assertNotNull(fetched);
            assertEquals("Updated Name", fetched.getName());
            assertEquals("9811111111", ((Customer) fetched).getPhone());
            assertEquals("New Address", ((Customer) fetched).getAddress());

            assertTrue(userDAO.updatePassword(id, "newpass"), "Password should be updated");
            assertNotNull(userDAO.authenticate(email, "newpass"), "New password should authenticate");
            assertNull(userDAO.authenticate(email, "oldpass"), "Old password should no longer work");
        } finally {
            userDAO.deleteUser(id);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(19)
    @DisplayName("Command - Cancellation rejected for OUT_FOR_DELIVERY orders")
    void testCancelRejectedOutForDelivery() {
        Customer c = new Customer("U011", "Guard", "g@t.com", "9800000000", "Ktm");
        Order order = new Order("ORD-GUARD-" + System.currentTimeMillis(), c);
        OrderService svc = new OrderService();
        svc.placeOrder(order);
        order.confirm();
        order.prepare();
        order.deliver();
        assertEquals("OUT_FOR_DELIVERY", order.getStatus());

        assertFalse(svc.cancelOrder(order.getOrderId()),
                "Out-for-delivery orders must be rejected by the state machine");
        assertEquals("OUT_FOR_DELIVERY", order.getStatus(),
                "Rejected cancellation must not change the status");
    }

    @Test
    @org.junit.jupiter.api.Order(20)
    @DisplayName("Database - Delivery strategy restored on reload")
    void testDatabaseStrategyRestore() {
        assumeTrue(com.foodordering.db.DatabaseManager.getInstance().getConnection() != null,
                "PostgreSQL not available - skipping database test");
        String ts = String.valueOf(System.currentTimeMillis());
        String custId = "DB-STR-" + ts.substring(ts.length() - 6);
        String email = "strategy" + ts.substring(ts.length() - 6) + "@test.com";

        com.foodordering.db.UserDAO userDAO = new com.foodordering.db.UserDAO();
        com.foodordering.db.OrderDAO orderDAO = new com.foodordering.db.OrderDAO();

        Order order = null;
        try {
            UserFactory cf = new CustomerFactory();
            Customer cust = (Customer) cf.createUser(custId, "Strategy User", email,
                    "9800000000|Ktm");
            userDAO.registerUser(cust, "pass");

            OrderBuilder builder = new OrderBuilder(cust)
                    .addItem(new BaseMenuItem("Item", 100.0), 1)
                    .setDeliveryStrategy(new ExpressDeliveryStrategy(), 2.0)
                    .setPaymentMethod("KHALTI");
            order = builder.build();
            assertTrue(orderDAO.saveOrder(order), "Order should be saved");

            Order reloaded = orderDAO.findByOrderId(order.getOrderId());
            assertNotNull(reloaded);
            assertNotNull(reloaded.getDeliveryStrategy(),
                    "Reloaded order must restore its delivery strategy");
            assertTrue(reloaded.getDeliveryStrategy() instanceof ExpressDeliveryStrategy);
            assertEquals("15-20 minutes", reloaded.getDeliveryStrategy().getEstimatedTime());
        } finally {
            if (order != null) orderDAO.deleteOrder(order.getOrderId());
            userDAO.deleteUser(custId);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(21)
    @DisplayName("Proxy - Delivery role cannot place or cancel orders")
    void testProxyDeniesDeliveryRole() {
        DeliveryPartner partner = new DeliveryPartner("D900", "Partner", "d@t.com", "BA 1 PA 1234");
        Customer cust = new Customer("U900", "Cust", "c@t.com", "9800000000", "Ktm");
        OrderFacade facade = new OrderFacade(partner);

        String orderId = facade.placeOrder(cust,
                Arrays.asList(new BaseMenuItem("Item", 100.0)), List.of(1),
                new StandardDeliveryStrategy(), 1.0, "KHALTI", new ArrayList<>());
        assertNull(orderId, "Delivery partners must not be able to place orders");

        IOrderService proxy = new AuthProxy(partner);
        assertFalse(proxy.cancelOrder("ORD-NOPE"), "Delivery partners must not cancel orders");
    }

    @Test
    @org.junit.jupiter.api.Order(22)
    @DisplayName("Database - Delivery availability flag persists")
    void testDatabaseAvailabilityPersist() {
        assumeTrue(com.foodordering.db.DatabaseManager.getInstance().getConnection() != null,
                "PostgreSQL not available - skipping database test");
        String ts = String.valueOf(System.currentTimeMillis());
        String id = "DB-AVL-" + ts.substring(ts.length() - 6);
        String email = "avail" + ts.substring(ts.length() - 6) + "@test.com";

        com.foodordering.db.UserDAO userDAO = new com.foodordering.db.UserDAO();
        try {
            DeliveryPartnerFactory df = new DeliveryPartnerFactory();
            DeliveryPartner partner = (DeliveryPartner) df.createUser(id, "Avail User", email,
                    "BA 1 PA 0001");
            assertTrue(userDAO.registerUser(partner, "pass"));

            partner.setAvailable(false);
            assertTrue(userDAO.updateProfile(partner), "Availability should be persisted");

            User fetched = userDAO.findById(id);
            assertNotNull(fetched);
            assertFalse(((DeliveryPartner) fetched).isAvailable(),
                    "Reloaded partner should be unavailable");
        } finally {
            userDAO.deleteUser(id);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(23)
    @DisplayName("Database - Notification rename keys on user id, not name")
    void testDatabaseNotificationRenameByUserId() {
        assumeTrue(com.foodordering.db.DatabaseManager.getInstance().getConnection() != null,
                "PostgreSQL not available - skipping database test");
        String ts = String.valueOf(System.currentTimeMillis());
        String custId = "DB-NTF-" + ts.substring(ts.length() - 6);
        String email = "notif" + ts.substring(ts.length() - 6) + "@test.com";

        com.foodordering.db.UserDAO userDAO = new com.foodordering.db.UserDAO();
        com.foodordering.db.OrderDAO orderDAO = new com.foodordering.db.OrderDAO();
        com.foodordering.db.NotificationDAO notificationDAO = new com.foodordering.db.NotificationDAO();

        Order order = null;
        try {
            UserFactory cf = new CustomerFactory();
            Customer cust = (Customer) cf.createUser(custId, "Old Name", email,
                    "9800000000|Ktm");
            userDAO.registerUser(cust, "pass");

            OrderBuilder builder = new OrderBuilder(cust)
                    .addItem(new BaseMenuItem("Test Item", 50.0), 1)
                    .setDeliveryStrategy(new StandardDeliveryStrategy(), 1.0)
                    .setPaymentMethod("KHALTI");
            order = builder.build();
            orderDAO.saveOrder(order);

            notificationDAO.saveNotification(order.getOrderId(), cust.getId(), "Old Name",
                    "Hello notification");
            assertTrue(notificationDAO.listNotifications(cust.getId(), "Old Name")
                            .contains("Hello notification"),
                    "Notification should be visible under the original name");

            notificationDAO.renameRecipient(cust.getId(), "Brand New Name");
            assertTrue(notificationDAO.listNotifications(cust.getId(), "Brand New Name")
                            .contains("Hello notification"),
                    "Notification must survive a rename keyed by user id");
            assertTrue(notificationDAO.listNotifications("SOME-OTHER-USER", "Old Name").isEmpty(),
                    "Another user with the same name must not see this notification");
        } finally {
            if (order != null) orderDAO.deleteOrder(order.getOrderId());
            userDAO.deleteUser(custId);
        }
    }

    @Test
    @org.junit.jupiter.api.Order(24)
    @DisplayName("Command - Invoker records only successful commands")
    void testCommandInvokerRecordsOnlySuccesses() {
        CommandInvoker invoker = new CommandInvoker();

        OrderCommand ok = new OrderCommand() {
            @Override public boolean execute() { return true; }
            @Override public boolean undo() { return false; }
            @Override public String getDescription() { return "ok"; }
        };
        OrderCommand failing = new OrderCommand() {
            @Override public boolean execute() { return false; }
            @Override public boolean undo() { return false; }
            @Override public String getDescription() { return "failing"; }
        };

        assertTrue(invoker.executeCommand(ok), "Successful command should report success");
        assertEquals(1, invoker.getHistorySize());
        assertFalse(invoker.executeCommand(failing), "Failed command must report failure");
        assertEquals(1, invoker.getHistorySize(), "Failed commands must not be recorded");
    }
}
