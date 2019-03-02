package com.example.hp.mychat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hp.mychat.model.Msg;
import com.example.hp.mychat.model.PersonChat;
import com.example.hp.mychat.model.Server;
import com.example.hp.mychat.model.User;
import com.example.hp.mychat.server.MsgListener;
import com.example.hp.mychat.server.SocketClient;
import com.example.hp.mychat.view.SettingDialog;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ChatAdapter chatAdapter;
    /**
     * 声明ListView
     */
    private ListView lv_chat_dialog;
    public SocketClient client;

    EditText et_chat_message = null;
    /**
     * 集合
     */
    private List<PersonChat> personChats = new ArrayList<PersonChat>();
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    /**
                     * ListView条目控制在最后一行
                     */
                    lv_chat_dialog.setSelection(personChats.size());
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_chat_dialog = (ListView) findViewById(R.id.lv_chat_dialog);
        Button btn_chat_message_send = (Button) findViewById(R.id.btn_chat_message_send);
        et_chat_message = (EditText) findViewById(R.id.et_chat_message);
        chatAdapter = new ChatAdapter(this, personChats);
        lv_chat_dialog.setAdapter(chatAdapter);
        listen();
        btn_chat_message_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(et_chat_message.getText().toString())) {
                    Toast.makeText(MainActivity.this, "发送内容不能为空",0).show();
                    return;
                }
                String msg = et_chat_message.getText().toString();
                client.sendMsg(msg,AppApplication.userList);
            }
        });
    }



    private void addMsg(String msg, boolean meSend){
        PersonChat personChat = new PersonChat();
        personChat.setMeSend(meSend);
        personChat.setChatMessage(msg);
        personChats.add(personChat);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_chat_message.setText("");
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
            }
        });
    }

    private void listen(){
        client = new SocketClient(AppApplication.user);
        client.setMsgListener(new MsgListener() {
            @Override
            public void sendMsg(Msg msg) {
                addMsg(msg.getMessage(),true);
            }

            @Override
            public void receiveMsg(Msg msg) {
                addMsg(msg.getMessage(),false);
            }

            @Override
            public void errorMsg(Msg msg) {
                addMsg(msg.getMessage(),false);
            }
        });
        try {
            client.create();
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
               SettingDialog settingDialog = new SettingDialog(this);
               settingDialog.show();
                break;

            default:
                break;
        }
        return true;
    }

}
