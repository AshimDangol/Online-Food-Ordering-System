package com.library.state;

public class BorrowedState implements BookState {
    @Override
    public void borrow(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is already borrowed.");
    }

    @Override
    public void returnBook(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now AVAILABLE.");
        context.setState(new AvailableState());
    }

    @Override
    public void reserve(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is reserved (will be available soon).");
        context.setState(new ReservedState());
    }

    @Override
    public void repair(BookContext context) {
        System.out.println("  Cannot repair a borrowed book.");
    }

    @Override
    public void lose(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now LOST.");
        context.setState(new LostState());
    }

    @Override
    public String getStateName() {
        return "BORROWED";
    }
}
