package ru.NSTU.rgz.Server;

import java.io.Serializable;

public interface Command extends Serializable {
    String getType(); // Тип команды
}