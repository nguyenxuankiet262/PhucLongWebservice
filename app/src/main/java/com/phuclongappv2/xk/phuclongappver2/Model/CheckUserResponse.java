package com.phuclongappv2.xk.phuclongappver2.Model;

public class CheckUserResponse {
    private boolean exist;
    private String err_msg;

    public CheckUserResponse() {
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }
}
