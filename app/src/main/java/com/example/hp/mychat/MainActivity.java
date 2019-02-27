package com.example.hp.mychat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ChatAdapter chatAdapter;
    /**
     * 声明ListView
     */
    private ListView lv_chat_dialog;
    private SocketClient client;

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
                client.sendMsg(msg);
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
        String ip = SocketClient.getIP(this);
        client = new SocketClient("192.168.6.109",5251,5252,ip);
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
}
