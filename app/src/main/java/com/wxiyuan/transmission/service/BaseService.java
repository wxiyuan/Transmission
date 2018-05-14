package com.wxiyuan.transmission.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.IBinder;

@SuppressLint("Registered")
public class BaseService extends Service {

    public static final String ACTION_START_CLIENT = "start_client";
    public static final String ACTION_START_SERVER = "start_server";
    public static final String KEY_WIFI_P2P_INFO = "wifi_p2p_info";

    protected WifiP2pInfo wifip2pinfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
