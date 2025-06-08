package ru.NSTU.rgz.Server;

import ru.NSTU.rgz.extra.book;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 12345;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен и ожидает подключения...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress().getHostAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            while (true) {
                try {
                    Command command = (Command) in.readObject();

                    if (command != null) {
                        switch (command.getType()) {
                            case "SEND_BOOK":
                                SendBookCommand sendBookCommand = (SendBookCommand) command;
                                saveBookToDatabase(sendBookCommand.getBook(), sendBookCommand.getReceiverId());
                                out.writeObject(new CommandResponse("Книга успешно отправлена"));
                                break;
                            case "GET_BOOKS":
                                GetBooksCommand getBooksCommand = (GetBooksCommand) command;
                                List<book> books = getBooksFromDatabase(getBooksCommand.getClientId());
                                out.writeObject(new BooksResponse(books));
                                break;
                            default:
                                out.writeObject(new CommandResponse("ERROR: Неизвестная команда"));
                        }
                    } else {
                        System.out.println("Клиент отключился");
                        return;
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Ошибка десериализации объекта: " + e.getMessage());
                    out.writeObject(new CommandResponse("ERROR: Ошибка десериализации объекта"));
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка ввода/вывода: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    private static void saveBookToDatabase(book book, String receiverId) {
        String sql = "INSERT INTO books_exchange (title, author, genre, status, user, extra, receiver_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getGenre());
            pstmt.setString(4, book.getStatus());
            pstmt.setString(5, book.getUser());
            pstmt.setString(6, book.getExtra());
            pstmt.setString(7, receiverId);

            pstmt.executeUpdate();
            System.out.println("Книга сохранена в базу данных");

        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении книги в базу данных: " + e.getMessage());
        }
    }

    private static List<book> getBooksFromDatabase(String clientId) {
        List<book> books = new ArrayList<>();
        String sql = "SELECT title, author, genre, status, user, extra FROM books_exchange WHERE receiver_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, clientId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                book book = new book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("status"),
                        rs.getString("user"),
                        rs.getString("extra")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении книг из базы данных: " + e.getMessage());
        }

        return books;
    }
}