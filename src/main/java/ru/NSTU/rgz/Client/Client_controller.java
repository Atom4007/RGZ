package ru.NSTU.rgz.Client;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.NSTU.rgz.extra.book;
import javafx.scene.control.ComboBox;
import javafx.application.Application;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Client_controller implements Initializable {

    @FXML
    private TableView<book> bookTableView;
    @FXML
    private TableColumn<book, String> titleColumn;
    @FXML
    private TableColumn<book, String> authorColumn;
    @FXML
    private TableColumn<book, String> genreColumn;
    @FXML
    private TableColumn<book, String> statusColumn;
    @FXML
    private TableColumn<book, String> userColumn;
    @FXML
    private TableColumn<book, String> extraColumn;

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField authorTextField;
    @FXML
    private TextField genreTextField;
    @FXML
    private TextField userTextField;
    @FXML
    private TextField extraTextField;
    @FXML
    private Button addBookButton;
    @FXML
    private Button editBookButton;
    @FXML
    private Button deleteBookButton;
    @FXML
    private MenuItem reconnectButton;

    private ObservableList<book> bookData = FXCollections.observableArrayList();
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    @FXML
    private ComboBox statusComboBox;

    @FXML
    private CheckMenuItem authorCheck;
    @FXML
    private CheckMenuItem genreCheck;

    private final File savefile = new File("library.dat");
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { //инициализация таблицы
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        userColumn.setCellValueFactory(cellData -> cellData.getValue().userProperty());
        extraColumn.setCellValueFactory(cellData -> cellData.getValue().extraProperty());
        userTextField.setEditable(false);
        userTextField.setVisible(false);
        userTextField.setText("");
        System.out.println("trying to connect");
        connectToServer();

        bookTableView.setItems(bookData);

        BooleanProperty authorTick = new SimpleBooleanProperty(false);
        BooleanProperty genreTick = new SimpleBooleanProperty(false);
        authorCheck.selectedProperty().bindBidirectional(authorTick);
        genreCheck.selectedProperty().bindBidirectional(genreTick);

        //статусы книг
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Available",
                        "Booked",
                        "In use",
                        "Not available"
                );
        statusComboBox.setItems(options); // комбобокс контролирует возможность записи в user
        statusComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String status = newValue.toString();
            switch (status) {
                case "Booked":
                    userTextField.setEditable(true);
                    userTextField.setVisible(true);
                    break;
                case "In use":
                    userTextField.setEditable(true);
                    userTextField.setVisible(true);
                    break;
                default:
                    userTextField.setEditable(false);
                    userTextField.setVisible(false);
                    userTextField.setText("");
            }});
        addBookButton.setOnAction(event -> { // кнопка добавления
            String title = titleTextField.getText();
            String author = authorTextField.getText();
            String genre = genreTextField.getText();
            String status;
            if (!statusComboBox.getSelectionModel().isEmpty()){
                status = statusComboBox.getSelectionModel().getSelectedItem().toString();
            } else status = "Undefined";
            String user = userTextField.getText();
            String extra = extraTextField.getText();
            if (!title.isEmpty() && !author.isEmpty()) {
                book newBook = new book(title, author, genre, status, user, extra);
                bookData.add(newBook);
                titleTextField.clear();
                authorTextField.clear();
                genreTextField.clear();
                userTextField.clear();
                extraTextField.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in author and title");
                alert.showAndWait();
            }
            sendListToServer();
        });

        editBookButton.setOnAction(event -> { // кнопка изменения
            book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
            if(selectedBook != null) {
                if(!authorTick.get() && !genreTick.get()){
                    if (!titleTextField.getText().isEmpty() && !authorTextField.getText().isEmpty())
                        selectedBook.setTitle(titleTextField.getText());
                    selectedBook.setAuthor(authorTextField.getText());
                    selectedBook.setGenre(genreTextField.getText());
                    selectedBook.setStatus(statusComboBox.getSelectionModel().getSelectedItem().toString());
                    selectedBook.setUser(userTextField.getText());
                    selectedBook.setExtra(extraTextField.getText());

                }
                if(!authorTick.get() && genreTick.get()){
                    for(book current : bookData){
                        if(current.getGenre().equals(genreTextField.getText())){
                            //current.setExtra();
                            current.setUser(userTextField.getText());
                            current.setStatus(statusComboBox.getSelectionModel().getSelectedItem().toString());
                        }
                    }
                }
                if(!genreTick.get() && authorTick.get()){
                    for(book current : bookData){
                        if(current.getAuthor().equals(authorTextField.getText())){
                            //current.setExtra();
                            current.setUser(userTextField.getText());
                            current.setStatus(statusComboBox.getSelectionModel().getSelectedItem().toString());
                        }
                    }
                }
                if(genreTick.get() && authorTick.get()){
                    for(book current : bookData){
                        if(current.getAuthor().equals(authorTextField.getText()) && current.getGenre().equals(genreTextField.getText())){
                            //current.setExtra();
                            current.setUser(userTextField.getText());
                            current.setStatus(statusComboBox.getSelectionModel().getSelectedItem().toString());
                        }
                    }
                }
                titleTextField.clear();
                authorTextField.clear();
                genreTextField.clear();
                userTextField.clear();
                extraTextField.clear();
                editBookButton.setDisable(true);
                deleteBookButton.setDisable(true);
                bookTableView.refresh();
                sendListToServer();
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in author and title");
                alert.showAndWait();
            }
        });

        deleteBookButton.setOnAction(event -> {// кнопка удаления
            book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
            if(selectedBook != null) {
                bookData.remove(selectedBook);
                titleTextField.clear();
                authorTextField.clear();
                genreTextField.clear();
                userTextField.clear();
                extraTextField.clear();
                editBookButton.setDisable(true);
                deleteBookButton.setDisable(true);
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("No book is selected");
                alert.showAndWait();
            }
            sendListToServer();
        });

        reconnectButton.setOnAction(event -> {//попытаться переподключиться вручную
            connectToServer();
        });

        //просмотр существующей записи
        bookTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if(!authorTick.get() && !genreTick.get()) {
                    deleteBookButton.setDisable(false);
                }
                editBookButton.setDisable(false);
                titleTextField.setText(newSelection.getTitle());
                authorTextField.setText(newSelection.getAuthor());
                genreTextField.setText(newSelection.getGenre());
                statusComboBox.getSelectionModel().select(newSelection.getStatus());
                if (newSelection.getStatus().equals("In use") || newSelection.getStatus().equals("Booked")) {
                    userTextField.setEditable(true);
                    userTextField.setVisible(true);
                }
                userTextField.setText(newSelection.getUser());
                extraTextField.setText(newSelection.getExtra());
            }
            else {editBookButton.setDisable(true); deleteBookButton.setDisable(true);}
        });
        //редактирование нескольких записей одновременно
        authorCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                addBookButton.setDisable(true);
                deleteBookButton.setDisable(true);
                titleTextField.setVisible(false);
            } else {
                if (!genreTick.get()) {
                    addBookButton.setDisable(false);
                    titleTextField.setVisible(true);
                    deleteBookButton.setDisable(false);
                }
            }
        });
        genreCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                addBookButton.setDisable(true);
                deleteBookButton.setDisable(true);
                titleTextField.setVisible(false);
            } else {
                if (!genreTick.get()) {
                    addBookButton.setDisable(false);
                    titleTextField.setVisible(true);
                    deleteBookButton.setDisable(false);
                }
            }
        });
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("connecting...");
                List<book> initialList = (List<book>) inputStream.readObject();
                Platform.runLater(() -> {
                    bookData.addAll(initialList);
                });
                System.out.println("connected to server, data received");

                while (true) { //получение данных клиентом при изменении на сервере
                    try {
                        List<book> receivedList = (List<book>) inputStream.readObject();
                        Platform.runLater(() -> {
                            bookData.clear();
                            bookData.addAll(receivedList);
                        });
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error: Received an object of an unknown class.");
                        break;
                    } catch (IOException e) {
                        System.out.println("Server disconnected.");

                        break;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Could not connect to server. Is the server running?", ButtonType.OK);
                    alert.showAndWait();
                });
            }
        }).start();
    }

    private void sendListToServer() {
        new Thread(() -> {
            try {
                List<book> serializableList = new ArrayList<>(bookData);
                outputStream.writeObject(serializableList);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error sending data to server.", ButtonType.OK);
                    alert.showAndWait();
                });
            }
        }).start();
    }
    public void shutdown() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Shutdown...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}