package ru.NSTU.rgz.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ru.NSTU.rgz.extra.book;

public class Server {

    private static final int PORT = 12345;
    private static final List<ObjectOutputStream> clientOutputStreams = new ArrayList<>();
    private static List<book> bookData = new ArrayList<>();// "База данных"
    private static final File savefile = new File("library.dat");

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(10); // Пул потоков для клиентов
        if (savefile.exists()) {
            System.out.println("loading data from file");
            load();
        }
        else {
            System.out.println("generating data");
            bookData.add(new book("Война и мир", "Лев Толстой", "Роман", "Available", "", ""));
            bookData.add(new book("Преступление и наказание", "Федор Достоевский", "Роман", "In use", "user123", ""));
            bookData.add(new book("Мастер и Маргарита", "Михаил Булгаков", "Роман", "Available", "", "Мистическая сатира"));
            bookData.add(new book("1984", "Джордж Оруэлл", "Антиутопия", "Available", "", ""));
            bookData.add(new book("Гордость и предубеждение", "Джейн Остин", "Роман", "Available", "", ""));
            save();
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                executor.submit(new ClientHandler(clientSocket));
            }
        }
    }


    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                clientOutputStreams.add(outputStream);
                sendListToClient(outputStream, bookData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        List<book> receivedList = (List<book>) inputStream.readObject();
                        bookData = receivedList;
                        broadcastList(bookData);
                        save();
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error: Received an object of an unknown class.");
                        break;
                    } catch (EOFException e) {
                        System.out.println("Client disconnected.");
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                try {
                    clientOutputStreams.remove(outputStream);
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                    clientSocket.close();
                    save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastList(List<book> list) {
            for (ObjectOutputStream out : clientOutputStreams) {
                sendListToClient(out, new ArrayList<>(list));
            }
        }

        private void sendListToClient(ObjectOutputStream out, List<book> list) {
            try {
                out.writeObject(list);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                clientOutputStreams.remove(out);
            }
        }


    }
    static void load(){ // загрузка из файла
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
    static void save(){ //сохранение в файл
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
}