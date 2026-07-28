package com.library.state;

public class UnderRepairState implements BookState {
    @Override
    public void borrow(BookContext context) {
        System.out.println("  Cannot borrow '" + context.getBook().getTitle() + "' - it is under repair.");
    }

    @Override
    public void returnBook(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is under repair, cannot return.");
    }

    @Override
    public void reserve(BookContext context) {
        System.out.println("  Cannot reserve '" + context.getBook().getTitle() + "' - it is under repair.");
    }

    @Override
    public void repair(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' repair complete, now AVAILABLE.");
        context.setState(new AvailableState());
    }

    @Override
    public void lose(BookContext context) {
        System.out.println("  Book '" + context.getBook().getTitle() + "' is now LOST.");
        context.setState(new LostState());
    }

    @Override
    public String getStateName() {
        return "UNDER_REPAIR";
    }
}
