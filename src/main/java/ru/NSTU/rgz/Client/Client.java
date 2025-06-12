package ru.NSTU.rgz.Client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.NSTU.rgz.Main;

import java.io.IOException;

public class Client extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main app (server).fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Client_controller controller = fxmlLoader.getController();
        stage.setTitle("library (Server)");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            controller.shutdown();
        });
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}