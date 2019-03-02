package com.example.hp.mychat.server;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.hp.mychat.AppApplication;
import com.example.hp.mychat.model.Msg;
import com.example.hp.mychat.model.Server;
import com.example.hp.mychat.model.User;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketClient {
    private int port;
    private DatagramSocket client;
    private Thread th_listen;
    private boolean isWork = true;
    private MsgListener msgListener;
    private List<User> users;
    private User user;
    final static String WE_USER = "WE";
    final static String APPLY_USER = "AD";
    final static String ALL_USER = "USERS";
    final static String EXIT_USER = "EXIT";

    final static String SPLIT = "-";


    public SocketClient(User user){
        users = new ArrayList<>();
        this.user = user;
        this.port = user.getPort();
    }

    public void setMsgListener(MsgListener msgListener) {
        this.msgListener = msgListener;
    }

    public void create()throws SocketException{
        client = new DatagramSocket( port );
        th_listen = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    receive();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } );
        th_listen.start();
        System.out.println( "启动成功" );
    }

    protected String replaceBlank(String str){
        String dest = null;
        if(str == null){
            return dest;
        }else{
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
            return dest;
        }
    }
    private String filterCode(String string) {
        if (string != null) {
            string = string.trim();
            byte[] zero = new byte[1];
            zero[0] = (byte) 0;
            String s = new String(zero);
            string = string.replace(s, "");
        }
        return string;
    }


    private void receive()throws IOException{
        while (isWork){
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket( data,data.length );
            client.receive( packet );
            //String message = new String( data );
            String message =  new String( data ) ;
            System.out.println(message);
            String[] msgs = filterCode(message).split("-");
            if(msgs.length >=2){
               switch (msgs[0]){
                   case APPLY_USER:
                       User apply_user = new User();
                       apply_user.setPort(port);
                       apply_user.setIp(msgs[1]);
                       addUser(apply_user);
                       sendUsersMsg(apply_user);
                       sendWEUserMsg(apply_user,AppApplication.userList);
                       Msg msg = new Msg();
                       msg.setUserName(msgs[1]);
                       msg.setMessage(msgs[1]+"加入聊天");
                       msgListener.receiveMsg( msg );
                       break;
                   case WE_USER:
                       String resultIP = msgs[1];
                       if( !resultIP.equals( user.getIp() ) ){
                           User user = new User();
                           user.setIp(resultIP);
                           user.setPort(port);
                           addUser(user);
                       }
                       Msg we_msg = new Msg();
                       we_msg.setUserName(resultIP);
                       we_msg.setMessage(msgs[1]+"加入聊天");
                       msgListener.receiveMsg( we_msg );
                       break;
                   case ALL_USER:
                       AppApplication.userList.clear();
                       addUser(user);
                       for(int i=1;i<msgs.length;i++){
                           User user = new User();
                           user.setIp(msgs[i]);
                           user.setPort(port);
                           addUser(user);
                       }
                       String users_ip = packet.getAddress().toString();
                       Msg users_msg = new Msg();
                       users_msg.setUserName(users_ip);
                       users_msg.setMessage(String.format("加入%s的群聊",users_ip));
                       msgListener.receiveMsg(users_msg);
                       break;
                   case EXIT_USER:
                       User user = new User();
                       user.setIp( msgs[1]);
                       user.setPort(port);
                       AppApplication.userList.remove(user);
                       Msg exit_msg = new Msg();
                       exit_msg.setUserName( msgs[1] );
                       exit_msg.setMessage( msgs[1]+"退出聊天" );
                       msgListener.receiveMsg(exit_msg);
                       break;
               }
            }else {
                Msg msg = new Msg();
                msg.setUserName( packet.getAddress().getHostName() );
                msg.setMessage( message );
                msgListener.receiveMsg( msg );
            }
        }
    }

    public void sendMsg(final String message,List<User> users){
        synchronized (users){
            for(final User user:users){
                if( user.getIp().equals(this.user.getIp())){
                    continue;
                }
                final String sendIP = user.getIp();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] send = message.getBytes();
                        DatagramPacket sendPacket = null;
                        try {
                            sendPacket = new DatagramPacket( send, send.length,InetAddress.getByName( sendIP ),port);
                        } catch (UnknownHostException e) {
                            Msg error = new Msg();
                            error.setUserName(user.getName());
                            error.setMessage(e.getMessage());
                            msgListener.errorMsg(error);
                            return;
                        }
                        try {
                            client.send( sendPacket );
                            System.out.println(message);
                        } catch (IOException e) {
                            Msg error = new Msg();
                            error.setUserName(user.getName());
                            error.setMessage(e.getMessage());
                            msgListener.errorMsg(error);
                        }
                    }
                }).start();
            }
            Msg msg = new Msg();
            msg.setUserName(user.getName());
            msg.setMessage(message);
            msgListener.sendMsg(msg);
        }
    }

    private boolean isExitUser(User user){
        for (User user1:AppApplication.userList){
            if(user1.getIp().equals(user.getIp())){
                return true;
            }
        }
        return false;
    }

    private void addUser(User user){
        if(!isExitUser(user)){
            AppApplication.userList.add(user);
        }
    }


    private void sendUsersMsg(User user){
        String message = ALL_USER + getAllUsers(AppApplication.userList);
        List<User> users = new ArrayList<>();
        users.add(user);
        sendMsg(message,users);
    }

    private void sendWEUserMsg(User user,List<User> users){
        String message = WE_USER + SPLIT+user.getIp();
        sendMsg(message,users);
    }

    public void sendExitUser(){
        String exit_msg = EXIT_USER + SPLIT + user.getIp();
        sendMsg(exit_msg,AppApplication.userList);
        AppApplication.userList.clear();
    }

    public void sendAddGroupChat(String ip){
        sendExitUser();
        addUser(user);
        String apply_msg = APPLY_USER + SPLIT + user.getIp();
        User user = new User();
        user.setIp(ip);
        user.setPort(port);
        List<User> users = new ArrayList<>();
        users.add(user);
        sendMsg(apply_msg,users);
    }

    public void sendWelcomeUser(String ip){
        User user = new User();
        user.setIp(ip);
        user.setPort(port);
        addUser(user);
        sendWEUserMsg(user,AppApplication.userList);
        sendUsersMsg(user);
    }


    private static String getAllUsers(List<User> users){
        String result = "";
        for (User user:users){
            result += SPLIT + user.getIp();
        }
        return result;
    }


    public static String getIP(Context context){
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

}
