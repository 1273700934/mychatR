package com.example.hp.mychat;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class SocketClient {
    private String ip;
    private int port;
    private int sendport;
    private DatagramSocket client;
    private Thread th_listen;
    private boolean isWork = true;
    private MsgListener msgListener;
    private String userIP;


    public SocketClient(String ip,int port,int send,String user_ip){
        this.ip = ip;
        this.port = port;
        this.sendport = send;
        this.userIP = user_ip;
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

    private void receive()throws IOException{
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket( data,1024 );
        while (isWork){
            client.receive( packet );
            String message = new String( data );
            String[] msgs = message.split("-");
            if(msgs.length <2){
                continue;
            }
            Msg msg = new Msg();
            msg.setUserName( msgs[0] );
            msg.setMessage( msgs[1] );
            msgListener.receiveMsg( msg );
        }
    }

    public void sendMsg( String message ){
        final Msg msg = new Msg();
        msg.setUserName( userIP );
        msg.setMessage( message );
        final String msgR = msg.getMsgR();
        final String serverIP = ip;
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] send = msgR.getBytes();
                DatagramPacket sendPacket = null;
                try {
                    sendPacket = new DatagramPacket( send, send.length,InetAddress.getByName( serverIP ),sendport);
                } catch (UnknownHostException e) {
                    Msg error = new Msg();
                    error.setUserName(userIP);
                    error.setMessage(e.getMessage());
                    msgListener.errorMsg(error);
                }
                try {
                    client.send( sendPacket );
                } catch (IOException e) {
                    Msg error = new Msg();
                    error.setUserName(userIP);
                    error.setMessage(e.getMessage());
                    msgListener.errorMsg(error);
                }
            }
        }).start();
        msgListener.sendMsg(msg);
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
