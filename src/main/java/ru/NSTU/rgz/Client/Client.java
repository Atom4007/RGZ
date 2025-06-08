package ru.NSTU.rgz.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ru.NSTU.rgz.Server.*;
import ru.NSTU.rgz.extra.book;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Client extends Application {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final String CLIENT_ID = "client123"; // Unique client ID

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;

    private ObservableList<book> bookData = FXCollections.observableArrayList();
    private TableView<book> bookTableView = new TableView<>();
    private TextField titleTextField = new TextField();
    private TextField authorTextField = new TextField();
    private Button sendButton = new Button("Send");

    private TextField receiverIdField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Book Client");

        TableColumn<book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        bookTableView.getColumns().addAll(titleColumn, authorColumn);
        bookTableView.setItems(bookData);

        HBox inputFields = new HBox(10, new Label("Title:"), titleTextField, new Label("Author:"), authorTextField, new Label("Receiver ID:"), receiverIdField, sendButton);
        inputFields.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(bookTableView);
        root.setBottom(inputFields);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        setupConnection();

        sendButton.setOnAction(e -> {
            String title = titleTextField.getText();
            String author = authorTextField.getText();
            String receiverId = receiverIdField.getText();
            if (!title.isEmpty() && !author.isEmpty() && !receiverId.isEmpty()) {
                book newBook = new book(title, author, "Genre", "Status", "User", "Extra");
                sendBook(newBook, receiverId);
                titleTextField.clear();
                authorTextField.clear();
                receiverIdField.clear();
            }
        });

        fetchBooks();

        primaryStage.setOnCloseRequest(e -> {
            try {
                out.writeObject(null); // Exit
                if (socket != null) {
                    socket.close();
                }
                System.exit(0);
            } catch (IOException ex) {
                System.err.println("Ошибка при закрытии соединения: " + ex.getMessage());
            }
        });
    }

    private void setupConnection() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("Подключено к серверу.");
            } catch (IOException e) {
                System.err.println("Ошибка при подключении к серверу: " + e.getMessage());
            }
        }).start();
    }

    private void sendBook(book book, String receiverId) {
        new Thread(() -> {
            try {
                Command command = new SendBookCommand(book, receiverId);
                out.writeObject(command);

                CommandResponse response = (CommandResponse) in.readObject();
                System.out.println("Сервер ответил: " + response.getMessage());
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при отправке книги: " + e.getMessage());
            }
        }).start();
    }

    private void fetchBooks() {
        new Thread(() -> {
            try {
                Command command = new GetBooksCommand(CLIENT_ID);
                out.writeObject(command);

                BooksResponse response = (BooksResponse) in.readObject();
                List<book> receivedBooks = response.getBooks();

                Platform.runLater(() -> {
                    bookData.clear();
                    if (receivedBooks != null) {
                        bookData.addAll(receivedBooks);
                    }
                });

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Ошибка при получении списка книг: " + e.getMessage());
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}