package com.example.hp.mychat;

public interface MsgListener {
    void sendMsg(Msg msg);
    void receiveMsg(Msg msg);
    void errorMsg(Msg msg);
}
