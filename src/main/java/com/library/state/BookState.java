package com.library.state;

import com.library.model.Book;

public interface BookState {
    void borrow(BookContext context);
    void returnBook(BookContext context);
    void reserve(BookContext context);
    void repair(BookContext context);
    void lose(BookContext context);
    String getStateName();
}
