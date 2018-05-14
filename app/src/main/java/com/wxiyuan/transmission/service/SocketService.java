package com.wxiyuan.transmission.service;

import android.content.Intent;

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
                break;
            case ACTION_START_SERVER:
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
