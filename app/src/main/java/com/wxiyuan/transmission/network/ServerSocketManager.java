package com.wxiyuan.transmission.network;

import android.net.wifi.p2p.WifiP2pInfo;

import com.wxiyuan.transmission.Const;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketManager implements Runnable {

    public static ServerSocketManager instance;

    private ServerSocket mServerSocket;

    public ServerSocketManager(WifiP2pInfo wifip2pinfo) {
        try {
            mServerSocket = new ServerSocket(Const.SOCKET_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void startServer(WifiP2pInfo wifip2pinfo) {
        if (instance == null) {
            instance = new ServerSocketManager(wifip2pinfo);
        }
    }

    @Override
    public void run() {
        //
    }
}
