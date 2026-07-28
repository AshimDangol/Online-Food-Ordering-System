package com.library.state;

import com.library.model.Book;

public class BookContext {
    private final Book book;
    private BookState state;

    public BookContext(Book book) {
        this.book = book;
        this.state = new AvailableState();
    }

    public Book getBook() { return book; }
    public BookState getState() { return state; }
    public void setState(BookState state) { this.state = state; }

    public void borrow() { state.borrow(this); }
    public void returnBook() { state.returnBook(this); }
    public void reserve() { state.reserve(this); }
    public void repair() { state.repair(this); }
    public void lose() { state.lose(this); }

    public void displayState() {
        System.out.println("  Book: '" + book.getTitle() + "' | Status: " + state.getStateName());
    }
}
