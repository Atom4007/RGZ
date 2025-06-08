package ru.NSTU.rgz.Server;

public class GetBooksCommand implements Command {
    private static final String TYPE = "GET_BOOKS";
    private String clientId;

    public GetBooksCommand(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
