package com.example.hp.mychat.view.iview;

import com.example.hp.mychat.model.User;

import java.util.List;

public interface SetView {
    void sendViewMsg(String msg);
    void showCurrent(List<User> users);
    void showOwnIP(String ip);
}
