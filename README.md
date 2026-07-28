# Online Food Ordering System

A comprehensive Java application demonstrating the integration of **11 Design Patterns** (Creational, Structural, and Behavioral) within a single real-world domain.

---

## Domain: Online Food Ordering System

Customers browse menu items, customize them with extras, choose delivery options, place orders via various payment gateways, and receive real-time status notifications. Administrators manage orders and generate reports.

---

## Design Pattern Mapping

| Category     | Pattern         | Implementation                                                                 |
|--------------|-----------------|-------------------------------------------------------------------------------|
| **Creational** | **Singleton**     | `RestaurantConfig` — single source of restaurant-wide settings                |
|              | **Factory Method**| `UserFactory` hierarchy — creates `Customer`, `Admin`, `DeliveryPartner`      |
|              | **Builder**       | `OrderBuilder` — step-by-step construction of complex `Order` objects         |
| **Structural** | **Adapter**       | `PaymentAdapter` — unifies Khalti, eSewa, and PayPal behind one interface     |
|              | **Facade**        | `OrderFacade` — simplified interface to the ordering subsystem                |
|              | **Proxy**         | `AuthProxy` — controls access to sensitive operations based on user role      |
|              | **Decorator**     | `ItemDecorator` hierarchy — dynamically adds extras (cheese, toppings, drinks) |
| **Behavioral** | **Strategy**      | `DeliveryStrategy` — interchangeable delivery algorithms (Standard/Express/Scheduled) |
|              | **Observer**      | `OrderObserver` — notifies Customer, Kitchen, and Delivery Partner on changes  |
|              | **Command**       | `OrderCommand` — encapsulates Place/Cancel operations with undo support        |
|              | **State**         | `OrderState` — manages PENDING → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED lifecycle |

---

## Functional Requirements Covered

| Requirement       | Implementation                            |
|-------------------|-------------------------------------------|
| User Management   | Factory Method creates Customers, Admins, Delivery Partners |
| Core Business     | Order placement, customization, payment, delivery |
| Notifications     | Observer pattern notifies all parties on status changes |
| Reports           | ReportGenerator + Proxy-restricted admin reports |
| Status Tracking   | State pattern manages full order lifecycle |
| Security          | Proxy pattern restricts sensitive operations to authorized roles |

---

## Project Structure

```
src/main/java/com/foodordering/
├── Main.java                         # Demo entry point
├── config/RestaurantConfig.java      # Singleton
├── model/                            # Domain models
│   ├── User.java, Customer.java, Admin.java, DeliveryPartner.java
│   ├── MenuItem.java, BaseMenuItem.java
│   ├── OrderItem.java, Order.java
├── factory/                          # Factory Method
│   ├── UserFactory.java, CustomerFactory.java
│   ├── AdminFactory.java, DeliveryPartnerFactory.java
├── builder/OrderBuilder.java         # Builder
├── adapter/                          # Adapter
│   ├── PaymentGateway.java, KhaltiGateway.java
│   ├── ESewaGateway.java, PayPalGateway.java, PaymentAdapter.java
├── facade/OrderFacade.java           # Facade
├── proxy/                            # Proxy
│   ├── IOrderService.java, OrderService.java, AuthProxy.java
├── decorator/                        # Decorator
│   ├── ItemDecorator.java, ExtraCheeseDecorator.java
│   ├── ExtraToppingDecorator.java, DrinkDecorator.java
├── strategy/                         # Strategy
│   ├── DeliveryStrategy.java, StandardDeliveryStrategy.java
│   ├── ExpressDeliveryStrategy.java, ScheduledDeliveryStrategy.java
├── observer/                         # Observer
│   ├── OrderObserver.java, CustomerNotifier.java
│   ├── RestaurantNotifier.java, DeliveryNotifier.java
├── command/                          # Command
│   ├── OrderCommand.java, PlaceOrderCommand.java
│   ├── CancelOrderCommand.java, CommandInvoker.java
├── state/                            # State
│   ├── OrderState.java, PendingState.java, ConfirmedState.java
│   ├── PreparingState.java, OutForDeliveryState.java
│   ├── DeliveredState.java, CancelledState.java
└── report/ReportGenerator.java       # Report generation

src/test/java/com/foodordering/
└── FoodOrderingSystemTest.java       # 15 JUnit tests
```

---

## Build & Run

### Prerequisites
- Java 21+
- Maven 3.8+

### Compile
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```
All **15 tests** pass, covering every pattern plus a full integration workflow.

### Run Demo
```bash
mvn exec:java -Dexec.mainClass="com.foodordering.Main"
```
Or after compilation:
```bash
java -cp target/classes com.foodordering.Main
```

---

## JUnit Test Summary

| Test # | Pattern         | Test Name                                       | Status |
|--------|-----------------|-------------------------------------------------|--------|
| 1      | Singleton       | RestaurantConfig returns same instance          | ✅     |
| 2      | Factory Method  | Create different user types                     | ✅     |
| 3      | Builder         | Build order with items, strategy, payment       | ✅     |
| 4      | Decorator       | Add extras to menu items                        | ✅     |
| 5      | Strategy        | Different delivery strategies calculate correctly| ✅    |
| 6      | Adapter         | Payment gateways process through unified interface| ✅    |
| 7      | Observer        | Notify all registered observers                 | ✅     |
| 8      | Command         | Execute and undo order commands                 | ✅     |
| 9      | State           | Order lifecycle state transitions               | ✅     |
| 10     | State           | Cancel from PENDING state                       | ✅     |
| 11     | State           | Cannot transition from DELIVERED                | ✅     |
| 12     | Facade          | Simplified order placement flow                 | ✅     |
| 13     | Proxy           | Access control for sensitive operations         | ✅     |
| 14     | Report          | Generate order summary report                   | ✅     |
| 15     | Integration     | Full system workflow                            | ✅     |

---

## SOLID Principles Applied

- **S** — Each class has a single responsibility (e.g., `PaymentAdapter` adapts, `AuthProxy` controls access)
- **O** — Open for extension via Strategy, Decorator, Factory Method hierarchies
- **L** — Subtypes are substitutable (any `DeliveryStrategy` works with `OrderBuilder`)
- **I** — Small, focused interfaces (`MenuItem`, `OrderCommand`, `OrderState`, `OrderObserver`)
- **D** — High-level modules (`OrderFacade`) depend on abstractions (`IOrderService`, `PaymentGateway`)
