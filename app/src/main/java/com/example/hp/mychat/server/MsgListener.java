package com.example.hp.mychat.server;

import com.example.hp.mychat.model.Msg;

public interface MsgListener {
    void sendMsg(Msg msg);
    void receiveMsg(Msg msg);
    void errorMsg(Msg msg);
}
