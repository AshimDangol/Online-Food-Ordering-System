package com.foodordering;

import com.foodordering.builder.OrderBuilder;
import com.foodordering.command.*;
import com.foodordering.config.RestaurantConfig;
import com.foodordering.decorator.*;
import com.foodordering.facade.OrderFacade;
import com.foodordering.factory.*;
import com.foodordering.model.*;
import com.foodordering.observer.*;
import com.foodordering.report.ReportGenerator;
import com.foodordering.strategy.*;

import java.util.*;

/**
 * Main demonstration class for the Online Food Ordering System.
 * Runs sequential demonstrations of all 11 design patterns with
 * formatted console output. Each method focuses on one pattern
 * while showing how patterns collaborate in a real system.
 */
public class Main {

    private static List<Order> orderHistory = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       FOODIEEXPRESS - ONLINE FOOD ORDERING SYSTEM       ║");
        System.out.println("║    Design Patterns Integration Project (Assignment 2)   ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        demonstrateSingleton();
        demonstrateFactoryMethod();
        demonstrateBuilder();
        demonstrateDecorator();
        demonstrateStrategy();
        demonstrateAdapter();
        demonstrateObserver();
        demonstrateCommand();
        demonstrateState();
        demonstrateFacade();
        demonstrateProxy();

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                   END OF DEMONSTRATION                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    /** Demonstrates Singleton — RestaurantConfig ensures a single configuration source. */
    static void demonstrateSingleton() {
        RestaurantConfig config1 = RestaurantConfig.getInstance();
        RestaurantConfig config2 = RestaurantConfig.getInstance();
        config1.display();
        System.out.println("  (Same instance? " + (config1 == config2) + ")\n");
    }

    /** Demonstrates Factory Method — User creation via concrete factories. */
    static void demonstrateFactoryMethod() {
        System.out.println("=========================================");
        System.out.println("  FACTORY METHOD PATTERN - USER CREATION");
        System.out.println("=========================================");

        UserFactory customerFactory = new CustomerFactory();
        UserFactory adminFactory = new AdminFactory();
        UserFactory deliveryFactory = new DeliveryPartnerFactory();

        Customer alice = (Customer) customerFactory.createAndRegister(
                "U001", "Alice Sharma", "alice@email.com", "9801234567|Lalitpur");
        Admin admin = (Admin) adminFactory.createAndRegister(
                "A001", "Bob Thapa", "bob@foodieexpress.com", "Operations");
        DeliveryPartner dev = (DeliveryPartner) deliveryFactory.createAndRegister(
                "D001", "Dev Rai", "dev@email.com", "BA 1 PA 1234");

        System.out.println();
    }

    /** Demonstrates Builder — Step-by-step order construction with chainable methods. */
    static void demonstrateBuilder() {
        System.out.println("=========================================");
        System.out.println("  BUILDER PATTERN - ORDER CONSTRUCTION");
        System.out.println("=========================================");

        Customer c = new Customer("U002", "Priya Karki", "priya@email.com", "9841112233", "Patan");

        MenuItem pizza = new BaseMenuItem("Margherita Pizza", 450.0);
        MenuItem pasta = new BaseMenuItem("White Sauce Pasta", 350.0);

        OrderBuilder builder = new OrderBuilder(c)
                .addItem(pizza, 1)
                .addItem(pasta, 2)
                .setDeliveryStrategy(new StandardDeliveryStrategy(), 3.5)
                .setPaymentMethod("KHALTI");

        Order order = builder.build();
        orderHistory.add(order);

        System.out.println("  Customer     : " + c.getName());
        System.out.println("  Items:");

        double subtotal = 0;
        for (OrderItem oi : order.getItems()) {
            System.out.println("    - " + oi);
            subtotal += oi.getTotalPrice();
        }
        System.out.println("  Subtotal     : NPR " + String.format("%,.2f", subtotal));
        System.out.println("  Tax (13%)    : NPR " + String.format("%,.2f", builder.getTaxAmount()));
        System.out.println("  Delivery     : NPR " + String.format("%,.2f", builder.getDeliveryCharge()));
        System.out.println("  Total        : NPR " + String.format("%,.2f", order.getTotalAmount()));
        System.out.println("  Payment      : " + order.getPaymentMethod());
        System.out.println();
    }

    /** Demonstrates Decorator — Dynamically adds extras to menu items. */
    static void demonstrateDecorator() {
        System.out.println("=========================================");
        System.out.println("  DECORATOR PATTERN - ITEM CUSTOMIZATION");
        System.out.println("=========================================");

        MenuItem basePizza = new BaseMenuItem("Pepperoni Pizza", 500.0);
        System.out.println("  Base item     : " + basePizza.getDescription()
                + " - NPR " + String.format("%,.2f", basePizza.getPrice()));

        MenuItem withCheese = new ExtraCheeseDecorator(basePizza);
        System.out.println("  + Extra Cheese: " + withCheese.getDescription()
                + " - NPR " + String.format("%,.2f", withCheese.getPrice()));

        MenuItem withToppings = new ExtraToppingDecorator(withCheese);
        System.out.println("  + Toppings    : " + withToppings.getDescription()
                + " - NPR " + String.format("%,.2f", withToppings.getPrice()));

        MenuItem fullyLoaded = new DrinkDecorator(withToppings, "Coke");
        System.out.println("  + Drink       : " + fullyLoaded.getDescription()
                + " - NPR " + String.format("%,.2f", fullyLoaded.getPrice()));
        System.out.println();
    }

    /** Demonstrates Strategy — Interchangeable delivery algorithms. */
    static void demonstrateStrategy() {
        System.out.println("=========================================");
        System.out.println("  STRATEGY PATTERN - DELIVERY OPTIONS");
        System.out.println("=========================================");

        DeliveryStrategy[] strategies = {
                new StandardDeliveryStrategy(),
                new ExpressDeliveryStrategy(),
                new ScheduledDeliveryStrategy()
        };

        double distance = 5.0;

        for (DeliveryStrategy s : strategies) {
            System.out.println("  " + s.getStrategyName());
            System.out.println("    Distance      : " + distance + " km");
            System.out.println("    Charge        : NPR "
                    + String.format("%,.2f", s.calculateCharge(distance)));
            System.out.println("    Estimated     : " + s.getEstimatedTime());
            System.out.println();
        }
    }

    /** Demonstrates Adapter — Unifies different payment gateway APIs. */
    static void demonstrateAdapter() {
        System.out.println("=========================================");
        System.out.println("  ADAPTER PATTERN - PAYMENT PROCESSING");
        System.out.println("=========================================");

        String[] gateways = {"KHALTI", "ESEWA", "PAYPAL"};
        double amount = 1500.0;

        for (String gw : gateways) {
            com.foodordering.adapter.PaymentAdapter adapter =
                    new com.foodordering.adapter.PaymentAdapter(gw);
            System.out.println("  Gateway    : " + adapter.getGatewayName());
            System.out.println("  Amount     : NPR " + String.format("%,.2f", amount));
            boolean result = adapter.processPayment(amount);
            System.out.println("  Status     : " + (result ? "SUCCESS" : "FAILED"));
            System.out.println();
        }
    }

    /** Demonstrates Observer — Notifies multiple parties on order status changes. */
    static void demonstrateObserver() {
        System.out.println("=========================================");
        System.out.println("  OBSERVER PATTERN - NOTIFICATIONS");
        System.out.println("=========================================");

        Customer customer = new Customer("U003", "Riya Gurung", "riya@email.com",
                "9855512345", "Bhaktapur");
        Order order = new Order("ORD-DEMO-OBS", customer);

        CustomerNotifier customerNotifier = new CustomerNotifier();
        RestaurantNotifier restaurantNotifier = new RestaurantNotifier();
        DeliveryNotifier deliveryNotifier = new DeliveryNotifier();

        order.attach(customerNotifier);
        order.attach(restaurantNotifier);
        order.attach(deliveryNotifier);

        order.notifyObservers("Your order has been received!");
        System.out.println("  -> All relevant parties notified.\n");
    }

    /** Demonstrates Command — Encapsulates operations with undo support. */
    static void demonstrateCommand() {
        System.out.println("=========================================");
        System.out.println("  COMMAND PATTERN - ORDER OPERATIONS");
        System.out.println("=========================================");

        Customer customer = new Customer("U004", "Sita Lamichhane", "sita@email.com",
                "9866611223", "Koteshwor");
        Admin admin = new Admin("A002", "Rajesh Hamal", "rajesh@foodieexpress.com", "Management");

        OrderFacade facade = new OrderFacade(admin);
        CommandInvoker invoker = new CommandInvoker();

        List<MenuItem> items = Arrays.asList(
                new BaseMenuItem("Chicken Burger", 350.0),
                new BaseMenuItem("Fries", 150.0)
        );
        List<Integer> qty = Arrays.asList(2, 1);

        ArrayList<OrderObserver> obs = new ArrayList<>();
        obs.add(new CustomerNotifier());
        obs.add(new RestaurantNotifier());

        OrderCommand placeCommand = new PlaceOrderCommand(
                facade, customer, items, qty,
                new ExpressDeliveryStrategy(), 2.0, "ESEWA", obs);

        invoker.executeCommand(placeCommand);
        System.out.println();
        System.out.println("  (Undoing last command...)");
        invoker.undoLastCommand();
        System.out.println();
    }

    /** Demonstrates State — Order lifecycle transitions between states. */
    static void demonstrateState() {
        System.out.println("=========================================");
        System.out.println("  STATE PATTERN - ORDER LIFECYCLE");
        System.out.println("=========================================");

        Customer customer = new Customer("U005", "Anish Basnet", "anish@email.com",
                "9840012345", "Baneshwor");
        Order order = new Order("ORD-DEMO-STATE", customer);

        System.out.println("  Initial: " + order.getStatus());
        order.confirm();
        System.out.println("  After confirm: " + order.getStatus());
        order.prepare();
        System.out.println("  After prepare: " + order.getStatus());
        order.deliver();
        System.out.println("  After deliver: " + order.getStatus());
        order.complete();
        System.out.println("  After complete: " + order.getStatus());
        System.out.println();
    }

    /** Demonstrates Facade — Simplified interface to the ordering subsystem. */
    static void demonstrateFacade() {
        System.out.println("=========================================");
        System.out.println("  FACADE PATTERN - SIMPLIFIED ORDERING");
        System.out.println("=========================================");

        Admin admin = new Admin("A003", "Admin User", "admin@foodieexpress.com", "IT");
        Customer cust = new Customer("U006", "Nabin Shrestha", "nabin@email.com",
                "9845566778", "Jawalakhel");

        OrderFacade facade = new OrderFacade(admin);

        List<MenuItem> items = Arrays.asList(
                new BaseMenuItem("Chowmein", 200.0),
                new BaseMenuItem("Momo (12 pcs)", 300.0)
        );
        List<Integer> qty = Arrays.asList(1, 1);

        ArrayList<OrderObserver> obs = new ArrayList<>();
        obs.add(new CustomerNotifier());

        String orderId = facade.placeOrder(
                cust, items, qty,
                new StandardDeliveryStrategy(), 4.0, "KHALTI", obs);

        if (orderId != null) {
            Order placed = facade.getOrder(orderId);
            if (placed != null) {
                placed.confirm();
                placed.prepare();
                placed.deliver();
                placed.complete();
            }
        }
        System.out.println();
    }

    /** Demonstrates Proxy — Access control for sensitive operations. */
    static void demonstrateProxy() {
        System.out.println("=========================================");
        System.out.println("  PROXY PATTERN - ACCESS CONTROL");
        System.out.println("=========================================");

        Customer regularUser = new Customer("U007", "Guest User", "guest@email.com",
                "9800000000", "N/A");
        Admin adminUser = new Admin("A004", "Admin Boss", "boss@foodieexpress.com", "Management");

        System.out.println("  --- Attempt 1: Regular customer tries to generate report ---");
        com.foodordering.proxy.AuthProxy proxyForCustomer =
                new com.foodordering.proxy.AuthProxy(regularUser);
        String report1 = proxyForCustomer.generateReport("SUMMARY");
        System.out.println("  Result: " + report1);
        System.out.println();

        System.out.println("  --- Attempt 2: Admin generates report ---");
        com.foodordering.proxy.AuthProxy proxyForAdmin =
                new com.foodordering.proxy.AuthProxy(adminUser);
        String report2 = proxyForAdmin.generateReport("SUMMARY");
        System.out.println("  " + report2.replace("\n", "\n  "));
        System.out.println();

        orderHistory.clear();
    }
}
