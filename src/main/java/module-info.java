module ru.NSTU.rgz {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.NSTU.rgz to javafx.fxml;
    exports ru.NSTU.rgz;
    exports ru.NSTU.rgz.module;
    opens ru.NSTU.rgz.module to javafx.fxml;
    opens ru.NSTU.rgz.extra to javafx.base, javafx.fxml;

    exports ru.NSTU.rgz.Client to javafx.graphics;
}