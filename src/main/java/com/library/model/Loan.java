package com.library.model;

import java.time.LocalDate;

public class Loan {
    private String id;
    private Member member;
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public Loan(String id, Member member, Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.member = member;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "ACTIVE";
    }

    public String getId() { return id; }
    public Member getMember() { return member; }
    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isOverdue() {
        return "ACTIVE".equals(status) && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("Loan[ID=%s, Member='%s', Book='%s', Borrow=%s, Due=%s, Status=%s]",
                id, member.getName(), book.getTitle(), borrowDate, dueDate, status);
    }
}
