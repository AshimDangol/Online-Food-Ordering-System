package com.library;

import com.library.model.*;
import com.library.factory.*;
import com.library.strategy.*;
import com.library.command.*;
import com.library.state.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibrarySystemTest {

    // =============================================================
    // FACTORY METHOD PATTERN TESTS
    // =============================================================
    @Test
    @Order(1)
    @DisplayName("Factory Method - BookFactory creates correct types")
    public void testBookFactoryPattern() {
        BookFactory physFactory = new PhysicalBookFactory();
        BookFactory ebookFactory = new EBookFactory();
        BookFactory audioFactory = new AudioBookFactory();

        Book physBook = physFactory.createBook("B1", "Test Physical", "Author A", "111", "Fiction");
        Book ebook = ebookFactory.createBook("B2", "Test EBook", "Author B", "222", "Tech");
        Book audioBook = audioFactory.createBook("B3", "Test Audio", "Author C", "333", "Science");

        assertEquals("Physical", physBook.getType());
        assertEquals("E-Book", ebook.getType());
        assertEquals("AudioBook", audioBook.getType());
    }

    @Test
    @Order(2)
    @DisplayName("Factory Method - MemberFactory creates correct member types")
    public void testMemberFactoryPattern() {
        MemberFactory studentFactory = new StudentMemberFactory();
        MemberFactory teacherFactory = new TeacherMemberFactory();
        MemberFactory adminFactory = new AdminFactory();

        Member student = studentFactory.createMember("U1", "Alice", "a@t.com", "555", "Student");
        Member teacher = teacherFactory.createMember("U2", "Bob", "b@t.com", "555", "Teacher");
        Member admin = adminFactory.createMember("U3", "Carol", "c@t.com", "555", "Admin");

        assertEquals("Student", student.getMemberType());
        assertEquals("Teacher", teacher.getMemberType());
        assertEquals("Admin", admin.getMemberType());
        assertEquals("MEMBER", student.getRole());
        assertEquals("ADMIN", admin.getRole());
    }

    // =============================================================
    // STRATEGY PATTERN TESTS
    // =============================================================
    @Test
    @Order(3)
    @DisplayName("Strategy Pattern - FineCalculator uses different strategies")
    public void testStrategyPattern() {
        Member student = new Member("U1", "Alice", "a@t.com", "555", "Student", "MEMBER");
        Member teacher = new Member("U2", "Bob", "b@t.com", "555", "Teacher", "MEMBER");
        Book book = new Book("B1", "Test", "Author", "111", "Fiction", "Physical");

        Loan overdueLoan = new Loan("L1", student, book,
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(6));

        FineCalculator calculator = new FineCalculator();

        calculator.setStrategy(new StandardFineStrategy());
        double standardFine = calculator.calculateFine(overdueLoan);
        assertTrue(standardFine > 0, "Standard fine should be > 0 for overdue loan");

        calculator.setStrategy(new StudentDiscountStrategy());
        double studentFine = calculator.calculateFine(overdueLoan);
        assertTrue(studentFine < standardFine, "Student discount should be less than standard");
        assertTrue(studentFine > 0, "Student fine should still be > 0");

        calculator.setStrategy(new TeacherNoFineStrategy());
        double teacherFine = calculator.calculateFine(overdueLoan);
        assertEquals(0.0, teacherFine, "Teacher fine should be 0");
    }

    // =============================================================
    // COMMAND PATTERN TESTS
    // =============================================================
    @Test
    @Order(4)
    @DisplayName("Command Pattern - Commands execute and undo correctly")
    public void testCommandPattern() {
        Map<String, Loan> loans = new HashMap<>();
        Map<String, Book> books = new HashMap<>();
        Map<String, Fine> fines = new HashMap<>();

        Book book = new Book("B1", "Test Book", "Author", "111", "Fiction", "Physical");
        Member member = new Member("U1", "Alice", "a@t.com", "555", "Student", "MEMBER");
        books.put("B1", book);

        CommandInvoker invoker = new CommandInvoker();

        Command borrowCmd = new BorrowBookCommand("L1", member, book, loans, books, 14);
        assertTrue(invoker.executeCommand(borrowCmd));
        assertTrue(loans.containsKey("L1"));
        assertEquals("ACTIVE", loans.get("L1").getStatus());

        Command returnCmd = new ReturnBookCommand("L1", loans);
        assertTrue(invoker.executeCommand(returnCmd));
        assertEquals("RETURNED", loans.get("L1").getStatus());

        invoker.undoLastCommand();
        assertEquals("ACTIVE", loans.get("L1").getStatus(), "Undo should revert return");

        invoker.undoLastCommand();
        assertFalse(loans.containsKey("L1"), "Undo should remove the loan");
    }

    // =============================================================
    // STATE PATTERN TESTS
    // =============================================================
    @Test
    @Order(5)
    @DisplayName("State Pattern - BookContext transitions through states correctly")
    public void testStatePattern() {
        Book book = new Book("B1", "Test Book", "Author", "111", "Fiction", "Physical");
        BookContext ctx = new BookContext(book);

        assertEquals("AVAILABLE", ctx.getState().getStateName());

        ctx.borrow();
        assertEquals("BORROWED", ctx.getState().getStateName());

        ctx.borrow();
        assertEquals("BORROWED", ctx.getState().getStateName(), "Cannot borrow when already borrowed");

        ctx.returnBook();
        assertEquals("AVAILABLE", ctx.getState().getStateName());

        ctx.reserve();
        assertEquals("RESERVED", ctx.getState().getStateName());

        ctx.repair();
        assertEquals("UNDER_REPAIR", ctx.getState().getStateName());

        ctx.repair();
        assertEquals("AVAILABLE", ctx.getState().getStateName(), "Repair complete -> Available");

        ctx.lose();
        assertEquals("LOST", ctx.getState().getStateName());

        ctx.returnBook();
        assertEquals("AVAILABLE", ctx.getState().getStateName(), "Found and returned -> Available");
    }
}