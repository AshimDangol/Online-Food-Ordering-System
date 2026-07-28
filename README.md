# Library Management System

A Java console application that demonstrates the integration of **Creational** and **Behavioral** Design Patterns.

## Design Patterns Used

| Pattern | Category | Purpose |
|---|---|---|
| **Factory Method** | Creational | Creates books (Physical, E-Book, AudioBook) and members (Student, Teacher, Admin) |
| **Strategy** | Behavioral | Runtime-swappable fine calculation algorithms per member type |
| **Command** | Behavioral | Encapsulates borrow/return requests with undo support |
| **State** | Behavioral | Manages book lifecycle (AVAILABLE → BORROWED → RESERVED → UNDER_REPAIR → LOST) |

## Prerequisites

- Java 25+
- Maven

## Build & Run

```bash
mvn clean compile
mvn test
java -cp target/classes com.library.Main
```

## Project Structure

```
src/
├── main/java/com/library/
│   ├── Main.java                   # Entry point — demonstrates all 4 patterns
│   ├── model/                      # Domain models (Book, Member, Loan, Fine, Notification)
│   ├── factory/                    # Factory Method pattern
│   ├── strategy/                   # Strategy pattern (fine calculation)
│   ├── command/                    # Command pattern (borrow/return with undo)
│   └── state/                      # State pattern (book lifecycle)
└── test/java/com/library/
    └── LibrarySystemTest.java      # JUnit 5 tests (5 tests, all pass)
```

## Test Results

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Functional Features

- User Management (Student, Teacher, Admin)
- Book Catalog (Physical, E-Book, AudioBook)
- Borrowing & Returning with Undo
- Overdue Fine Calculation (per member type)
- Book Lifecycle State Tracking
