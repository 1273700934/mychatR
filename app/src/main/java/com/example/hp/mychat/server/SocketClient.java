package com.example.hp.mychat.server;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

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
    private Server server;
    private int port;
    private DatagramSocket client;
    private Thread th_listen;
    private boolean isWork = true;
    private MsgListener msgListener;
    private List<User> users;
    private User user;
    final static String SERVER_WE = "WE";
    final static String ADD_USER = "AD";
    final static String ALL_USER = "USERS";


    public SocketClient(User user,Server server){
        users = new ArrayList<>();
        this.user = user;
        this.port = user.getPort();
        this.server = server;
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
            String[] msgs = filterCode(message).split("-");
            if(msgs.length >=2){
               switch (msgs[0]){
                   case SERVER_WE:
                       String resultIP = msgs[1];
                       if( !resultIP.equals( user.getIp() ) ){
                           User user = new User();
                           user.setIp(resultIP);
                           user.setPort(port);
                           users.add(user);
                       }
                       Msg msg = new Msg();
                       msg.setUserName(server.getIp());
                       msg.setMessage(msgs[1]+"加入聊天");
                       msgListener.receiveMsg( msg );
                       break;
                   case ALL_USER:
                       for(int i=1;i<msgs.length;i++){
                           User user = new User();
                           user.setIp(msgs[i]);
                           user.setPort(port);
                           users.add(user);
                       }
               }
            }else {
                Msg msg = new Msg();
                msg.setUserName( packet.getAddress().getHostName() );
                msg.setMessage( message );
                msgListener.receiveMsg( msg );
            }
        }
    }

    public void sendMsg(final String message ){
        for(final User user:users){
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

    public void sendInitMsg(final String msg ){
         final String message = ADD_USER+"-" + msg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] send = message.getBytes();
                DatagramPacket sendPacket = null;
                try {
                    sendPacket = new DatagramPacket( send, send.length,InetAddress.getByName( server.getIp() ),server.getPort());
                } catch (UnknownHostException e) {
                    Msg error = new Msg();
                    error.setUserName(user.getName());
                    error.setMessage(e.getMessage());
                    msgListener.errorMsg(error);
                }
                try {
                    client.send( sendPacket );
                } catch (IOException e) {
                    Msg error = new Msg();
                    error.setUserName(user.getName());
                    error.setMessage(e.getMessage());
                    msgListener.errorMsg(error);
                }
            }
        }).start();
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
