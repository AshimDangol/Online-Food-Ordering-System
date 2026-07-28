package com.library;

import com.library.factory.*;
import com.library.model.*;
import com.library.strategy.*;
import com.library.command.*;
import com.library.state.*;

import java.time.LocalDate;
import java.util.*;

public class Main {
    private static final Map<String, Book> bookCatalog = new HashMap<>();
    private static final Map<String, Member> members = new HashMap<>();
    private static final Map<String, Loan> loans = new HashMap<>();
    private static final Map<String, Fine> fines = new HashMap<>();
    private static final Map<String, BookContext> bookStates = new HashMap<>();
    private static final CommandInvoker commandInvoker = new CommandInvoker();
    private static final FineCalculator fineCalculator = new FineCalculator();
    private static int idCounter = 0;

    private static String nextId() {
        return String.valueOf(++idCounter);
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.println("=========================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("   Design Patterns: Factory, Strategy, Command, State");
        System.out.println("=========================================");
        System.out.println();

        // =========================================================
        // 1. FACTORY METHOD PATTERN
        // =========================================================
        System.out.println("=========================================");
        System.out.println("FACTORY METHOD PATTERN - BOOK & MEMBER CREATION");
        System.out.println("=========================================");
        System.out.println("Demonstrates: Subclasses decide which objects to create.");
        System.out.println("PhysicalBookFactory, EBookFactory, AudioBookFactory create different book types.");
        System.out.println("StudentMemberFactory, TeacherMemberFactory, AdminFactory create different members.");
        System.out.println();

        BookFactory physFactory = new PhysicalBookFactory();
        BookFactory ebookFactory = new EBookFactory();
        BookFactory audioFactory = new AudioBookFactory();

        Book book1 = physFactory.createAndDisplay("BK-1", "The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5", "Fiction");
        Book book2 = physFactory.createAndDisplay("BK-2", "To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4", "Fiction");
        Book book3 = ebookFactory.createAndDisplay("BK-3", "Clean Code", "Robert C. Martin", "978-0-13-235088-4", "Technology");
        Book book4 = audioFactory.createAndDisplay("BK-4", "The Alchemist", "Paulo Coelho", "978-0-06-250217-4", "Philosophy");
        System.out.println();

        bookCatalog.put(book1.getId(), book1);
        bookCatalog.put(book2.getId(), book2);
        bookCatalog.put(book3.getId(), book3);
        bookCatalog.put(book4.getId(), book4);
        bookStates.put(book1.getId(), new BookContext(book1));
        bookStates.put(book2.getId(), new BookContext(book2));
        bookStates.put(book3.getId(), new BookContext(book3));
        bookStates.put(book4.getId(), new BookContext(book4));

        MemberFactory studentFactory = new StudentMemberFactory();
        MemberFactory teacherFactory = new TeacherMemberFactory();
        MemberFactory adminFactory = new AdminFactory();

        Member alice = studentFactory.createAndDisplay("USR-1", "Alice Johnson", "alice@student.edu", "555-1001", "Student");
        Member bob = studentFactory.createAndDisplay("USR-2", "Bob Williams", "bob@student.edu", "555-1002", "Student");
        Member carol = teacherFactory.createAndDisplay("USR-3", "Prof. Carol Davis", "carol@teacher.edu", "555-2001", "Teacher");
        Member admin = adminFactory.createAndDisplay("USR-4", "Dr. Smith", "smith@library.com", "555-0000", "Admin");
        System.out.println();

        members.put(alice.getId(), alice);
        members.put(bob.getId(), bob);
        members.put(carol.getId(), carol);
        members.put(admin.getId(), admin);

        // =========================================================
        // 2. STATE PATTERN
        // =========================================================
        System.out.println("=========================================");
        System.out.println("STATE PATTERN - BOOK LIFECYCLE MANAGEMENT");
        System.out.println("=========================================");
        System.out.println("Demonstrates: Object behavior changes based on internal state.");
        System.out.println("BookState: AVAILABLE -> BORROWED -> RESERVED -> UNDER_REPAIR -> LOST");
        System.out.println();

        System.out.println("  [State Transitions for 'The Great Gatsby']");
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: AVAILABLE -> RESERVED");
        bookStates.get("BK-1").reserve();
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: RESERVED -> BORROWED");
        bookStates.get("BK-1").borrow();
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: BORROWED -> AVAILABLE (returned)");
        bookStates.get("BK-1").returnBook();
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: AVAILABLE -> LOST");
        bookStates.get("BK-1").lose();
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: LOST -> AVAILABLE (found)");
        bookStates.get("BK-1").returnBook();
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: AVAILABLE -> UNDER_REPAIR");
        bookStates.get("BK-1").repair();
        bookStates.get("BK-1").displayState();
        System.out.println("  Transition: UNDER_REPAIR -> AVAILABLE (repair complete)");
        bookStates.get("BK-1").repair();
        bookStates.get("BK-1").displayState();
        System.out.println();

        // =========================================================
        // 3. COMMAND PATTERN
        // =========================================================
        System.out.println("=========================================");
        System.out.println("COMMAND PATTERN - BORROWING & RETURNING");
        System.out.println("=========================================");
        System.out.println("Demonstrates: Encapsulating requests as objects with undo support.");
        System.out.println("BorrowBookCommand, ReturnBookCommand executed via CommandInvoker.");
        System.out.println();

        bookStates.get("BK-2").borrow();
        Command borrow1 = new BorrowBookCommand("LN-1", alice, book2, loans, bookCatalog, 14);
        commandInvoker.executeCommand(borrow1);
        System.out.println();

        bookStates.get("BK-3").borrow();
        Command borrow2 = new BorrowBookCommand("LN-2", bob, book3, loans, bookCatalog, 14);
        commandInvoker.executeCommand(borrow2);
        System.out.println();

        bookStates.get("BK-4").borrow();
        Command borrow3 = new BorrowBookCommand("LN-3", carol, book4, loans, bookCatalog, 14);
        commandInvoker.executeCommand(borrow3);
        System.out.println();

        displayAllLoans();

        System.out.println("  [Undo last borrow (Carol's book)]");
        commandInvoker.undoLastCommand();
        bookStates.get("BK-4").returnBook();
        displayAllLoans();

        System.out.println("  [Returning Alice's book]");
        bookStates.get("BK-2").returnBook();
        Command return1 = new ReturnBookCommand("LN-1", loans);
        commandInvoker.executeCommand(return1);
        displayAllLoans();
        System.out.println();

        // =========================================================
        // 4. STRATEGY PATTERN
        // =========================================================
        System.out.println("=========================================");
        System.out.println("STRATEGY PATTERN - FINE CALCULATION");
        System.out.println("=========================================");
        System.out.println("Demonstrates: Selecting algorithms at runtime.");
        System.out.println("FineCalculationStrategy: StandardFine, StudentDiscount, TeacherNoFine.");
        System.out.println("Strategy selected based on member type.");
        System.out.println();

        Loan overdueSample = new Loan("LN-SAMPLE", new Member("USR-X", "Sample", "x@t.com", "555", "Student", "MEMBER"),
                new Book("BK-X", "Sample Book", "Author", "111", "Fiction", "Physical"),
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));

        System.out.println("  6 days overdue, $0.50/day standard rate");
        System.out.println();

        fineCalculator.setStrategy(new StandardFineStrategy());
        double standardFine = fineCalculator.calculateFine(overdueSample);
        System.out.println("  Standard Member: " + String.format("$%.2f", standardFine) + " (" + fineCalculator.getStrategyName() + ")");

        fineCalculator.setStrategy(new StudentDiscountStrategy());
        double studentFine = fineCalculator.calculateFine(overdueSample);
        System.out.println("  Student Member:  " + String.format("$%.2f", studentFine) + " (" + fineCalculator.getStrategyName() + ")");

        fineCalculator.setStrategy(new TeacherNoFineStrategy());
        double teacherFine = fineCalculator.calculateFine(overdueSample);
        System.out.println("  Teacher Member:  " + String.format("$%.2f", teacherFine) + " (" + fineCalculator.getStrategyName() + ")");
        System.out.println();

        System.out.println("  [Applying Strategy to actual overdue loan]");
        System.out.println("  Bob (Student) borrowed Clean Code - calculating fine:");
        fineCalculator.setStrategy(new StudentDiscountStrategy());
        Loan bobLoan = loans.get("LN-2");
        if (bobLoan != null && bobLoan.isOverdue()) {
            double fine = fineCalculator.calculateFine(bobLoan);
            System.out.println("    Fine: " + String.format("$%.2f", fine));
        } else {
            System.out.println("    No overdue fine (book not overdue yet)");
        }
        System.out.println();

        // =========================================================
        // SUMMARY
        // =========================================================
        System.out.println("=========================================");
        System.out.println("DESIGN PATTERNS SUMMARY");
        System.out.println("=========================================");
        System.out.println("  Creational:");
        System.out.println("    1. Factory Method - BookFactory, MemberFactory (polymorphic creation)");
        System.out.println();
        System.out.println("  Behavioral:");
        System.out.println("    2. Strategy  - FineCalculationStrategy (dynamic algorithm selection)");
        System.out.println("    3. Command   - BorrowBookCommand, ReturnBookCommand (undoable operations)");
        System.out.println("    4. State     - BookState (state-dependent behavior)");
        System.out.println();
        System.out.println("=========================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM - DEMO END");
        System.out.println("=========================================");
    }

    private static void displayAllLoans() {
        System.out.println("-----------------------------------------");
        System.out.println("Active Loans (" + loans.size() + ")");
        System.out.println("-----------------------------------------");
        if (loans.isEmpty()) {
            System.out.println("  No active loans.");
        } else {
            for (Loan loan : loans.values()) {
                System.out.printf("  %-8s %-20s %-25s %s%n",
                    loan.getId(), loan.getMember().getName(),
                    loan.getBook().getTitle(), loan.getStatus());
            }
        }
        System.out.println();
    }
}