package com.phuclongappv2.xk.phuclongappver2.Model;

public class Feedback {
    String content;
    String reply;

    public Feedback(){

    }

    public Feedback(String content, String reply) {
        this.content = content;
        this.reply = reply;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
