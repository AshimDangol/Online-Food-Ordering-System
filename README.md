# Online Food Ordering System

A comprehensive **interactive Java application** demonstrating the integration of **11 Design Patterns** (Creational, Structural, and Behavioral) with **PostgreSQL database persistence** within a single real-world domain.

---

## Domain: Online Food Ordering System

Customers browse menu items, customize them with extras, choose delivery options, place orders via various payment gateways, and receive real-time status notifications. Administrators manage orders and generate reports.

The application features an **interactive console menu** with login/register, role-based access, and database persistence via **PostgreSQL**.

---

## Design Pattern Mapping

| Category     | Pattern         | Implementation                                                                 |
|--------------|-----------------|-------------------------------------------------------------------------------|
| **Creational** | **Singleton**     | `RestaurantConfig` + `DatabaseManager` — single sources of config and DB      |
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
| User Management   | Factory Method + UserDAO (PostgreSQL persistence) |
| Core Business     | Interactive order placement, customization, payment, delivery |
| Notifications     | Observer pattern + NotificationDAO (PostgreSQL log) |
| Reports           | ReportGenerator + Proxy-restricted admin reports |
| Status Tracking   | State pattern + OrderDAO (PostgreSQL persistence) |
| Security          | Proxy pattern (login + role-based access) |
| Data Persistence  | PostgreSQL database (auto-created) |

---

## Project Structure

```
src/main/java/com/foodordering/
├── Main.java                         # Interactive entry point
├── config/RestaurantConfig.java      # Singleton
├── db/                               # Database Layer
│   ├── DatabaseManager.java          # PostgreSQL connection singleton
│   ├── UserDAO.java                  # User CRUD
│   ├── MenuItemDAO.java              # Menu CRUD
│   ├── OrderDAO.java                 # Order CRUD
│   └── NotificationDAO.java          # Notification logging
├── interactive/                      # Interactive Console
│   ├── InteractiveMenu.java          # Menu-driven interface
│   └── InputHelper.java             # Input validation
├── model/                            # Domain models
├── factory/                          # Factory Method
├── builder/OrderBuilder.java         # Builder
├── adapter/                          # Adapter
├── facade/OrderFacade.java           # Facade
├── proxy/                            # Proxy
├── decorator/                        # Decorator
├── strategy/                         # Strategy
├── observer/                         # Observer
├── command/                          # Command
├── state/                            # State
└── report/ReportGenerator.java       # Report generation

src/test/java/com/foodordering/
└── FoodOrderingSystemTest.java       # 17 JUnit tests

data/                                 # (reserved — not used with PostgreSQL)
docs/
├── uml-diagram.puml                  # PlantUML source
└── uml-diagram.png                   # Rendered UML class diagram
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
All **17 tests** pass, covering every pattern, database operations, plus a full integration workflow.

### Run Interactive App
```bash
mvn exec:java
```

The PostgreSQL database `foodordering` is auto-created on `localhost:5432` on first run (requires a running PostgreSQL 18+ server). User: `postgres`, Password: `1234`.

---

## JUnit Test Summary

| Test # | Pattern / Area  | Test Name                                       | Status |
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
| 16     | Database        | User registration and authentication via PostgreSQL | ✅ |
| 17     | Database        | Save and retrieve orders via PostgreSQL             | ✅ |

---

## SOLID Principles Applied

- **S** — Each class has a single responsibility (e.g., `PaymentAdapter` adapts, `AuthProxy` controls access)
- **O** — Open for extension via Strategy, Decorator, Factory Method hierarchies
- **L** — Subtypes are substitutable (any `DeliveryStrategy` works with `OrderBuilder`)
- **I** — Small, focused interfaces (`MenuItem`, `OrderCommand`, `OrderState`, `OrderObserver`)
- **D** — High-level modules (`OrderFacade`) depend on abstractions (`IOrderService`, `PaymentGateway`)
