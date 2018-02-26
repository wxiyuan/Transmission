package com.wxiyuan.transmission;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.client.android.CaptureActivity;
import com.wxiyuan.transmission.entry.DialogEntry;
import com.wxiyuan.transmission.ui.CustomAlertDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG_P2P_DISABLE_DIALOG = "p2p_disable";

    private WifiP2pDeviceList mPeers;
    private CustomAlertDialog mP2pDisableDialog;

    private Button mCreateQrBtn;
    private Button mScanQrBtn;
    private View mMainBtnPart;
    private View mMainQrPart;
    private View mCloseQrBtn;
    private ImageView mQrImage;
    private ProgressBar mQrProgressBar;

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
        setContentView(R.layout.activity_main);
        // Init wifiP2p broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        // Init views
        mMainBtnPart = findViewById(R.id.main_btn_part);
        mMainQrPart = findViewById(R.id.main_qr_part);
        mCreateQrBtn = findViewById(R.id.btn_create_qr);
        mCreateQrBtn.setOnClickListener(this);
        mScanQrBtn = findViewById(R.id.btn_scan_qr);
        mScanQrBtn.setOnClickListener(this);
        mQrImage = findViewById(R.id.main_qr_image);
        mCloseQrBtn = findViewById(R.id.close_qr_btn);
        mCloseQrBtn.setOnClickListener(this);
        mQrProgressBar = findViewById(R.id.qr_load_progress);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_qr:
                mMainBtnPart.setVisibility(View.GONE);
                mMainQrPart.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_scan_qr:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.close_qr_btn:
                mMainQrPart.setVisibility(View.GONE);
                mMainBtnPart.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
