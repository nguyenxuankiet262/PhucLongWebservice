package com.phuclongappv2.xk.phuclongappver2.Model;

import java.io.Serializable;

public class Drink implements Serializable {
    String ID;
    String imageCold;
    String imageHot;
    String categoryID;
    String Name;
    int Price;

    public Drink(){

    }

    public Drink(String ID, String imageCold, String imageHot, String categoryID, String name, int price) {
        this.ID = ID;
        this.imageCold = imageCold;
        this.imageHot = imageHot;
        this.categoryID = categoryID;
        Name = name;
        Price = price;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageCold() {
        return imageCold;
    }

    public void setImageCold(String imageCold) {
        this.imageCold = imageCold;
    }

    public String getImageHot() {
        return imageHot;
    }

    public void setImageHot(String imageHot) {
        this.imageHot = imageHot;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
