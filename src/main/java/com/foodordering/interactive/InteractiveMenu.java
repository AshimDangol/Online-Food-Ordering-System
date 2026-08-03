package com.foodordering.interactive;

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

/**
 * Main interactive console interface for the Online Food Ordering System.
 * Provides role-based menus for Customers, Admins, and Delivery Partners.
 * Each menu option demonstrates one or more GoF Design Patterns:
 *
 * <ul>
 *   <li><b>Singleton</b> — RestaurantConfig and DatabaseManager</li>
 *   <li><b>Factory Method</b> — User registration via UserFactory hierarchy</li>
 *   <li><b>Builder</b> — Order construction via OrderBuilder</li>
 *   <li><b>Decorator</b> — Item customization with cheese, toppings, drinks</li>
 *   <li><b>Strategy</b> — Delivery charge calculation (Standard/Express/Scheduled)</li>
 *   <li><b>Adapter</b> — Payment processing via Khalti/eSewa/PayPal</li>
 *   <li><b>Facade</b> — OrderFacade simplifies the ordering workflow</li>
 *   <li><b>Proxy</b> — AuthProxy controls report access</li>
 *   <li><b>Observer</b> — Notifications on order status changes</li>
 *   <li><b>Command</b> — Cancel orders with undo support</li>
 *   <li><b>State</b> — Order lifecycle transitions</li>
 * </ul>
 *
 * All data is persisted to a PostgreSQL database via DAO objects.
 */
public class InteractiveMenu {
    private final Scanner scanner;
    private final InputHelper input;
    private final UserDAO userDAO;
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO orderDAO;
    private final NotificationDAO notificationDAO;

    private User currentUser;
    private final CommandInvoker commandInvoker;

    /** Initializes the interactive menu with DAOs and input helper. */
    public InteractiveMenu() {
        this.scanner = new Scanner(System.in);
        this.input = new InputHelper(scanner);
        this.userDAO = new UserDAO();
        this.menuItemDAO = new MenuItemDAO();
        this.orderDAO = new OrderDAO();
        this.notificationDAO = new NotificationDAO();
        this.commandInvoker = new CommandInvoker();
    }

    /**
     * Starts the main application loop.
     * Displays the auth menu when no user is logged in,
     * or the role-based main menu after authentication.
     */
    public void start() {
        RestaurantConfig.getInstance();
        printBanner();
        RestaurantConfig.getInstance().display();

        if (DatabaseManager.getInstance().getConnection() == null) {
            fail("  \u2717 Could not connect to the database. Exiting.");
            return;
        }

        while (true) {
            System.out.println();
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // ==================== AUTHENTICATION ====================

    /** Displays the login/register/exit menu. */
    private void showAuthMenu() {
        printMenuBox("\u2726", "WELCOME GUEST",
                List.of("1. Login", "2. Register", "3. Exit"));
        int choice = input.readInt("  Choice: ");
        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 3 -> {
                System.out.println("\n  " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN,
                        "Thank you for using FoodieExpress!"));
                DatabaseManager.getInstance().shutdown();
                System.exit(0);
            }
            default -> fail("  Invalid choice.");
        }
    }

    /** Authenticates the user via UserDAO and sets currentUser on success. */
    private void login() {
        printHeader("LOGIN");
        String email = input.readLine("  Email: ");
        String password = input.readLine("  Password: ");
        User user = userDAO.authenticate(email, password);
        if (user != null) {
            currentUser = user;
            ok("  \u2713 Welcome back, " + user.getName() + "! [" + user.getRole() + "]");
        } else {
            fail("  \u2717 Invalid email or password.");
        }
        input.pressEnter();
    }

    /**
     * Registers a new user using the Factory Method pattern.
     * The user selects a role, and the corresponding concrete factory
     * (CustomerFactory, AdminFactory, or DeliveryPartnerFactory)
     * creates the appropriate User subclass.
     */
    private void register() {
        printHeader("REGISTER");
        printSubheader("Factory Method Pattern \u2014 User creation via concrete factories");
        System.out.println("  Select user role:");
        System.out.println("    1. Customer");
        System.out.println("    2. Admin");
        System.out.println("    3. Delivery Partner");
        int roleChoice = input.readInt("  Role: ");

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String name = input.readLine("  Name: ");
        String email = input.readLine("  Email: ");
        String password = input.readLine("  Password: ");

        if (userDAO.findByEmail(email) != null) {
            fail("  Email already registered.");
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
                if (!userDAO.findAllByRole("ADMIN").isEmpty()) {
                    fail("  \u2717 Admin registration is locked \u2014 an admin account already exists.");
                    input.pressEnter();
                    return;
                }
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
                fail("  Invalid.");
                return;
            }
        }

        info("  [Factory Method] Creating via " + factory.getClass().getSimpleName() + "...");
        User newUser = factory.createAndRegister(id, name, email, extra);

        if (userDAO.registerUser(newUser, password)) {
            ok("  \u2713 Registration successful! You can now log in.");
        } else {
            fail("  \u2717 Registration failed.");
        }
        input.pressEnter();
    }

    /** Routes to the appropriate role-based menu. */
    private void showMainMenu() {
        info("  Logged in: " + currentUser.getName() + " \u2502 " + currentUser.getRole());
        printSeparator();
        switch (currentUser.getRole()) {
            case "ADMIN" -> showAdminMenu();
            case "DELIVERY" -> showDeliveryMenu();
            default -> showCustomerMenu();
        }
    }

    // ==================== CUSTOMER MENU ====================

    /** Displays the customer menu loop with options for ordering, tracking, and account management. */
    private void showCustomerMenu() {
        while (true) {
            System.out.println();
            printMenuBox("\u25BC", "CUSTOMER MENU",
                    List.of("1. Browse Menu",
                            "2. Customize Item [Decorator]",
                            "3. Place Order [Build+Str+Adp+Fcd]",
                            "4. Track Orders [State]",
                            "5. Cancel Order [Command]",
                            "6. Notifications [Observer]",
                            "7. Logout"));
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> browseMenu();
                case 2 -> customizeItemDemo();
                case 3 -> placeOrder();
                case 4 -> trackOrders();
                case 5 -> cancelOrder();
                case 6 -> viewNotifications();
                case 7 -> { currentUser = null; return; }
                default -> fail("  Invalid choice.");
            }
        }
    }

    // ==================== ADMIN MENU ====================

    /** Displays the admin menu loop with order processing, reporting, and menu management. */
    private void showAdminMenu() {
        while (true) {
            System.out.println();
            printMenuBox("\u2699", "ADMIN MENU",
                    List.of("1. View All Orders",
                            "2. Process Order [State]",
                            "3. Generate Report [Proxy]",
                            "4. Manage Menu Items",
                            "5. View All Notifications",
                            "6. Logout"));
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> viewAllOrders();
                case 2 -> processOrder();
                case 3 -> generateReport();
                case 4 -> manageMenu();
                case 5 -> viewAllNotifications();
                case 6 -> { currentUser = null; return; }
                default -> fail("  Invalid choice.");
            }
        }
    }

    // ==================== DELIVERY MENU ====================

    /** Displays the delivery partner menu for managing out-for-delivery orders. */
    private void showDeliveryMenu() {
        while (true) {
            System.out.println();
            printMenuBox("\u26A1", "DELIVERY PARTNER MENU",
                    List.of("1. View Out for Delivery",
                            "2. Mark as Delivered",
                            "3. Logout"));
            int choice = input.readInt("  Choice: ");

            switch (choice) {
                case 1 -> viewOutForDelivery();
                case 2 -> markDelivered();
                case 3 -> { currentUser = null; return; }
                default -> fail("  Invalid choice.");
            }
        }
    }

    // ==================== CUSTOMER FEATURES ====================

    /** Displays available menu items loaded from the database. */
    private void browseMenu() {
        printHeader("MENU ITEMS");
        List<MenuItem> items = menuItemDAO.findAllAvailable();
        if (items.isEmpty()) {
            System.out.println("  No items available.");
        } else {
            printTableHeader(String.format("%-3s", "#"),
                    String.format("%-27s", "Item"),
                    String.format("%12s", "Price"));
            for (int i = 0; i < items.size(); i++) {
                printTableRow(
                        String.format("%-3d", i + 1),
                        String.format("%-27s", items.get(i).getDescription()),
                        String.format("%12s", fmt(items.get(i).getPrice())));
            }
            printTableFooter(String.format("%-3s", "#"),
                    String.format("%-27s", "Item"),
                    String.format("%12s", "Price"));
        }
        input.pressEnter();
    }

    /**
     * Demonstrates the Decorator pattern by allowing the user to
     * dynamically add extras (cheese, toppings, drinks) to a menu item.
     * Each selection wraps the MenuItem in another decorator layer.
     */
    private void customizeItemDemo() {
        printHeader("DECORATOR PATTERN");
        printSubheader("Item Customization \u2014 Dynamically add extras to any menu item");

        List<MenuItem> available = menuItemDAO.findAllAvailable();
        if (available.isEmpty()) {
            System.out.println("  No items available.");
            input.pressEnter();
            return;
        }

        System.out.println("  Select a base item:");
        printTableHeader(String.format("%-3s", "#"),
                String.format("%-27s", "Item"),
                String.format("%12s", "Price"));
        for (int i = 0; i < available.size(); i++) {
            printTableRow(
                    String.format("%-3d", i + 1),
                    String.format("%-27s", available.get(i).getDescription()),
                    String.format("%12s", fmt(available.get(i).getPrice())));
        }
        printTableFooter(String.format("%-3s", "#"),
                String.format("%-27s", "Item"),
                String.format("%12s", "Price"));
        int itemChoice = input.readInt("  Choice: ") - 1;
        if (itemChoice < 0 || itemChoice >= available.size()) {
            fail("  Invalid choice.");
            input.pressEnter();
            return;
        }

        MenuItem customized = available.get(itemChoice);
        info("\n  \u25B6 Starting with: " + customized.getDescription() +
                " \u2014 " + fmt(customized.getPrice()));

        while (true) {
            printSubheader("Add Extras [Decorator]");
            System.out.println("    1. Extra Cheese    (+NPR 50)");
            System.out.println("    2. Extra Toppings  (+NPR 80)");
            System.out.println("    3. Add Drink       (+NPR 100)");
            System.out.println("    4. Done");
            int choice = input.readInt("    Choice: ");

            switch (choice) {
                case 1 -> {
                    customized = new ExtraCheeseDecorator(customized);
                    ok("    \u2714 + Extra Cheese");
                }
                case 2 -> {
                    customized = new ExtraToppingDecorator(customized);
                    ok("    \u2714 + Extra Toppings");
                }
                case 3 -> {
                    String drink = input.readLine("    Drink name: ");
                    customized = new DrinkDecorator(customized, drink);
                    ok("    \u2714 + " + drink);
                }
                case 4 -> {
                    printSeparator();
                    System.out.println(ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_YELLOW,
                            "  \u2B50 FINAL CUSTOMIZED ITEM:")));
                    System.out.println("    " + customized.getDescription());
                    System.out.println("    Price: " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_WHITE, fmt(customized.getPrice())));

                    if (input.readYesNo("\n  Add this to your order?")) {
                        placeOrderWithItems(List.of(customized), List.of(1));
                    }
                    return;
                }
                default -> fail("    \u2717 Invalid.");
            }
            info("    \u2192 Current: " + customized.getDescription() +
                    " \u2014 " + fmt(customized.getPrice()));
        }
    }

    /**
     * Full order placement flow demonstrating Builder, Strategy, Adapter, and Facade patterns.
     * 1. User selects menu items (optionally decorated)
     * 2. User chooses a delivery strategy
     * 3. User selects a payment gateway
     * 4. OrderFacade orchestrates building, payment, and persistence
     */
    private void placeOrder() {
        if (!(currentUser instanceof Customer customer)) {
            System.out.println("  Only customers can place orders.");
            input.pressEnter();
            return;
        }

        printHeader("PLACE ORDER");
        printSubheader("Builder + Strategy + Adapter + Facade Patterns");

        List<MenuItem> selectedItems = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        List<MenuItem> menuItems = menuItemDAO.findAllAvailable();

        if (menuItems.isEmpty()) {
            System.out.println("  No menu items available.");
            input.pressEnter();
            return;
        }

        // Step 1: Select items
        info("\n  \u2460 Select items (0 to finish):");
        printTableHeader(String.format("%-3s", "#"),
                String.format("%-27s", "Item"),
                String.format("%12s", "Price"));
        for (int i = 0; i < menuItems.size(); i++) {
            printTableRow(
                    String.format("%-3d", i + 1),
                    String.format("%-27s", menuItems.get(i).getDescription()),
                    String.format("%12s", fmt(menuItems.get(i).getPrice())));
        }
        printTableFooter(String.format("%-3s", "#"),
                String.format("%-27s", "Item"),
                String.format("%12s", "Price"));

        while (true) {
            int sel = input.readInt("  Item #: ");
            if (sel == 0) break;
            if (sel < 1 || sel > menuItems.size()) {
                fail("  \u2717 Invalid.");
                continue;
            }

            MenuItem item = menuItems.get(sel - 1);
            if (input.readYesNo("  Customize with [Decorator]")) {
                item = customizeItemInline(item);
            }
            int qty;
            do {
                qty = input.readInt("  Quantity: ");
                if (qty < 1) fail("  \u2717 Quantity must be at least 1.");
            } while (qty < 1);
            selectedItems.add(item);
            quantities.add(qty);
            ok("  \u2714 Added: " + item.getDescription() + " x " + qty);
        }

        if (selectedItems.isEmpty()) {
            fail("  \u2717 No items selected.");
            input.pressEnter();
            return;
        }

        // Step 2: Delivery strategy
        printSeparator();
        info("  \u2461 Delivery Options [Strategy Pattern]:");
        System.out.println("    1. Standard  \u2014 NPR 20/km (30\u201345 min)");
        System.out.println("    2. Express   \u2014 NPR 20/km + 100 (15\u201320 min)");
        System.out.println("    3. Scheduled \u2014 Free");
        int stratChoice = input.readInt("    Choice: ");
        double distance;
        do {
            distance = input.readDouble("    Distance (km): ");
            if (distance < 0) fail("    \u2717 Distance cannot be negative.");
        } while (distance < 0);

        DeliveryStrategy strategy = switch (stratChoice) {
            case 2 -> new ExpressDeliveryStrategy();
            case 3 -> new ScheduledDeliveryStrategy();
            default -> new StandardDeliveryStrategy();
        };
        info("    \u2192 " + strategy.getStrategyName() +
                " | Charge: " + fmt(strategy.calculateCharge(distance)));

        // Step 3: Payment
        printSeparator();
        info("  \u2462 Payment Gateway [Adapter Pattern]:");
        System.out.println("    1. Khalti    2. eSewa    3. PayPal");
        int payChoice = input.readInt("    Choice: ");
        String paymentMethod = switch (payChoice) {
            case 2 -> "ESEWA";
            case 3 -> "PAYPAL";
            default -> "KHALTI";
        };

        placeOrderWithItems(selectedItems, quantities, strategy, distance, paymentMethod);
    }

    /** Convenience overload that defaults to Standard delivery and Khalti payment. */
    private void placeOrderWithItems(List<MenuItem> items, List<Integer> quantities) {
        placeOrderWithItems(items, quantities, new StandardDeliveryStrategy(), 3.0, "KHALTI");
    }

    /**
     * Core order placement logic. Uses the Facade pattern (OrderFacade) to
     * orchestrate Builder construction, Adapter payment processing, and
     * Proxy-based persistence. The order is then saved to the database.
     */
    private void placeOrderWithItems(List<MenuItem> items, List<Integer> quantities,
                                      DeliveryStrategy strategy, double distance,
                                      String paymentMethod) {
        if (!(currentUser instanceof Customer customer)) return;

        printSeparator();
        info("  \u2463 Processing via Facade [Facade Pattern] ...");
        OrderFacade facade = new OrderFacade(currentUser);

        List<OrderObserver> observers = List.of(
                new CustomerNotifier(), new RestaurantNotifier(), new DeliveryNotifier());

        String orderId = facade.placeOrder(customer, items, quantities,
                strategy, distance, paymentMethod, observers);

        if (orderId != null) {
            Order order = facade.getOrder(orderId);
            if (order != null) {
                orderDAO.saveOrder(order);
                notificationDAO.saveNotification(orderId, customer.getName(),
                        "Order " + orderId + " placed successfully.");
                printSeparator();
                System.out.println(ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_YELLOW,
                        "  \u2B50 ORDER CONFIRMED \u2B50")));
                System.out.println("    Order ID : " + orderId);
                System.out.println("    Customer : " + customer.getName());
                System.out.println("    Items    : " + items.size() + " item(s)");
                System.out.println("    Delivery : " + strategy.getStrategyName());
                System.out.println("    Payment  : " + paymentMethod);
                System.out.println("    Total    : " + ConsoleStyle.paint(ConsoleStyle.BRIGHT_YELLOW, fmt(order.getTotalAmount())));
                printSeparator();
            }
        } else {
            fail("  \u2717 Order placement failed.");
        }
        input.pressEnter();
    }

    /** Interactive inline item customization using the Decorator pattern. */
    private MenuItem customizeItemInline(MenuItem item) {
        printSubheader("Customize Item [Decorator Pattern]");
        while (true) {
            System.out.println("    1. Extra Cheese (+50)  2. Extra Toppings (+80)");
            System.out.println("    3. Add Drink (+100)    4. Done");
            int c = input.readInt("    Choice: ");
            switch (c) {
                case 1 -> { item = new ExtraCheeseDecorator(item); ok("    \u2714 + Cheese"); }
                case 2 -> { item = new ExtraToppingDecorator(item); ok("    \u2714 + Toppings"); }
                case 3 -> { item = new DrinkDecorator(item, input.readLine("    Drink: ")); ok("    \u2714 + Drink"); }
                case 4 -> { return item; }
                default -> fail("    \u2717 Invalid.");
            }
            info("    \u2192 " + item.getDescription() + " \u2014 " + fmt(item.getPrice()));
        }
    }

    /**
     * Displays the user's orders with their current lifecycle state.
     * Demonstrates the State pattern by showing the order status
     * and explaining the valid transition paths.
     */
    private void trackOrders() {
        printHeader("TRACK ORDERS");
        printSubheader("State Pattern \u2014 Order Lifecycle Management");

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getId());
        if (orders.isEmpty()) {
            System.out.println("  No orders found.");
            input.pressEnter();
            return;
        }

        printTableHeader(String.format("%-15s", "Order ID"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));
        for (Order o : orders) {
            printTableRow(
                    String.format("%-15s", o.getOrderId()),
                    badgeCell(o.getStatus()),
                    String.format("%12s", fmt(o.getTotalAmount())));
        }
        printTableFooter(String.format("%-15s", "Order ID"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));

        info("\n  \u25B6 Order Lifecycle: PENDING \u2192 CONFIRMED \u2192 PREPARING \u2192 OUT FOR DELIVERY \u2192 DELIVERED");
        warn("  \u2716 Cancel allowed from: PENDING, CONFIRMED, PREPARING");

        if (input.readYesNo("  View details for an order")) {
            String oid = input.readLine("  Order ID: ");
            orders.stream()
                    .filter(o -> o.getOrderId().equalsIgnoreCase(oid))
                    .findFirst().ifPresentOrElse(
                            o -> System.out.println("  Current state: " + ConsoleStyle.statusBadge(o.getStatus())),
                            () -> fail("  \u2717 Not found."));
        }
        input.pressEnter();
    }

    private void cancelOrder() {
        printHeader("CANCEL ORDER");
        printSubheader("Command Pattern \u2014 Cancel with undo support");

        List<Order> orders = orderDAO.findByCustomerId(currentUser.getId());
        List<Order> cancellable = orders.stream()
                .filter(o -> !o.getStatus().equals("DELIVERED") && !o.getStatus().equals("CANCELLED"))
                .toList();

        if (cancellable.isEmpty()) {
            System.out.println("  No cancellable orders.");
            input.pressEnter();
            return;
        }

        printTableHeader(String.format("%-3s", "#"),
                String.format("%-15s", "Order ID"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));
        for (int i = 0; i < cancellable.size(); i++) {
            Order o = cancellable.get(i);
            printTableRow(
                    String.format("%-3d", i + 1),
                    String.format("%-15s", o.getOrderId()),
                    badgeCell(o.getStatus()),
                    String.format("%12s", fmt(o.getTotalAmount())));
        }
        printTableFooter(String.format("%-3s", "#"),
                String.format("%-15s", "Order ID"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));
        int choice = input.readInt("  Select: ") - 1;
        if (choice < 0 || choice >= cancellable.size()) return;

        Order toCancel = cancellable.get(choice);
        String previousStatus = toCancel.getStatus();
        CancelOrderCommand cmd = new CancelOrderCommand(
                new OrderFacade(currentUser), toCancel.getOrderId(), currentUser, previousStatus);
        commandInvoker.executeCommand(cmd);

        toCancel.cancel();
        orderDAO.updateStatus(toCancel.getOrderId(), "CANCELLED");
        notificationDAO.saveNotification(toCancel.getOrderId(), currentUser.getName(),
                "Order " + toCancel.getOrderId() + " cancelled.");

        info("  \u25B6 [Command] executed. History size: " + commandInvoker.getHistorySize());
        if (input.readYesNo("  Undo? [Command Undo]")) {
            commandInvoker.undoLastCommand();
            toCancel.setState(OrderDAO.fromStatus(previousStatus));
            orderDAO.updateStatus(toCancel.getOrderId(), previousStatus);
            notificationDAO.saveNotification(toCancel.getOrderId(), currentUser.getName(),
                    "Order " + toCancel.getOrderId() + " restored to " + previousStatus + ".");
        }
        input.pressEnter();
    }

    private void viewNotifications() {
        printHeader("NOTIFICATIONS");
        printSubheader("Observer Pattern \u2014 Event-driven notifications");
        notificationDAO.printNotificationsForUser(currentUser.getName());
        input.pressEnter();
    }

    // ==================== ADMIN FEATURES ====================

    private void viewAllOrders() {
        printHeader("ALL ORDERS");
        List<Order> orders = orderDAO.findAll();
        if (orders.isEmpty()) {
            System.out.println("  No orders.");
            input.pressEnter();
            return;
        }
        printTableHeader(String.format("%-15s", "Order ID"),
                String.format("%-20s", "Customer"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));
        for (Order o : orders) {
            printTableRow(
                    String.format("%-15s", o.getOrderId()),
                    String.format("%-20s", o.getCustomer().getName()),
                    badgeCell(o.getStatus()),
                    String.format("%12s", fmt(o.getTotalAmount())));
        }
        printTableFooter(String.format("%-15s", "Order ID"),
                String.format("%-20s", "Customer"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));
        input.pressEnter();
    }

    private void processOrder() {
        printHeader("PROCESS ORDER");
        printSubheader("State Pattern \u2014 Advance order through lifecycle");

        List<Order> orders = orderDAO.findAll().stream()
                .filter(o -> !o.getStatus().equals("DELIVERED") && !o.getStatus().equals("CANCELLED"))
                .toList();

        if (orders.isEmpty()) {
            System.out.println("  No orders to process.");
            input.pressEnter();
            return;
        }

        printTableHeader(String.format("%-3s", "#"),
                String.format("%-15s", "Order ID"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            printTableRow(
                    String.format("%-3d", i + 1),
                    String.format("%-15s", o.getOrderId()),
                    badgeCell(o.getStatus()),
                    String.format("%12s", fmt(o.getTotalAmount())));
        }
        printTableFooter(String.format("%-3s", "#"),
                String.format("%-15s", "Order ID"),
                String.format("%-22s", "Status"),
                String.format("%12s", "Amount"));

        int choice = input.readInt("  Select order: ") - 1;
        if (choice < 0 || choice >= orders.size()) return;

        Order order = orders.get(choice);
        info("\n  \u25B6 Current: " + ConsoleStyle.statusBadge(order.getStatus()));
        System.out.println("    1. Confirm  2. Prepare  3. Deliver  4. Complete  5. Cancel");
        int action = input.readInt("    Action: ");

        String target = switch (action) {
            case 1 -> { order.confirm(); yield "CONFIRMED"; }
            case 2 -> { order.prepare(); yield "PREPARING"; }
            case 3 -> { order.deliver(); yield "OUT_FOR_DELIVERY"; }
            case 4 -> { order.complete(); yield "DELIVERED"; }
            case 5 -> { order.cancel(); yield "CANCELLED"; }
            default -> null;
        };

        if (target == null) {
            fail("    \u2717 Invalid action.");
        } else if (target.equals(order.getStatus())) {
            // Transition accepted by the state machine — persist it
            orderDAO.updateStatus(order.getOrderId(), target);
            if (target.equals("DELIVERED")) {
                notificationDAO.saveNotification(order.getOrderId(), order.getCustomer().getName(),
                        "Your order " + order.getOrderId() + " has been delivered!");
            }
            info("    \u2192 " + ConsoleStyle.statusBadge(order.getStatus()) + " (saved to database)");
        } else {
            fail("    \u2717 Transition to " + target + " rejected by the state machine \u2014 database unchanged.");
        }
        input.pressEnter();
    }

    private void generateReport() {
        printHeader("GENERATE REPORT");
        printSubheader("Proxy Pattern \u2014 Access control via AuthProxy");

        IOrderService proxy = new AuthProxy(currentUser, new OrderService());
        info("  \u25B6 " + currentUser.getName() +
                " (" + currentUser.getRole() + ") requesting report...");

        String report = proxy.generateReport("SUMMARY");
        if (report.equals("Access Denied")) {
            fail("  \u2717 [Proxy] ACCESS DENIED: Only ADMIN can generate reports.");
        } else {
            ok("  \u2714 [Proxy] Access granted.");
            printSeparator();
            System.out.println(report);
            printSeparator();

            List<Order> allOrders = orderDAO.findAll();
            if (!allOrders.isEmpty()) {
                new ReportGenerator().generateOrderReport(allOrders);
            }
        }
        input.pressEnter();
    }

    private void manageMenu() {
        printHeader("MANAGE MENU");
        System.out.println("    1. View All Items");
        System.out.println("    2. Add New Item");
        System.out.println("    3. Toggle Availability");
        int c = input.readInt("    Choice: ");
        switch (c) {
            case 1 -> {
                List<MenuItem> all = menuItemDAO.findAll();
                if (all.isEmpty()) {
                    System.out.println("    No items.");
                } else {
                    printTableHeader(String.format("%-3s", "#"),
                            String.format("%-27s", "Item"),
                            String.format("%12s", "Price"));
                    for (int i = 0; i < all.size(); i++) {
                        MenuItem mi = all.get(i);
                        printTableRow(
                                String.format("%-3d", i + 1),
                                String.format("%-27s", mi.getDescription()),
                                String.format("%12s", fmt(mi.getPrice())));
                    }
                    printTableFooter(String.format("%-3s", "#"),
                            String.format("%-27s", "Item"),
                            String.format("%12s", "Price"));
                }
            }
            case 2 -> {
                String name = input.readLine("    Name: ");
                double price = input.readDouble("    Price: ");
                if (menuItemDAO.addItem(name, price)) {
                    ok("    \u2714 Added.");
                } else {
                    fail("    \u2717 Failed.");
                }
            }
            case 3 -> {
                String name = input.readLine("    Item name: ");
                boolean avail = input.readYesNo("    Available?");
                menuItemDAO.toggleAvailability(name, avail);
                ok("    \u2714 Updated.");
            }
        }
        input.pressEnter();
    }

    private void viewAllNotifications() {
        printHeader("ALL NOTIFICATIONS");
        printSubheader("Observer Pattern \u2014 System-wide notifications");
        notificationDAO.printNotificationsForUser("%");
        input.pressEnter();
    }

    // ==================== DELIVERY FEATURES ====================

    private void viewOutForDelivery() {
        printHeader("OUT FOR DELIVERY");
        List<Order> deliveries = orderDAO.findAll().stream()
                .filter(o -> o.getStatus().equals("OUT_FOR_DELIVERY"))
                .toList();
        if (deliveries.isEmpty()) {
            System.out.println("  No orders out for delivery.");
        } else {
            printTableHeader(String.format("%-15s", "Order ID"),
                    String.format("%-20s", "Customer"),
                    String.format("%12s", "Amount"));
            for (Order o : deliveries) {
                printTableRow(
                        String.format("%-15s", o.getOrderId()),
                        String.format("%-20s", o.getCustomer().getName()),
                        String.format("%12s", fmt(o.getTotalAmount())));
            }
            printTableFooter(String.format("%-15s", "Order ID"),
                    String.format("%-20s", "Customer"),
                    String.format("%12s", "Amount"));
        }
        input.pressEnter();
    }

    private void markDelivered() {
        printHeader("MARK DELIVERED");
        List<Order> deliveries = orderDAO.findAll().stream()
                .filter(o -> o.getStatus().equals("OUT_FOR_DELIVERY"))
                .toList();
        if (deliveries.isEmpty()) {
            System.out.println("  No orders to deliver.");
            input.pressEnter();
            return;
        }
        printTableHeader(String.format("%-3s", "#"),
                String.format("%-15s", "Order ID"),
                String.format("%-20s", "Customer"));
        for (int i = 0; i < deliveries.size(); i++) {
            Order o = deliveries.get(i);
            printTableRow(
                    String.format("%-3d", i + 1),
                    String.format("%-15s", o.getOrderId()),
                    String.format("%-20s", o.getCustomer().getName()));
        }
        printTableFooter(String.format("%-3s", "#"),
                String.format("%-15s", "Order ID"),
                String.format("%-20s", "Customer"));
        int choice = input.readInt("  Select: ") - 1;
        if (choice >= 0 && choice < deliveries.size()) {
            Order o = deliveries.get(choice);
            o.complete();
            if ("DELIVERED".equals(o.getStatus())) {
                orderDAO.updateStatus(o.getOrderId(), "DELIVERED");
                notificationDAO.saveNotification(o.getOrderId(), o.getCustomer().getName(),
                        "Your order " + o.getOrderId() + " has been delivered!");
                ok("  \u2714 Delivered!");
            } else {
                fail("  \u2717 Could not deliver \u2014 order is not out for delivery.");
            }
        }
        input.pressEnter();
    }

    // ==================== UI FORMATTING HELPERS ====================

    private void printBanner() {
        String title = "FOODIEEXPRESS - ONLINE FOOD ORDERING";
        String sub = "11 GoF Design Patterns | PostgreSQL";
        int inner = Math.max(title.length(), sub.length()) + 4;
        String bar = "\u2550".repeat(inner);
        System.out.println();
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2554" + bar + "\u2557"));
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2551")
                + ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN,
                        String.format("%-" + inner + "s", "  " + title)))
                + ConsoleStyle.paint(ConsoleStyle.CYAN, "\u2551"));
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2551")
                + ConsoleStyle.paint(ConsoleStyle.DIM,
                        String.format("%-" + inner + "s", "      " + sub))
                + ConsoleStyle.paint(ConsoleStyle.CYAN, "\u2551"));
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u255A" + bar + "\u255D"));
    }

    private void printHeader(String title) {
        String line = "\u2500".repeat(title.length() + 6);
        String border = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u250C" + line + "\u2510");
        String bottom = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2514" + line + "\u2518");
        System.out.println(border);
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2502   ")
                + ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN, title))
                + ConsoleStyle.paint(ConsoleStyle.CYAN, "   \u2502"));
        System.out.println(bottom);
    }

    private void printSubheader(String title) {
        System.out.println(ConsoleStyle.paint(ConsoleStyle.MAGENTA, "  \u2500\u2500 " + title + " \u2500\u2500"));
    }

    private void printSeparator() {
        System.out.println(ConsoleStyle.paint(ConsoleStyle.DIM, "  " + "\u2500".repeat(55)));
    }

    /**
     * Draws a themed menu box with a colored border and a bold title.
     * The box width adapts to the longest option so all menus align.
     */
    private void printMenuBox(String icon, String title, List<String> options) {
        int inner = Math.max(title.length() + 2,
                options.stream().mapToInt(String::length).max().orElse(0));
        String bar = "\u2500".repeat(inner + 2);
        String border = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u250C" + bar + "\u2510");
        String mid = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u251C" + bar + "\u2524");
        String bottom = ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2514" + bar + "\u2518");

        System.out.println(border);
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2502 ")
                + ConsoleStyle.bold(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN,
                        String.format("%-" + inner + "s", icon + " " + title)))
                + ConsoleStyle.paint(ConsoleStyle.CYAN, " \u2502"));
        System.out.println(mid);
        for (String opt : options) {
            System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, "  \u2502 ")
                    + ConsoleStyle.paint(ConsoleStyle.BRIGHT_WHITE,
                            String.format("%-" + inner + "s", opt))
                    + ConsoleStyle.paint(ConsoleStyle.CYAN, " \u2502"));
        }
        System.out.println(bottom);
    }

    /** Painted status badge, right-padded to the fixed table column width. */
    private String badgeCell(String status) {
        return ConsoleStyle.paint(ConsoleStyle.statusColor(status),
                String.format("%-22s", ConsoleStyle.statusSymbol(status)));
    }

    private void ok(String msg) {
        System.out.println(ConsoleStyle.paint(ConsoleStyle.GREEN, msg));
    }

    private void fail(String msg) {
        System.out.println(ConsoleStyle.paint(ConsoleStyle.RED, msg));
    }

    private void info(String msg) {
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, msg));
    }

    private void warn(String msg) {
        System.out.println(ConsoleStyle.paint(ConsoleStyle.YELLOW, msg));
    }

    private String fmt(double amount) {
        return "NPR " + String.format("%,.2f", amount);
    }

    private void printTableHeader(String... columns) {
        StringBuilder sb = new StringBuilder("  \u250C");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) sb.append("\u252C");
            sb.append("\u2500".repeat(columns[i].length() + 2));
        }
        sb.append("\u2510");
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, sb.toString()));

        sb = new StringBuilder("  \u2502");
        for (String col : columns) {
            sb.append(" ").append(col).append(" \u2502");
        }
        System.out.println(ConsoleStyle.paint(ConsoleStyle.BRIGHT_CYAN, sb.toString()));

        sb = new StringBuilder("  \u251C");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) sb.append("\u253C");
            sb.append("\u2500".repeat(columns[i].length() + 2));
        }
        sb.append("\u2524");
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, sb.toString()));
    }

    private void printTableRow(String... cells) {
        StringBuilder sb = new StringBuilder("  \u2502");
        for (String cell : cells) {
            sb.append(" ").append(cell).append(" \u2502");
        }
        System.out.println(sb);
    }

    private void printTableFooter(String... columns) {
        StringBuilder sb = new StringBuilder("  \u2514");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) sb.append("\u2534");
            sb.append("\u2500".repeat(columns[i].length() + 2));
        }
        sb.append("\u2518");
        System.out.println(ConsoleStyle.paint(ConsoleStyle.CYAN, sb.toString()));
    }
}
