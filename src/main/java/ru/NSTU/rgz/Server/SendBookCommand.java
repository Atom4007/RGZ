package ru.NSTU.rgz.Server;

import ru.NSTU.rgz.extra.book;

public class SendBookCommand implements Command {
    private static final String TYPE = "SEND_BOOK";
    private book book;
    private String receiverId;

    public SendBookCommand(book book, String receiverId) {
        this.book = book;
        this.receiverId = receiverId;
    }

    public ru.NSTU.rgz.extra.book getBook() {
        return book;
    }

    public String getReceiverId() {
        return receiverId;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
