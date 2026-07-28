package com.foodordering.interactive;

import com.foodordering.adapter.PaymentAdapter;
import com.foodordering.builder.OrderBuilder;
import com.foodordering.command.*;
import com.foodordering.config.RestaurantConfig;
import com.foodordering.db.*;
import com.foodordering.decorator.*;
import com.foodordering.facade.OrderFacade;
import com.foodordering.factory.*;
import com.foodordering.model.*;
import com.foodordering.observer.*;
import com.foodordering.proxy.AuthProxy;
import com.foodordering.proxy.IOrderService;
import com.foodordering.proxy.OrderService;
import com.foodordering.report.ReportGenerator;
import com.foodordering.strategy.*;

import java.util.*;

public class InteractiveMenu {
    private final Scanner scanner;
    private final InputHelper input;
    private final UserDAO userDAO;
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO orderDAO;
    private final NotificationDAO notificationDAO;

    private User currentUser;
    private final CommandInvoker commandInvoker;

    public InteractiveMenu() {
        this.scanner = new Scanner(System.in);
        this.input = new InputHelper(scanner);
        this.userDAO = new UserDAO();
        this.menuItemDAO = new MenuItemDAO();
        this.orderDAO = new OrderDAO();
        this.notificationDAO = new NotificationDAO();
        this.commandInvoker = new CommandInvoker();
    }

    public void start() {
        RestaurantConfig.getInstance();

        while (true) {
            printBanner();
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("=========================================");
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Exit");
        System.out.println("=========================================");
        int choice = input.readInt("  Choice: ");
        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 3 -> {
                System.out.println("\n  Thank you for using FoodieExpress!");
                DatabaseManager.getInstance().shutdown();
                System.exit(0);
            }
            default -> System.out.println("  Invalid choice.");
        }
    }

    private void login() {
        System.out.println("\n=========================================");
        System.out.println("  LOGIN");
        System.out.println("=========================================");
        String email = input.readLine("  Email: ");
        String password = input.readLine("  Password: ");
        User user = userDAO.authenticate(email, password);
        if (user != null) {
            currentUser = user;
            System.out.println("  Welcome back, " + user.getName() + "! [" + user.getRole() + "]");
        } else {
            System.out.println("  Invalid email or password.");
        }
        input.pressEnter();
    }

    private void register() {
        System.out.println("\n=========================================");
        System.out.println("  REGISTER - FACTORY METHOD PATTERN");
        System.out.println("  (User creation via concrete factories)");
        System.out.println("=========================================");
        System.out.println("  1. Customer");
        System.out.println("  2. Admin");
        System.out.println("  3. Delivery Partner");
        int roleChoice = input.readInt("  Select role: ");

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String name = input.readLine("  Name: ");
        String email = input.readLine("  Email: ");
        String password = input.readLine("  Password: ");

        if (userDAO.findByEmail(email) != null) {
            System.out.println("  Email already registered.");
            input.pressEnter();
            return;
        }

        UserFactory factory;
        String extra;

        switch (roleChoice) {
            case 1 -> {
                id = "C" + id;
                factory = new CustomerFactory();
                String phone = input.readLine("  Phone: ");
                String address = input.readLine("  Address: ");
                extra = phone + "|" + address;
            }
            case 2 -> {
                id = "A" + id;
                factory = new AdminFactory();
                extra = input.readLine("  Department: ");
            }
            case 3 -> {
                id = "D" + id;
                factory = new DeliveryPartnerFactory();
                extra = input.readLine("  Vehicle Number: ");
            }
            default -> {
                System.out.println("  Invalid.");
                return;
            }
        }

        System.out.println("  [Factory Method] Creating via " + factory.getClass().getSimpleName() + "...");
        User newUser = factory.createAndRegister(id, name, email, extra);

        if (userDAO.registerUser(newUser, password)) {
            System.out.println("  Registration successful! You can now log in.");
        } else {
            System.out.println("  Registration failed.");
        }
        input.pressEnter();
    }

    private void showMainMenu() {
        System.out.println("  Logged in as: " + currentUser.getName() + " [" + currentUser.getRole() + "]");
        switch (currentUser.getRole()) {
            case "ADMIN" -> showAdminMenu();
            case "DELIVERY" -> showDeliveryMenu();
            default -> showCustomerMenu();
        }
    }

    // ==================== CUSTOMER ====================

    private void showCustomerMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("  CUSTOMER MENU");
            System.out.println("=========================================");
            System.out.println("  1. Browse Menu");
            System.out.println("  2. Customize Item (Decorator Pattern)");
            System.out.println("  3. Place New Order (Builder+Strategy+Adapter+Facade)");
            System.out.println("  4. Track My Orders (State Pattern)");
            System.out.println("  5. Cancel an Order (Command Pattern)");
            System.out.println("  6. View Notifications (Observer Pattern)");
            System.out.println("  7. Logout");
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> browseMenu();
                case 2 -> customizeItemDemo();
                case 3 -> placeOrder();
                case 4 -> trackOrders();
                case 5 -> cancelOrder();
                case 6 -> viewNotifications();
                case 7 -> { currentUser = null; return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ==================== ADMIN ====================

    private void showAdminMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("  ADMIN MENU");
            System.out.println("=========================================");
            System.out.println("  1. View All Orders");
            System.out.println("  2. Process Order (State Pattern)");
            System.out.println("  3. Generate Report (Proxy Pattern)");
            System.out.println("  4. Manage Menu Items");
            System.out.println("  5. View All Notifications");
            System.out.println("  6. Logout");
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> viewAllOrders();
                case 2 -> processOrder();
                case 3 -> generateReport();
                case 4 -> manageMenu();
                case 5 -> viewAllNotifications();
                case 6 -> { currentUser = null; return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ==================== DELIVERY ====================

    private void showDeliveryMenu() {
        while (true) {
            System.out.println("\n=========================================");
            System.out.println("  DELIVERY PARTNER MENU");
            System.out.println("=========================================");
            System.out.println("  1. View Orders Out for Delivery");
            System.out.println("  2. Mark Order as Delivered");
            System.out.println("  3. Logout");
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> viewOutForDelivery();
                case 2 -> markDelivered();
                case 3 -> { currentUser = null; return; }
                default -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ==================== CUSTOMER FEATURES ====================

    private void browseMenu() {
        System.out.println("\n=========================================");
        System.out.println("  MENU ITEMS");
        System.out.println("=========================================");
        List<MenuItem> items = menuItemDAO.findAllAvailable();
        if (items.isEmpty()) {
            System.out.println("  No items available.");
        } else {
            System.out.printf("  %-3s %-25s %s%n", "#", "Item", "Price");
            System.out.println("  " + "-".repeat(45));
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("  %-3d %-25s NPR %,.2f%n",
                        i + 1, items.get(i).getDescription(), items.get(i).getPrice());
            }
        }
        input.pressEnter();
    }

    private void customizeItemDemo() {
        System.out.println("\n=========================================");
        System.out.println("  DECORATOR PATTERN - ITEM CUSTOMIZATION");
        System.out.println("=========================================");

        List<MenuItem> available = menuItemDAO.findAllAvailable();
        if (available.isEmpty()) {
            System.out.println("  No items available.");
            input.pressEnter();
            return;
        }

        System.out.println("  Select a base item:");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("  %d. %-25s NPR %,.2f%n", i + 1,
                    available.get(i).getDescription(), available.get(i).getPrice());
        }
        int itemChoice = input.readInt("  Choice: ") - 1;
        if (itemChoice < 0 || itemChoice >= available.size()) {
            System.out.println("  Invalid choice.");
            input.pressEnter();
            return;
        }

        MenuItem customized = available.get(itemChoice);
        System.out.println("\n  Starting with: " + customized.getDescription() +
                " - NPR " + String.format("%,.2f", customized.getPrice()));

        while (true) {
            System.out.println("\n  [Decorator] Add extras:");
            System.out.println("  1. Extra Cheese (+NPR 50)");
            System.out.println("  2. Extra Toppings (+NPR 80)");
            System.out.println("  3. Add Drink (+NPR 100)");
            System.out.println("  4. Done");
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> {
                    customized = new ExtraCheeseDecorator(customized);
                    System.out.println("  + Extra Cheese");
                }
                case 2 -> {
                    customized = new ExtraToppingDecorator(customized);
                    System.out.println("  + Extra Toppings");
                }
                case 3 -> {
                    String drink = input.readLine("  Drink name: ");
                    customized = new DrinkDecorator(customized, drink);
                    System.out.println("  + " + drink);
                }
                case 4 -> {
                    System.out.println("\n  FINAL CUSTOMIZED ITEM:");
                    System.out.println("  " + customized.getDescription());
                    System.out.println("  Price: NPR " + String.format("%,.2f", customized.getPrice()));

                    if (input.readYesNo("\n  Add this to your order?")) {
                        placeOrderWithItems(List.of(customized), List.of(1));
                    }
                    return;
                }
                default -> System.out.println("  Invalid.");
            }
            System.out.println("  Current: " + customized.getDescription() +
                    " - NPR " + String.format("%,.2f", customized.getPrice()));
        }
    }

    private void placeOrder() {
        if (!(currentUser instanceof Customer customer)) {
            System.out.println("  Only customers can place orders.");
            input.pressEnter();
            return;
        }

        System.out.println("\n=========================================");
        System.out.println("  PLACE ORDER");
        System.out.println("  Patterns: Builder + Strategy + Adapter + Facade");
        System.out.println("=========================================");

        List<MenuItem> selectedItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        List<MenuItem> menuItems = menuItemDAO.findAllAvailable();

        if (menuItems.isEmpty()) {
            System.out.println("  No menu items available.");
            input.pressEnter();
            return;
        }

        while (true) {
            System.out.println("\n  Select items (0 to finish):");
            for (int i = 0; i < menuItems.size(); i++) {
                System.out.printf("  %d. %-25s NPR %,.2f%n", i + 1,
                        menuItems.get(i).getDescription(), menuItems.get(i).getPrice());
            }
            int sel = input.readInt("  Item: ");
            if (sel == 0) break;
            if (sel < 1 || sel > menuItems.size()) {
                System.out.println("  Invalid.");
                continue;
            }

            MenuItem item = menuItems.get(sel - 1);
            if (input.readYesNo("  Customize with Decorator?")) {
                item = customizeItemInline(item);
            }
            int qty = input.readInt("  Quantity: ");
            selectedItems.add(item);
            quantities.add(qty);
            System.out.println("  Added: " + item.getDescription() + " x " + qty);
        }

        if (selectedItems.isEmpty()) {
            System.out.println("  No items selected.");
            input.pressEnter();
            return;
        }

        // Strategy
        System.out.println("\n  --- STRATEGY PATTERN: DELIVERY ---");
        System.out.println("  1. Standard (NPR 20/km, 30-45 min)");
        System.out.println("  2. Express (NPR 20/km + 100, 15-20 min)");
        System.out.println("  3. Scheduled (Free)");
        int stratChoice = input.readInt("  Choice: ");
        double distance = input.readDouble("  Distance (km): ");

        DeliveryStrategy strategy = switch (stratChoice) {
            case 2 -> new ExpressDeliveryStrategy();
            case 3 -> new ScheduledDeliveryStrategy();
            default -> new StandardDeliveryStrategy();
        };
        System.out.println("  [Strategy] " + strategy.getStrategyName() +
                " | Charge: NPR " + String.format("%,.2f", strategy.calculateCharge(distance)));

        // Payment
        System.out.println("\n  --- ADAPTER PATTERN: PAYMENT ---");
        System.out.println("  1. Khalti  2. eSewa  3. PayPal");
        int payChoice = input.readInt("  Choice: ");
        String paymentMethod = switch (payChoice) {
            case 2 -> "ESEWA";
            case 3 -> "PAYPAL";
            default -> "KHALTI";
        };

        placeOrderWithItems(selectedItems, quantities, strategy, distance, paymentMethod);
    }

    private void placeOrderWithItems(List<MenuItem> items, List<Integer> quantities) {
        placeOrderWithItems(items, quantities, new StandardDeliveryStrategy(), 3.0, "KHALTI");
    }

    private void placeOrderWithItems(List<MenuItem> items, List<Integer> quantities,
                                      DeliveryStrategy strategy, double distance,
                                      String paymentMethod) {
        if (!(currentUser instanceof Customer customer)) return;

        System.out.println("\n  --- FACADE PATTERN: ORDER FACADE ---");
        OrderFacade facade = new OrderFacade(currentUser);

        List<OrderObserver> observers = List.of(
                new CustomerNotifier(), new RestaurantNotifier());

        String orderId = facade.placeOrder(customer, items, quantities,
                strategy, distance, paymentMethod, observers);

        if (orderId != null) {
            Order order = facade.getOrder(orderId);
            if (order != null) {
                orderDAO.saveOrder(order);
                notificationDAO.saveNotification(orderId, customer.getName(),
                        "Order " + orderId + " placed successfully.");
                System.out.println("\n  Order saved to database!");
                System.out.println("  Order ID: " + orderId);
                System.out.println("  Total: NPR " + String.format("%,.2f", order.getTotalAmount()));
            }
        } else {
            System.out.println("  Order placement failed.");
        }
        input.pressEnter();
    }

    private MenuItem customizeItemInline(MenuItem item) {
        System.out.println("  [Decorator] Customize:");
        while (true) {
            System.out.println("    1. Extra Cheese (+50)  2. Extra Toppings (+80)  3. Add Drink (+100)  4. Done");
            int c = input.readInt("    Choice: ");
            switch (c) {
                case 1 -> item = new ExtraCheeseDecorator(item);
                case 2 -> item = new ExtraToppingDecorator(item);
                case 3 -> item = new DrinkDecorator(item, input.readLine("    Drink: "));
                case 4 -> { return item; }
                default -> System.out.println("    Invalid.");
            }
            System.out.println("    Current: " + item.getDescription() +
                    " - NPR " + String.format("%,.2f", item.getPrice()));
        }
    }

    private void trackOrders() {
        System.out.println("\n=========================================");
        System.out.println("  STATE PATTERN - TRACK ORDERS");
        System.out.println("=========================================");

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getId());
        if (orders.isEmpty()) {
            System.out.println("  No orders.");
            input.pressEnter();
            return;
        }

        System.out.printf("  %-15s %-15s %-12s%n", "Order ID", "Status", "Amount");
        System.out.println("  " + "-".repeat(45));
        for (Order o : orders) {
            System.out.printf("  %-15s %-15s NPR %,.2f%n",
                    o.getOrderId(), o.getStatus(), o.getTotalAmount());
        }

        System.out.println("\n  [State Pattern] Order Lifecycle:");
        System.out.println("  PENDING -> CONFIRMED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED");
        System.out.println("  Cancel allowed from: PENDING, CONFIRMED, PREPARING");

        if (input.readYesNo("  View state transitions for an order?")) {
            String oid = input.readLine("  Order ID: ");
            orders.stream()
                    .filter(o -> o.getOrderId().equalsIgnoreCase(oid))
                    .findFirst().ifPresentOrElse(
                            o -> System.out.println("  Current state: " + o.getStatus()),
                            () -> System.out.println("  Not found."));
        }
        input.pressEnter();
    }

    private void cancelOrder() {
        System.out.println("\n=========================================");
        System.out.println("  COMMAND PATTERN - CANCEL ORDER");
        System.out.println("=========================================");

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getId());
        List<Order> cancellable = orders.stream()
                .filter(o -> !o.getStatus().equals("DELIVERED") && !o.getStatus().equals("CANCELLED"))
                .toList();

        if (cancellable.isEmpty()) {
            System.out.println("  No cancellable orders.");
            input.pressEnter();
            return;
        }

        for (int i = 0; i < cancellable.size(); i++) {
            System.out.printf("  %d. %s - %s%n", i + 1,
                    cancellable.get(i).getOrderId(), cancellable.get(i).getStatus());
        }
        int choice = input.readInt("  Select: ") - 1;
        if (choice < 0 || choice >= cancellable.size()) return;

        Order toCancel = cancellable.get(choice);
        CancelOrderCommand cmd = new CancelOrderCommand(
                new OrderFacade(currentUser), toCancel.getOrderId(), currentUser);
        commandInvoker.executeCommand(cmd);

        toCancel.cancel();
        orderDAO.updateStatus(toCancel.getOrderId(), "CANCELLED");
        notificationDAO.saveNotification(toCancel.getOrderId(), currentUser.getName(),
                "Order " + toCancel.getOrderId() + " cancelled.");

        System.out.println("  [Command] History size: " + commandInvoker.getHistorySize());
        if (input.readYesNo("  Undo? (Command Undo)")) {
            commandInvoker.undoLastCommand();
        }
        input.pressEnter();
    }

    private void viewNotifications() {
        System.out.println("\n=========================================");
        System.out.println("  OBSERVER PATTERN - NOTIFICATIONS");
        System.out.println("=========================================");
        notificationDAO.printNotificationsForUser(currentUser.getName());
        input.pressEnter();
    }

    // ==================== ADMIN FEATURES ====================

    private void viewAllOrders() {
        System.out.println("\n=========================================");
        System.out.println("  ALL ORDERS");
        System.out.println("=========================================");
        List<Order> orders = orderDAO.findAll();
        if (orders.isEmpty()) {
            System.out.println("  No orders.");
            input.pressEnter();
            return;
        }
        System.out.printf("  %-15s %-20s %-15s %-12s%n", "Order ID", "Customer", "Status", "Amount");
        System.out.println("  " + "-".repeat(65));
        for (Order o : orders) {
            System.out.printf("  %-15s %-20s %-15s NPR %,.2f%n",
                    o.getOrderId(), o.getCustomer().getName(), o.getStatus(), o.getTotalAmount());
        }
        input.pressEnter();
    }

    private void processOrder() {
        System.out.println("\n=========================================");
        System.out.println("  STATE PATTERN - PROCESS ORDER");
        System.out.println("=========================================");

        List<Order> orders = orderDAO.findAll().stream()
                .filter(o -> !o.getStatus().equals("DELIVERED") && !o.getStatus().equals("CANCELLED"))
                .toList();

        if (orders.isEmpty()) {
            System.out.println("  No orders to process.");
            input.pressEnter();
            return;
        }

        for (int i = 0; i < orders.size(); i++) {
            System.out.printf("  %d. %s - %s - NPR %,.2f%n", i + 1,
                    orders.get(i).getOrderId(), orders.get(i).getStatus(),
                    orders.get(i).getTotalAmount());
        }
        int choice = input.readInt("  Select order: ") - 1;
        if (choice < 0 || choice >= orders.size()) return;

        Order order = orders.get(choice);
        System.out.println("\n  Current state: " + order.getStatus());
        System.out.println("  1. Confirm  2. Prepare  3. Deliver  4. Complete  5. Cancel");
        int action = input.readInt("  Action: ");

        System.out.print("  [State] " + order.getStatus() + " -> ");
        switch (action) {
            case 1 -> { order.confirm(); orderDAO.updateStatus(order.getOrderId(), "CONFIRMED"); }
            case 2 -> { order.prepare(); orderDAO.updateStatus(order.getOrderId(), "PREPARING"); }
            case 3 -> { order.deliver(); orderDAO.updateStatus(order.getOrderId(), "OUT_FOR_DELIVERY"); }
            case 4 -> {
                order.complete();
                orderDAO.updateStatus(order.getOrderId(), "DELIVERED");
                notificationDAO.saveNotification(order.getOrderId(), order.getCustomer().getName(),
                        "Your order " + order.getOrderId() + " has been delivered!");
            }
            case 5 -> { order.cancel(); orderDAO.updateStatus(order.getOrderId(), "CANCELLED"); }
        }
        System.out.println(order.getStatus());
        input.pressEnter();
    }

    private void generateReport() {
        System.out.println("\n=========================================");
        System.out.println("  PROXY PATTERN - GENERATE REPORT");
        System.out.println("=========================================");

        IOrderService proxy = new AuthProxy(currentUser, new OrderService());
        System.out.println("  [Proxy] " + currentUser.getName() +
                " (" + currentUser.getRole() + ") requesting report...");

        String report = proxy.generateReport("SUMMARY");
        if (report.equals("Access Denied")) {
            System.out.println("  [Proxy] ACCESS DENIED: Only ADMIN can generate reports.");
        } else {
            System.out.println("  [Proxy] Access granted.");
            System.out.println("\n" + report);

            List<Order> allOrders = orderDAO.findAll();
            if (!allOrders.isEmpty()) {
                new ReportGenerator().generateOrderReport(allOrders);
            }
        }
        input.pressEnter();
    }

    private void manageMenu() {
        System.out.println("\n=========================================");
        System.out.println("  MANAGE MENU");
        System.out.println("=========================================");
        System.out.println("  1. View All Items");
        System.out.println("  2. Add New Item");
        System.out.println("  3. Toggle Availability");
        int c = input.readInt("  Choice: ");
        switch (c) {
            case 1 -> menuItemDAO.findAll().forEach(item ->
                    System.out.println("  - " + item.getDescription() +
                            " - NPR " + String.format("%,.2f", item.getPrice())));
            case 2 -> {
                String name = input.readLine("  Name: ");
                double price = input.readDouble("  Price: ");
                System.out.println(menuItemDAO.addItem(name, price) ? "  Added." : "  Failed.");
            }
            case 3 -> {
                String name = input.readLine("  Item name: ");
                boolean avail = input.readYesNo("  Available?");
                menuItemDAO.toggleAvailability(name, avail);
            }
        }
        input.pressEnter();
    }

    private void viewAllNotifications() {
        System.out.println("\n=========================================");
        System.out.println("  NOTIFICATIONS (OBSERVER PATTERN)");
        System.out.println("=========================================");
        notificationDAO.printNotificationsForUser("%");
        input.pressEnter();
    }

    // ==================== DELIVERY FEATURES ====================

    private void viewOutForDelivery() {
        System.out.println("\n=========================================");
        System.out.println("  OUT FOR DELIVERY");
        System.out.println("=========================================");
        orderDAO.findAll().stream()
                .filter(o -> o.getStatus().equals("OUT_FOR_DELIVERY"))
                .forEach(o -> System.out.printf("  %s - %s - NPR %,.2f%n",
                        o.getOrderId(), o.getCustomer().getName(), o.getTotalAmount()));
        input.pressEnter();
    }

    private void markDelivered() {
        System.out.println("\n=========================================");
        System.out.println("  MARK DELIVERED");
        System.out.println("=========================================");
        List<Order> deliveries = orderDAO.findAll().stream()
                .filter(o -> o.getStatus().equals("OUT_FOR_DELIVERY"))
                .toList();
        if (deliveries.isEmpty()) {
            System.out.println("  No orders to deliver.");
            input.pressEnter();
            return;
        }
        for (int i = 0; i < deliveries.size(); i++) {
            System.out.printf("  %d. %s - %s%n", i + 1,
                    deliveries.get(i).getOrderId(), deliveries.get(i).getCustomer().getName());
        }
        int choice = input.readInt("  Select: ") - 1;
        if (choice >= 0 && choice < deliveries.size()) {
            Order o = deliveries.get(choice);
            o.complete();
            orderDAO.updateStatus(o.getOrderId(), "DELIVERED");
            notificationDAO.saveNotification(o.getOrderId(), o.getCustomer().getName(),
                    "Your order " + o.getOrderId() + " has been delivered!");
            System.out.println("  Delivered!");
        }
        input.pressEnter();
    }

    // ==================== HELPERS ====================

    private void printBanner() {
        System.out.println();
        System.out.println("=========================================");
        System.out.println("  FOODIEEXPRESS - ONLINE FOOD ORDERING");
        System.out.println("  Interactive Console Application");
        System.out.println("  11 Design Patterns | H2 Database");
        System.out.println("=========================================");
    }
}
