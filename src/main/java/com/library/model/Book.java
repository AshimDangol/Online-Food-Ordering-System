package com.library.model;

public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private String type;

    public Book(String id, String title, String author, String isbn, String category, String type) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.type = type;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return String.format("Book[ID=%s, Title='%s', Author='%s', ISBN=%s, Category=%s, Type=%s]",
                id, title, author, isbn, category, type);
    }
}
