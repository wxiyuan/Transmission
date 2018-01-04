package com.transmission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.transmission.entry.DialogEntry;
import com.transmission.ui.CustomAlertDialog;

public class WifiP2pActivity extends BaseActivity {

    private static final String TAG_P2P_DISABLE_DIALOG = "p2p_disable";

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
                    if (wifiP2pEnabled) {
                        dismissP2pDisableDialog();
                    } else {
                        showP2pDisableDialog();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_p2p);
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver() {
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

    private void showP2pDisableDialog() {
        DialogEntry entry = new DialogEntry("WifiP2p disabled",
                "WifiP2p disabled, please check wifi switch.", "OK", null);
        mP2pDisableDialog = CustomAlertDialog.newInstance(entry);
        mP2pDisableDialog.show(getFragmentManager(), TAG_P2P_DISABLE_DIALOG);
    }

    private void dismissP2pDisableDialog() {
        if (mP2pDisableDialog == null || !mP2pDisableDialog.getDialog().isShowing()) {
            return;
        }
        mP2pDisableDialog.dismiss();
    }

}
