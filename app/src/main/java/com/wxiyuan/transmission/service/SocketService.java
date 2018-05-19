package com.wxiyuan.transmission.service;

import android.content.Intent;

import com.wxiyuan.transmission.network.ClientSocketManager;
import com.wxiyuan.transmission.network.ServerSocketManager;

public class SocketService extends BaseService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent == null || intent.getAction() == null) ? "" : intent.getAction();
        if (intent != null) {
            wifip2pinfo = intent.getParcelableExtra(KEY_WIFI_P2P_INFO);
        }
        switch (action) {
            case ACTION_START_CLIENT:
                ClientSocketManager.startClient(wifip2pinfo);
                break;
            case ACTION_START_SERVER:
                ServerSocketManager.startServer(wifip2pinfo);
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ClientSocketManager.stop();
        ServerSocketManager.stop();
        super.onDestroy();
    }

}
