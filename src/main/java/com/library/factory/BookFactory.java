package com.library.factory;

import com.library.model.Book;

public abstract class BookFactory {
    public abstract Book createBook(String id, String title, String author, String isbn, String category);

    public Book createAndDisplay(String id, String title, String author, String isbn, String category) {
        Book book = createBook(id, title, author, isbn, category);
        System.out.println("  Created: " + book);
        return book;
    }
}
