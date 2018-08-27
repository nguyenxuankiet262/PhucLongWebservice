package com.phuclongappv2.xk.phuclongappver2.Model;

public class User {
    String id;
    String name;
    String address;
    String phone;
    String image;
    String password;
    String email;

    public User(){

    }

    public User(String id, String name, String address, String phone, String image, String email, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.image = image;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
