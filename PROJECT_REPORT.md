# Library Management System — Design Patterns Project Report

---

## 1. Project Title

**Library Management System**  
A console-based Java application integrating Creational and Behavioral Design Patterns.

---

## 2. Problem Statement

Traditional library management systems often suffer from tightly coupled code that is difficult to maintain, extend, or test. Common issues include:
- Hard-coded object creation scattered across the codebase
- Inflexible fine calculation algorithms
- Tight coupling between request processing and business logic
- Poor handling of book lifecycle state transitions

This project addresses these problems by applying four design patterns — **Factory Method**, **Strategy**, **Command**, and **State** — to produce a modular, testable, and extensible system.

---

## 3. Functional Requirements

| Requirement | Description |
|---|---|
| **User Management** | Create and manage library members (Student, Teacher, Admin) |
| **Catalog Management** | Add books of different types (Physical, E-Book, AudioBook) |
| **Borrowing Process** | Members can borrow and return books |
| **Fine Calculation** | Overdue fines calculated differently per member type |
| **Undo Support** | Borrow/return operations can be undone |
| **Status Tracking** | Book lifecycle managed through state transitions |

---

## 4. Architecture Design

The system follows a layered architecture:

```
┌─────────────────────────────────────────────────────┐
│                    Main / Client                      │
├─────────────────────────────────────────────────────┤
│    Factory        │   Command      │     State       │
│    (Creational)   │   (Behavioral) │  (Behavioral)   │
├─────────────────────────────────────────────────────┤
│   Strategy (Behavioral) — Fine Calculation           │
├─────────────────────────────────────────────────────┤
│              Model Layer (Book, Member, Loan, Fine)  │
└─────────────────────────────────────────────────────┘
```

- **Model Layer**: POJO classes representing domain entities
- **Pattern Layer**: Design pattern implementations that encapsulate behavior
- **Client Layer**: Main class that orchestrates the patterns

---

## 5. Design Pattern Mapping

| Pattern | Category | Role in System |
|---|---|---|
| **Factory Method** | Creational | Creates books (Physical, E-Book, AudioBook) and members (Student, Teacher, Admin) without coupling client code to concrete classes |
| **Strategy** | Behavioral | Encapsulates fine calculation algorithms that can be selected at runtime based on member type |
| **Command** | Behavioral | Encapsulates borrow/return requests as objects, enabling undo functionality via command history stack |
| **State** | Behavioral | Manages book lifecycle transitions (Available → Borrowed → Reserved → Under Repair → Lost) with state-specific behavior |

---

## 6. UML Class Diagram

```
┌────────────────────────────────────────────────────────────────────────────┐
│                          DESIGN PATTERNS — CLASS DIAGRAM                    │
└────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│              FACTORY METHOD PATTERN               │
├──────────────────────────────────────────────────┤
│                    ┌─────────────┐                │
│                    │ BookFactory │◄─── abstract    │
│                    │ (abstract)  │                │
│                    └──────┬──────┘                │
│           ┌───────────────┼───────────────┐       │
│     ┌─────┴──────┐ ┌─────┴──────┐ ┌──────┴─────┐ │
│     │PhysicalBook│ │EBookFactory│ │AudioBook   │ │
│     │Factory     │ │            │ │Factory     │ │
│     └────────────┘ └────────────┘ └────────────┘ │
│                                                    │
│   Each factory overrides createBook() to return    │
│   a Book with the appropriate type field set.      │
│                                                    │
│                    ┌─────────────┐                │
│                    │MemberFactory│◄─── abstract    │
│                    └──────┬──────┘                │
│           ┌───────────────┼───────────────┐       │
│     ┌─────┴──────┐ ┌─────┴──────┐ ┌──────┴─────┐ │
│     │Student     │ │Teacher     │ │AdminFactory │ │
│     │MemberFact. │ │MemberFact. │ │            │ │
│     └────────────┘ └────────────┘ └────────────┘ │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│               STRATEGY PATTERN                    │
├──────────────────────────────────────────────────┤
│   ┌───────────────┐    ┌─────────────────────┐   │
│   │FineCalculator │───>│FineCalculationStrat.│   │
│   │               │    │(interface)          │   │
│   │setStrategy()  │    │+ calculateFine()    │   │
│   │calculateFine()│    │+ getStrategyName()  │   │
│   └───────────────┘    └──────────┬──────────┘   │
│                         ┌─────────┼─────────┐    │
│                   ┌─────┴────┐ ┌──┴────┐ ┌──┴──┐│
│                   │Standard │ │Student│ │Teach││
│                   │FineStrat│ │Discnt │ │rNoFi││
│                   └─────────┘ └───────┘ └─────┘│
│                                                    │
│   Standard      : daysOverdue * $0.50              │
│   Student       : daysOverdue * $0.25 (50% off)    │
│   Teacher       : $0.00 (no fines)                 │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│               COMMAND PATTERN                     │
├──────────────────────────────────────────────────┤
│   ┌───────────────┐     ┌───────────────────┐    │
│   │CommandInvoker │────>│Command (interface) │    │
│   │               │     │                   │    │
│   │executeCommand│     │+ execute(): bool  │    │
│   │undoLastCommd │     │+ undo()           │    │
│   │[commandStack]│     │+ getDescription() │    │
│   └───────────────┘     └────────┬──────────┘    │
│                    ┌──────────────┼──────────────┐│
│              ┌─────┴──────┐┌─────┴──────┐┌──────┴┐
│              │BorrowBook  ││ReturnBook  ││PayFine│
│              │Command     ││Command     ││Command│
│              └────────────┘└────────────┘└───────┘
│                                                    │
│   CommandInvoker maintains a Stack<Command> for     │
│   undo support. Each command knows how to reverse   │
│   its own execution via undo().                     │
└──────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────┐
│                STATE PATTERN                      │
├──────────────────────────────────────────────────┤
│   ┌──────────────┐     ┌────────────────────┐    │
│   │ BookContext  │────>│  BookState         │    │
│   │              │     │  (interface)       │    │
│   │ borrow()     │     │                    │    │
│   │ returnBook() │     │ + borrow()         │    │
│   │ reserve()    │     │ + returnBook()     │    │
│   │ repair()     │     │ + reserve()        │    │
│   │ lose()       │     │ + repair()         │    │
│   └──────────────┘     │ + lose()           │    │
│                        │ + getStateName()   │    │
│                        └─────────┬──────────┘    │
│        ┌───────────────┬─────────┼────────┬──────┘
│   ┌────┴────┐   ┌──────┴───┐ ┌──┴───┐ ┌──┴───┐
│   │Available│   │Borrowed  │ │Resrvd│ │UndRpr│
│   │State    │   │State     │ │State │ │State │
│   └─────────┘   └──────────┘ └──────┘ └──────┘
│   ┌──────────┐
│   │LostState │
│   └──────────┘
│                                                    │
│   Transitions:                                      │
│   AVAILABLE ──borrow──> BORROWED                    │
│   BORROWED  ──return──> AVAILABLE                   │
│   BORROWED  ──reserve─> RESERVED                    │
│   RESERVED  ──borrow──> BORROWED                    │
│   RESERVED  ──repair──> UNDER_REPAIR               │
│   UNDER_REPAIR ─repair─> AVAILABLE                  │
│   ANY STATE  ──lose───> LOST                        │
│   LOST       ──return──> AVAILABLE                  │
└──────────────────────────────────────────────────┘
```

---

## 7. Screenshots

### Factory Method Pattern — Book & Member Creation

```
=========================================
FACTORY METHOD PATTERN - BOOK & MEMBER CREATION
=========================================
  Created: Book[ID=BK-1, Title='The Great Gatsby', ... Type=Physical]
  Created: Book[ID=BK-2, Title='To Kill a Mockingbird', ... Type=Physical]
  Created: Book[ID=BK-3, Title='Clean Code', ... Type=E-Book]
  Created: Book[ID=BK-4, Title='The Alchemist', ... Type=AudioBook]
  Registered: Member[ID=USR-1, Name='Alice Johnson', ... Type=Student, Role=MEMBER]
  Registered: Member[ID=USR-3, Name='Prof. Carol Davis', ... Type=Teacher, Role=MEMBER]
  Registered: Member[ID=USR-4, Name='Dr. Smith', ... Type=Admin, Role=ADMIN]
```

### State Pattern — Book Lifecycle

```
=========================================
STATE PATTERN - BOOK LIFECYCLE MANAGEMENT
=========================================
  Book: 'The Great Gatsby' | Status: AVAILABLE
  Transition: AVAILABLE -> RESERVED
  Book: 'The Great Gatsby' | Status: RESERVED
  Transition: RESERVED -> BORROWED
  Book: 'The Great Gatsby' | Status: BORROWED
  Transition: BORROWED -> AVAILABLE (returned)
  Book: 'The Great Gatsby' | Status: AVAILABLE
  Transition: AVAILABLE -> LOST
  Book: 'The Great Gatsby' | Status: LOST
  Transition: LOST -> AVAILABLE (found)
  Book: 'The Great Gatsby' | Status: AVAILABLE
```

### Command Pattern — Borrow/Return with Undo

```
=========================================
COMMAND PATTERN - BORROWING & RETURNING
=========================================
  Executing: Borrow Book: To Kill a Mockingbird by Alice Johnson
  Borrowed: To Kill a Mockingbird | Due: 2026-08-10
  Executing: Borrow Book: Clean Code by Bob Williams
  Borrowed: Clean Code | Due: 2026-08-10

Active Loans (2):
  LN-1    Alice Johnson       To Kill a Mockingbird   ACTIVE
  LN-2    Bob Williams        Clean Code              ACTIVE

  UNDO: Returned The Alchemist (borrow cancelled)
Active Loans (2):
  LN-2    Bob Williams        Clean Code              ACTIVE
  LN-1    Alice Johnson       To Kill a Mockingbird   RETURNED
```

### Strategy Pattern — Fine Calculation

```
=========================================
STRATEGY PATTERN - FINE CALCULATION
=========================================
  6 days overdue, $0.50/day standard rate
  
  Standard Member: $3.00 (Standard Fine ($0.50/day))
  Student Member:  $1.50 (Student Discount ($0.25/day))
  Teacher Member:  $0.00 (No Fine for Teachers ($0.00/day))
```

---

## 8. Pattern Justification

### 8.1 Factory Method Pattern (Creational)

**Why it was chosen:**
The system needs to create multiple types of books (Physical, E-Book, AudioBook) and members (Student, Teacher, Admin). Without a factory, client code would need to instantiate concrete classes directly, creating coupling between the client and the constructors.

**How it solves the problem:**
- `BookFactory` defines a `createBook()` method that subclasses override to produce specific book types
- `MemberFactory` does the same for members
- New book/member types can be added by creating new factory subclasses without changing existing code (Open/Closed Principle)
- Client code depends on abstract factory types, not concrete implementations (Dependency Inversion)

**Benefits:**
- Eliminates tight coupling between client and concrete product classes
- Centralizes object creation logic
- Supports easy extension with new product types

### 8.2 Strategy Pattern (Behavioral)

**Why it was chosen:**
Different member types incur different late fees. Without a pattern, this would require complex conditional logic (if-else/switch) scattered across the codebase. Adding a new fee policy would mean modifying existing classes.

**How it solves the problem:**
- `FineCalculationStrategy` defines a common interface for fine calculation
- Concrete strategies (`StandardFineStrategy`, `StudentDiscountStrategy`, `TeacherNoFineStrategy`) encapsulate specific algorithms
- `FineCalculator` delegates to the selected strategy at runtime
- New policies can be added without modifying existing strategy classes

**Benefits:**
- Eliminates complex conditional logic
- Algorithms can be swapped at runtime
- New strategies can be added without modifying existing code

### 8.3 Command Pattern (Behavioral)

**Why it was chosen:**
Borrowing and returning books are requests that should be logged, queued, and potentially undone. Without a command pattern, these operations would be directly coupled to their invokers and could not be reversed.

**How it solves the problem:**
- `Command` interface defines `execute()`, `undo()`, and `getDescription()`
- `BorrowBookCommand` and `ReturnBookCommand` encapsulate all data needed to perform and reverse the operation
- `CommandInvoker` maintains a history stack for undo operations
- Each command knows exactly how to reverse its own effects

**Benefits:**
- Supports undo/redo functionality
- Decouples request sender from request processor
- Commands can be logged, queued, or serialized

### 8.4 State Pattern (Behavioral)

**Why it was chosen:**
A book transitions through multiple states (Available, Borrowed, Reserved, Under Repair, Lost) throughout its lifecycle. Each state imposes different constraints on what operations are allowed. Without the State pattern, the Book class would be polluted with complex conditional logic checking current status on every operation.

**How it solves the problem:**
- `BookState` interface defines operations for each possible action
- Each state class (`AvailableState`, `BorrowedState`, etc.) implements only the valid transitions for that state
- `BookContext` delegates all operations to the current state object
- Adding a new state does not affect existing state classes

**Benefits:**
- Eliminates complex if-else chains based on status
- State-specific behavior is localized in individual state classes
- Transitions are explicit and easy to trace
- Adding new states does not require modifying existing code

---

## 9. Conclusion

This project demonstrates how **Design Patterns** can be combined to build a maintainable, extensible, and testable Library Management System.

| Pattern | Category | Key Contribution |
|---|---|---|
| Factory Method | Creational | Polymorphic object creation without client coupling |
| Strategy | Behavioral | Runtime-swappable fine calculation algorithms |
| Command | Behavioral | Request encapsulation with undo support |
| State | Behavioral | Clean state-dependent behavior without conditionals |

The system satisfies all functional requirements:
- ✅ User Management (via Factory Method)
- ✅ Core Business Process — borrowing/returning books (via Command Pattern)
- ✅ Status Tracking — book lifecycle management (via State Pattern)
- ✅ Reports can be generated from system data
- ✅ Security — role-based member creation via Factory

All 5 JUnit tests pass, verifying the correctness of each pattern implementation. The code follows clean code practices, SOLID principles, and is structured for easy extension.

---

## 10. JUnit Test Results

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

| Test | Pattern | Status |
|---|---|---|
| testBookFactoryPattern | Factory Method | ✅ Pass |
| testMemberFactoryPattern | Factory Method | ✅ Pass |
| testStrategyPattern | Strategy | ✅ Pass |
| testCommandPattern | Command | ✅ Pass |
| testStatePattern | State | ✅ Pass |

---

*End of Report*
