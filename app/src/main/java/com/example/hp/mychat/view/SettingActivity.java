package com.example.hp.mychat.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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

public class SettingActivity extends Activity implements SetView,View.OnClickListener {

    @BindView(R.id.own_ip)
    private EditText ownIP;
    @BindView(R.id.others_ip)
    private EditText othersIP;
    @BindView(R.id.add_others)
    private Button addOthers;
    @BindView(R.id.owns_ip)
    private EditText ownsIP;
    @BindView(R.id.add_owns)
    private Button addOwns;
    @BindView(R.id.current)
    private ListView current;

    private SettingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        presenter = new SettingPresenterImpl();
    }

    @Override
    public void onClick(View view) {
        String ip = "";
        switch (view.getId()){
            case R.id.add_others:
                 ip = othersIP.getText().toString();
                if(checkIP(ip)){
                    presenter.addOthers(ip);
                }
                break;
            case R.id.add_owns:
                ip = othersIP.getText().toString();
                if(checkIP(ip)){
                    presenter.addOwns(ip);
                }
                break;

        }
    }

    private boolean checkIP(String ip){
        return true;
    }

    @Override
    public void sendViewMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showCurrent(List<User> users) {

    }
}
