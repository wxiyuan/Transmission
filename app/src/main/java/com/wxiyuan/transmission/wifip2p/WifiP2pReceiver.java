package com.wxiyuan.transmission.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.List;


public class WifiP2pReceiver extends BroadcastReceiver {

    private static WifiP2pReceiver instance;

    private IntentFilter filter;

    private List<WifiP2pListener> listeners = new ArrayList<>();

    public static WifiP2pReceiver getInstance() {
        if (instance == null) {
            instance = new WifiP2pReceiver();
        }
        return instance;
    }

    public void register(Context context) {
        context.registerReceiver(instance, getP2pFilter());
    }

    public void register(Context context, WifiP2pListener p2pListener) {
        this.register(context);
        addWifiP2pListener(p2pListener);
    }

    public void unRegister(Context context) {
        context.unregisterReceiver(instance);
    }

    public void unRegister(Context context, WifiP2pListener p2pListener) {
        this.unRegister(context);
        removeWifiP2pListener(p2pListener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                boolean wifiP2pEnabled =
                        intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
                                WifiP2pManager.WIFI_P2P_STATE_DISABLED)
                                == WifiP2pManager.WIFI_P2P_STATE_ENABLED;
                for (WifiP2pListener listener : listeners) {
                    listener.onP2pStateChanged(wifiP2pEnabled);
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                WifiP2pDeviceList peers =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                for (WifiP2pListener listener : listeners) {
                    listener.onP2pPeersChanged(peers);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                WifiP2pInfo wifip2pinfo =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                WifiP2pGroup wifiP2pGroup =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                for (WifiP2pListener listener : listeners) {
                    listener.onP2pConnectionChanged(networkInfo, wifip2pinfo, wifiP2pGroup);
                }
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                WifiP2pDevice thisDevice =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                for (WifiP2pListener listener : listeners) {
                    listener.onThisDeviceChanged(thisDevice);
                }
                break;
            case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE,
                        WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                for (WifiP2pListener listener : listeners) {
                    listener.onP2pDiscoveryChanged(discoveryState);
                }
                break;
        }
    }

    private IntentFilter getP2pFilter() {
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
            filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        }
        return filter;
    }

    public void addWifiP2pListener(WifiP2pListener p2pListener) {
        for (WifiP2pListener listener : listeners) {
            if (listener == p2pListener) {
                return;
            }
        }
        listeners.add(p2pListener);
    }

    public void removeWifiP2pListener(WifiP2pListener p2pListener) {
        listeners.remove(p2pListener);
    }

    public interface WifiP2pListener {
        void onP2pStateChanged(boolean enabled);
        void onP2pPeersChanged(WifiP2pDeviceList peers);
        void onP2pConnectionChanged(NetworkInfo networkInfo, WifiP2pInfo wifip2pinfo, WifiP2pGroup wifiP2pGroup);
        void onThisDeviceChanged(WifiP2pDevice thisDevice);
        void onP2pDiscoveryChanged(int discoveryState);
    }
}
