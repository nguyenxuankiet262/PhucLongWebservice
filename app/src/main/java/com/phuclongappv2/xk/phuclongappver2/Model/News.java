package com.phuclongappv2.xk.phuclongappver2.Model;

public class News {
    private int id;
    private String image;
    private String link;
    private String date;
    private String name;
    private int status;

    News(){

    }

    public News(int id, String image, String link, String date, String name, int status) {
        this.id = id;
        this.image = image;
        this.link = link;
        this.date = date;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
