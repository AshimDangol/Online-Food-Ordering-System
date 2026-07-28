package com.library.command;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import java.time.LocalDate;
import java.util.Map;

public class BorrowBookCommand implements Command {
    private final String loanId;
    private final Member member;
    private final Book book;
    private final Map<String, Loan> loans;
    private final Map<String, Book> bookCatalog;
    private final int maxBorrowDays;
    private Loan createdLoan;

    public BorrowBookCommand(String loanId, Member member, Book book,
                             Map<String, Loan> loans, Map<String, Book> bookCatalog, int maxBorrowDays) {
        this.loanId = loanId;
        this.member = member;
        this.book = book;
        this.loans = loans;
        this.bookCatalog = bookCatalog;
        this.maxBorrowDays = maxBorrowDays;
    }

    @Override
    public boolean execute() {
        if (!bookCatalog.containsKey(book.getId())) {
            System.out.println("  ERROR: Book " + book.getTitle() + " is not in the catalog.");
            return false;
        }
        for (Loan l : loans.values()) {
            if (l.getBook().getId().equals(book.getId()) && "ACTIVE".equals(l.getStatus())) {
                System.out.println("  ERROR: Book " + book.getTitle() + " is already borrowed.");
                return false;
            }
        }
        createdLoan = new Loan(loanId, member, book, LocalDate.now(), LocalDate.now().plusDays(maxBorrowDays));
        loans.put(loanId, createdLoan);
        System.out.println("  Borrowed: " + book.getTitle() + " | Due: " + createdLoan.getDueDate());
        return true;
    }

    @Override
    public void undo() {
        if (createdLoan != null) {
            loans.remove(createdLoan.getId());
            System.out.println("  UNDO: Returned " + book.getTitle() + " (borrow cancelled)");
        }
    }

    @Override
    public String getDescription() {
        return "Borrow Book: " + book.getTitle() + " by " + member.getName();
    }
}
