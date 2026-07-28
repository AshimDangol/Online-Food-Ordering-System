package com.library.state;

public class LostState implements BookState {
    @Override
    public void borrow(BookContext context) {
        System.out.println("  Cannot borrow '" + context.getBook().getTitle() + "' - it is lost.");
    }

    @Override
    public void returnBook(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' found and returned, now AVAILABLE.");
        context.setState(new AvailableState());
    }

    @Override
    public void reserve(BookContext context) {
        System.out.println("  Cannot reserve '" + context.getBook().getTitle() + "' - it is lost.");
    }

    @Override
    public void repair(BookContext context) {
        System.out.println("  Cannot repair a lost book.");
    }

    @Override
    public void lose(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is already marked as lost.");
    }

    @Override
    public String getStateName() {
        return "LOST";
    }
}
