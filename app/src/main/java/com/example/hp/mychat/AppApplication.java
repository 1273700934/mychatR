package com.example.hp.mychat;

import android.app.Application;

import com.example.hp.mychat.model.User;
import com.example.hp.mychat.server.SocketClient;

import java.util.ArrayList;
import java.util.List;

public class AppApplication extends Application {
    public static User user;
    public static List<User> userList;
    @Override
    public void onCreate(){
        super.onCreate();
        user = new User();
        user.setIp(SocketClient.getIP(this));
        user.setPort(5251);
        userList = new ArrayList<>();
        userList.add(user);
    }
}
