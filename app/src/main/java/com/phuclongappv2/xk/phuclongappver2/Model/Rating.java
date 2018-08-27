package com.phuclongappv2.xk.phuclongappver2.Model;

public class Rating {
    private String id;
    private String drinkId;
    private String rate;
    private String comment;
    private String date;
    private String status;

    public Rating(){

    }


    public Rating(String id, String drinkId, String rate, String comment, String date, String status) {
        this.id = id;
        this.drinkId = drinkId;
        this.rate = rate;
        this.comment = comment;
        this.date = date;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}