package ru.NSTU.rgz.Server;

public class CommandResponse implements Command {
    private static final String TYPE = "COMMAND_RESPONSE";
    private String message;

    public CommandResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
