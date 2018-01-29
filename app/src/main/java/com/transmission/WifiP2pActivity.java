package com.transmission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.transmission.entry.DialogEntry;
import com.transmission.ui.CustomAlertDialog;

public class WifiP2pActivity extends BaseActivity {

    private static final String TAG_P2P_DISABLE_DIALOG = "p2p_disable";

    private WifiP2pDeviceList mPeers;
    private CustomAlertDialog mP2pDisableDialog;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
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
                    handleP2pStateChanged(wifiP2pEnabled);
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    mPeers = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                    handlePeersChanged(mPeers);
                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    NetworkInfo networkInfo =
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                    WifiP2pInfo wifip2pinfo =
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                    handleConnectionChanged(networkInfo, wifip2pinfo);
                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    WifiP2pDevice thisDevice =
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    handleThisDeviceChanged(thisDevice);
                    break;
                case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:
                    int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE,
                            WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED);
                    handleDiscoverStateChanged(discoveryState);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2p);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void handleP2pStateChanged(boolean enabled) {
        if (enabled) {
            dismissP2pDisableDialog();
        } else {
            showP2pDisableDialog();
        }
    }

    private void handlePeersChanged(WifiP2pDeviceList peers) {
        //
    }

    private void handleConnectionChanged(NetworkInfo networkInfo, WifiP2pInfo wifip2pinfo) {
        //
    }

    private void handleThisDeviceChanged(WifiP2pDevice device) {
        //
    }

    private void handleDiscoverStateChanged(int state) {
        //
    }

    private void showP2pDisableDialog() {
        if (mP2pDisableDialog == null) {
            DialogEntry entry = new DialogEntry(
                    getStringRes(R.string.dlg_p2p_disable_title),
                    getStringRes(R.string.dlg_p2p_disable_message),
                    null,
                    getStringRes(R.string.dlg_btn_cancel));
            mP2pDisableDialog = CustomAlertDialog.newInstance(entry);
        }
        if (mP2pDisableDialog.getDialog() != null && mP2pDisableDialog.getDialog().isShowing()) {
            return;
        }
        mP2pDisableDialog.show(getFragmentManager(), TAG_P2P_DISABLE_DIALOG);
    }

    private void dismissP2pDisableDialog() {
        if (mP2pDisableDialog == null || !mP2pDisableDialog.getDialog().isShowing()) {
            return;
        }
        mP2pDisableDialog.dismiss();
    }

    private String getStringRes(int resId) {
        return Utils.getStringRes(getApplicationContext(), resId);
    }

}
