package ru.NSTU.rgz.module;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class module_controller implements Initializable {

    @FXML
    private ComboBox<String> myComboBox; // fx:id="myComboBox"

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "available",
                        "booked",
                        "in use",
                        "not available"
                );
        myComboBox.setItems(options);
    }
}