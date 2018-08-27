package com.phuclongappv2.xk.phuclongappver2.Model;

public class Category {

    private String Id;
    private String Image;
    private String Name;

    public Category(){

    }

    public Category(String id,String image, String name) {
        Id = id;
        Image = image;
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
