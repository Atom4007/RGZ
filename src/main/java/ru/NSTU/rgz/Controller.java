package ru.NSTU.rgz;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.NSTU.rgz.extra.book;
import javafx.scene.control.ComboBox;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements Initializable {

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

    private ObservableList<book> bookData = FXCollections.observableArrayList();

    @FXML
    private ComboBox statusComboBox;

    @FXML
    private CheckMenuItem authorCheck;
    @FXML
    private CheckMenuItem genreCheck;

    private File savefile = new File("library.dat");
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
        if (savefile.exists()) {
            load();
        }
        else {
            bookData.add(new book("Война и мир", "Лев Толстой", "Роман", "Available", "", ""));
            bookData.add(new book("Преступление и наказание", "Федор Достоевский", "Роман", "In use", "user123", ""));
            bookData.add(new book("Мастер и Маргарита", "Михаил Булгаков", "Роман", "Available", "", "Мистическая сатира"));
            bookData.add(new book("1984", "Джордж Оруэлл", "Антиутопия", "Available", "", ""));
            bookData.add(new book("Гордость и предубеждение", "Джейн Остин", "Роман", "Available", "", ""));
        }
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
            save();
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
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in author and title");
                alert.showAndWait();
            }
            save();
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
            save();
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
        authorCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Код, который будет выполнен при изменении состояния
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
            // Код, который будет выполнен при изменении состояния
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
void save(){ //сохранение в файл
    if (savefile != null) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savefile))) {
            oos.writeObject(new ArrayList<>(bookData));
            System.out.println("Data saved to: " + savefile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
void load(){ // загрузка из файла
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile))) {
        ArrayList<book> loadedData = (ArrayList<book>) ois.readObject();
        bookData.clear();
        if (loadedData != null) {
            bookData.addAll(loadedData);
        }
        System.out.println("Data loaded from: " + savefile.getAbsolutePath());
    } catch (IOException | ClassNotFoundException e) {
        System.out.println("Error occurred: " + e.getMessage());
        e.printStackTrace();
    }
}
}