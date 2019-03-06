package com.example.hp.mychat.presenter;

import android.app.Application;

import com.example.hp.mychat.AppApplication;
import com.example.hp.mychat.model.User;
import com.example.hp.mychat.presenter.ipresenter.SettingPresenter;
import com.example.hp.mychat.server.SocketClient;
import com.example.hp.mychat.view.iview.SetView;

import java.util.ArrayList;
import java.util.List;

public class SettingPresenterImpl implements SettingPresenter {

    private SetView setView;
    private SocketClient client;
    public SettingPresenterImpl(SetView setView,SocketClient client){
        this.setView = setView;
        this.client = client;
    }

    @Override
    public void initView() {
        setView.showCurrent(AppApplication.userList);
        setView.showOwnIP(AppApplication.user.getIp());
    }

    @Override
    public void addOthers(String ip) {
        if(client!=null){
            client.sendAddGroupChat(ip);
        }
    }

    @Override
    public void addOwns(String ip) {
        if(client!=null){
            client.sendWelcomeUser(ip);
        }
    }

    @Override
    public void addServer(String ip) {
        if(client!=null){
            String apply_msg = SocketClient.APPLY_USER + SocketClient.SPLIT + AppApplication.user.getIp();
            User user = new User();
            user.setIp(ip);
            user.setPort(5252);
            List<User> users = new ArrayList<>();
            users.add(user);
            AppApplication.addUser(user);
            client.sendMsg(apply_msg,users);
        }
    }

    @Override
    public void exitServer(String ip) {
        String apply_msg = SocketClient.EXIT_USER + SocketClient.SPLIT + AppApplication.user.getIp();
        User user = new User();
        user.setIp(ip);
        user.setPort(5252);
        List<User> users = new ArrayList<>();
        users.add(user);
        AppApplication.userList.remove(user);
        client.sendMsg(apply_msg,users);
    }
}
