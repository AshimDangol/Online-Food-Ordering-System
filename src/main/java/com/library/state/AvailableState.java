package com.library.state;

public class AvailableState implements BookState {
    @Override
    public void borrow(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now BORROWED.");
        context.setState(new BorrowedState());
    }

    @Override
    public void returnBook(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is already available.");
    }

    @Override
    public void reserve(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now RESERVED.");
        context.setState(new ReservedState());
    }

    @Override
    public void repair(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now UNDER REPAIR.");
        context.setState(new UnderRepairState());
    }

    @Override
    public void lose(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now LOST.");
        context.setState(new LostState());
    }

    @Override
    public String getStateName() {
        return "AVAILABLE";
    }
}
