package com.wxiyuan.transmission;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.client.android.CaptureActivity;
import com.wxiyuan.transmission.entry.DialogEntry;
import com.wxiyuan.transmission.handler.MainHandler;
import com.wxiyuan.transmission.ui.CustomAlertDialog;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String STATE_CONNECT_NONE = "none";
    public static final String STATE_CONNECTING = "connecting";
    public static final String STATE_CONNECTED = "connected";

    private static final String TAG_P2P_DISABLE_DIALOG = "p2p_disable";
    private final int QR_CODE_SIZE = 700;
    private final int DECODE_QR_REQUEST_CODE = 0x20;

    private Button mCreateQrBtn;
    private Button mScanQrBtn;
    private View mMainBtnPart;
    private View mMainQrPart;
    private View mCloseQrBtn;
    private ImageView mQrImage;
    private ProgressBar mQrProgressBar;

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pDeviceList mPeers;
    private CustomAlertDialog mP2pDisableDialog;
    private MainHandler mMainHandler = new MainHandler(this);
    private String mDesMac = null;
    private WifiP2pDevice mDesDevice = null;
    private Bitmap mMacQr;
    private String mConnectState = STATE_CONNECT_NONE;
    private boolean mIsDiscover = false;

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
        if (mMacQr != null) {
            mMacQr.recycle();
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void tearDown() {
        if (mIsDiscover) {
            stopDiscover();
        }
        mPeers = null;
        mDesMac = null;
        mDesDevice = null;
        mConnectState = STATE_CONNECT_NONE;
    }

    private void initWifiP2pManager() {
        if (mWifiP2pManager != null) {
            return;
        }
        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(this, this.getMainLooper(), null);
            if (mChannel == null) {
                //Failure to set up connection
                Utils.showToast(this, "Failed to set up connection with wifi p2p service");
                mWifiP2pManager = null;
            }
        } else {
            Utils.showToast(this, "mWifiP2pManager is null !");
        }
    }

    public void displayQrCode() {
        if (mMacQr == null) {
            mMainQrPart.setVisibility(View.GONE);
            mMainBtnPart.setVisibility(View.VISIBLE);
            Utils.showToast(this, "Create qr-code failed.");
            return;
        }
        mQrProgressBar.setVisibility(View.GONE);
        mQrImage.setImageBitmap(mMacQr);
        mCloseQrBtn.setVisibility(View.VISIBLE);
        discoverPeers();
    }

    private void discoverPeers() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onFailure(int i) {
                Utils.showToast(MainActivity.this, "Start discover failed.");
            }
        });
    }

    private void stopDiscover() {
        mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onFailure(int i) {
                Utils.showToast(MainActivity.this, "Stop discover failed.");
            }
        });
    }

    private void connect(WifiP2pDevice peer) {
        if (!mConnectState.equals(STATE_CONNECT_NONE)) {
            Utils.showToast(MainActivity.this, "Connect work is already running now.");
            return;
        }
        if (peer.status == WifiP2pDevice.CONNECTED) {
            Utils.showToast(MainActivity.this, "The device has connected.");
            return;
        }
        if (peer.status == WifiP2pDevice.UNAVAILABLE) {
            Utils.showToast(MainActivity.this, "The device is unavailable.");
            return;
        }
        if (!peer.wpsPbcSupported()) {
            Utils.showToast(MainActivity.this, "The device not support wps-pbc.");
            return;
        }
        mConnectState = STATE_CONNECTING;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = peer.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onFailure(int reason) {
                mConnectState = STATE_CONNECT_NONE;
                Utils.showToast(MainActivity.this, "Start connect failed, fail code is " + reason);
            }
        });
    }

    private void disConnect() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onFailure(int reason) {
                Utils.showToast(MainActivity.this, "Disconnect failed, fail code is " + reason);
            }
        });
    }

    private void handleP2pStateChanged(boolean enabled) {
        if (enabled) {
            initWifiP2pManager();
            dismissP2pDisableDialog();
        } else {
            showP2pDisableDialog();
        }
    }

    private void handlePeersChanged(WifiP2pDeviceList peers) {
        if (TextUtils.isEmpty(mDesMac)) {
            return;
        }
        for (WifiP2pDevice peer : peers.getDeviceList()) {
            String address = peer.deviceAddress;
            if (address.equals(mDesMac)) {
                mDesDevice = peer;
                connect(mDesDevice);
                break;
            }
        }
    }

    private void handleConnectionChanged(NetworkInfo networkInfo, WifiP2pInfo wifip2pinfo) {
        if (networkInfo.isConnected()) {
            mConnectState = STATE_CONNECTED;
        } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            tearDown();
            mConnectState = STATE_CONNECT_NONE;
        }
    }

    private void handleThisDeviceChanged(WifiP2pDevice device) {
        //
    }

    private void handleDiscoverStateChanged(int state) {
        mIsDiscover = (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DECODE_QR_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    break;
                }
                String result = data.getStringExtra("result");
                if (Utils.isValidMacAddress(result)) {
                    mDesMac = result;
                    discoverPeers();
                } else {
                    Utils.showToast(MainActivity.this, "Invalid qr-code for Transmission.");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_qr:
                mMainBtnPart.setVisibility(View.GONE);
                mMainQrPart.setVisibility(View.VISIBLE);
                if (mMacQr == null) {
                    new Thread() {
                        @Override
                        public void run() {
                            mMacQr = Utils.createQrBitmap(Utils.getMacAddress(), QR_CODE_SIZE);
                            mMainHandler.sendEmptyMessage(MainHandler.MSG_QR_CODE_READY);
                        }
                    }.start();
                } else {
                    displayQrCode();
                }
                break;
            case R.id.btn_scan_qr:
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, DECODE_QR_REQUEST_CODE);
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
