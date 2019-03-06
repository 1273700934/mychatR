package com.example.hp.mychat.presenter.ipresenter;

public interface SettingPresenter {
    void initView();
    void addOthers(String ip);
    void addOwns(String ip);
    void addServer(String ip);
    void exitServer(String ip);
}
