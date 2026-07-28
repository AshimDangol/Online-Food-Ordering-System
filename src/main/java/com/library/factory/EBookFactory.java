package com.library.factory;

import com.library.model.Book;

public class EBookFactory extends BookFactory {
    @Override
    public Book createBook(String id, String title, String author, String isbn, String category) {
        return new Book(id, title, author, isbn, category, "E-Book");
    }
}
