package ru.NSTU.rgz.Server;
import ru.NSTU.rgz.extra.book;

import java.util.List;

public class BooksResponse implements Command {
    private static final String TYPE = "BOOKS_RESPONSE";
    private List<book> books;

    public BooksResponse(List<book> books) {
        this.books = books;
    }

    public List<book> getBooks() {
        return books;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}


