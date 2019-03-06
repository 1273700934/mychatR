package com.example.hp.mychat.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.mychat.MainActivity;
import com.example.hp.mychat.R;
import com.example.hp.mychat.model.User;
import com.example.hp.mychat.presenter.SettingPresenterImpl;
import com.example.hp.mychat.presenter.ipresenter.SettingPresenter;
import com.example.hp.mychat.view.iview.SetView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingDialog extends Dialog implements SetView {

    @BindView(R.id.own_ip)
    EditText ownIP;
    @BindView(R.id.others_ip)
    EditText othersIP;
    @BindView(R.id.add_others)
    Button addOthers;
    @BindView(R.id.owns_ip)
    EditText ownsIP;
    @BindView(R.id.add_owns)
    Button addOwns;
    @BindView(R.id.current)
    ListView current;
    @BindView(R.id.server_ip)
    EditText serverIP;
    @BindView(R.id.add_server)
    Button addServer;
    @BindView(R.id.exit_server)
    Button exitServer;
    private SettingPresenter presenter;
    MainActivity main;

    public SettingDialog(MainActivity context) {
        super(context);
        this.main = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        Window dialogWindow = this.getWindow();
        DisplayMetrics m = this.main.getApplicationContext().getResources().getDisplayMetrics();
        final WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (m.heightPixels);
        p.width = (int) (m.widthPixels);
        dialogWindow.setAttributes(p);
        this.setCancelable(true);
        presenter = new SettingPresenterImpl(this,main.client);
        presenter.initView();

        addOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = othersIP.getText().toString();
                if(checkIP(ip)){
                    presenter.addOthers(ip);
                }
            }
        });

        addOwns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ownsIP.getText().toString();
                if(checkIP(ip)){
                    presenter.addOwns(ip);
                }
            }
        });
        addServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = serverIP.getText().toString();
                if(checkIP(ip)){
                    presenter.addServer(ip);
                }
            }
        });
        exitServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = serverIP.getText().toString();
                if(checkIP(ip)){
                    presenter.exitServer(ip);
                }
            }
        });
    }

    private boolean checkIP(String ip){
        return true;
    }

    @Override
    public void sendViewMsg(final String msg) {
        main.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(main.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showCurrent(List<User> users) {


    }

    @Override
    public void showOwnIP(String ip) {
        this.ownIP.setText(ip);
    }


}
