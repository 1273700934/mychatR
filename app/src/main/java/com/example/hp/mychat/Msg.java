package com.example.hp.mychat;

public class Msg {
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String userName;
    private String message;

    public String getMsgR(){
        return userName+"-"+message;
    }
}
