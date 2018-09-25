package com.phuclongappv2.xk.phuclongappver2.Model;

public class User {
    String phone;
    String name;
    String address;
    String birthday;
    String error_msg;

    public User() {

    }

    public User(String phone, String name, String address, String birthday, String error_msg) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.birthday = birthday;
        this.error_msg = error_msg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
