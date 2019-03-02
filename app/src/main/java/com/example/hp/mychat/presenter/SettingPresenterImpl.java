package com.example.hp.mychat.presenter;

import android.app.Application;

import com.example.hp.mychat.AppApplication;
import com.example.hp.mychat.model.User;
import com.example.hp.mychat.presenter.ipresenter.SettingPresenter;
import com.example.hp.mychat.server.SocketClient;
import com.example.hp.mychat.view.iview.SetView;

import java.util.ArrayList;

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
}
