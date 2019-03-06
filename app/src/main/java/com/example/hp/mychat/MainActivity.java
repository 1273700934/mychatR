package com.example.hp.mychat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    public SocketClient client = new SocketClient(AppApplication.user);

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

    public void sendInitServer(){
        String apply_msg = SocketClient.APPLY_USER + SocketClient.SPLIT + AppApplication.user.getIp();
        User user = new User();
        user.setIp("114.116.67.36");
        user.setPort(5252);
        List<User> users = new ArrayList<>();
        users.add(user);
        AppApplication.addUser(user);
        client.sendMsg(apply_msg,users);
    }

    private void listen(){
        client.create();
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
                /*WindowManager windowManager = getWindowManager();
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager.LayoutParams layoutParams = settingDialog.getWindow().getAttributes();
                layoutParams.width = dm.widthPixels;
                layoutParams.height = dm.heightPixels;
                settingDialog.getWindow().setAttributes(layoutParams);*/
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
       // client.stop();
    }
    @Override
    public void onResume(){
        super.onResume();
        listen();
        sendInitServer();
    }

    @Override
    public void onStart(){
        super.onStart();
//        listen();
//        sendInitServer();
    }


}
