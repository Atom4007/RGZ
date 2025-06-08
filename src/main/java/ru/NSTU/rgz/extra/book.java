package ru.NSTU.rgz.extra;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public class book implements Serializable{
    private String title;
    private String author;
    private String genre;
    private String status;
    private String user;
    private String extra;

    public book(String title, String author, String genre, String status, String user, String extra) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.status = status;
        this.user = user;
        this.extra = extra;
    }

    public String getTitle() {return title;}
    public StringProperty titleProperty() {return new SimpleStringProperty(title);}
    public void setTitle(String title) {this.title = title;}

    public String getAuthor() {return author;}
    public StringProperty authorProperty() {return new SimpleStringProperty(author);}
    public void setAuthor(String author) {this.author = author;}

    public String getGenre() {return genre;}
    public StringProperty genreProperty() {return new SimpleStringProperty(genre);}
    public void setGenre(String genre) {this.genre = genre;}

    public String getStatus() {return status;}
    public StringProperty statusProperty() {return new SimpleStringProperty(status);}
    public void setStatus(String status) {this.status = status;}

    public String getUser() {return user;}
    public StringProperty userProperty() {return new SimpleStringProperty(user);}
    public void setUser(String user) {this.user = user;}

    public String getExtra() {return extra;}
    public StringProperty extraProperty() {return new SimpleStringProperty(extra);}
    public void setExtra(String extra){this.extra = extra;}


    public String toString() {
        return "Book{" +
                "title=" + title +
                ", author=" + author +
                ", genre=" + genre +
                ", status=" + status +
                ", user=" + user +
                ", extra=" + extra +
                '}';
    }
}
