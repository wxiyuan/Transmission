package com.wxiyuan.transmission;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.wxiyuan.transmission.entry.DialogEntry;
import com.wxiyuan.transmission.handler.MainHandler;
import com.wxiyuan.transmission.listener.SimpleListener;
import com.wxiyuan.transmission.ui.CustomAlertDialog;
import com.wxiyuan.transmission.ui.ProgressDialog;
import com.wxiyuan.transmission.wifip2p.WifiP2pReceiver;


public class MainActivity extends BaseActivity implements
        View.OnClickListener, WifiP2pReceiver.WifiP2pListener {

    public static final String STATE_CONNECT_NONE = "none";
    public static final String STATE_CONNECTING = "connecting";
    public static final String STATE_CONNECTED = "connected";

    private static final String TAG_P2P_DISABLE_DIALOG = "p2p_disable";
    private final int DECODE_QR_REQUEST_CODE = 0x20;

    private Button mCreateQrBtn;
    private Button mScanQrBtn;
    private View mMainBtnPart;
    private View mMainQrPart;
    private View mStatusPart;
    private View mCloseQrBtn;
    private View mDisconnectBtn;
    private ImageView mQrImage;
    private ProgressBar mQrProgressBar;
    private TextView mStatusTitle;
    private TextView mScanTip;

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pDeviceList mPeers;
    private CustomAlertDialog mP2pDisableDialog;
    private ProgressDialog mConnectingDialog;
    private MainHandler mMainHandler = new MainHandler(this);
    private String mDesMac = null;
    private WifiP2pDevice mThisDevice = null;
    private WifiP2pDevice mDesDevice = null;
    private Bitmap mMacQr;
    private String mConnectState = STATE_CONNECT_NONE;
    private String mOwnerIp = null;
    private String mThisName = null;
    private boolean mIsDiscover = false;
    private boolean mIsOwner = false;
    private boolean mIsQrMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init wifiP2p receiver and listener
        WifiP2pReceiver.getInstance().register(MainActivity.this, MainActivity.this);
        // Init views
        mMainBtnPart = findViewById(R.id.main_btn_part);
        mMainQrPart = findViewById(R.id.main_qr_part);
        mStatusPart = findViewById(R.id.main_status_part);
        mCreateQrBtn = findViewById(R.id.btn_create_qr);
        mCreateQrBtn.setOnClickListener(this);
        mScanQrBtn = findViewById(R.id.btn_scan_qr);
        mScanQrBtn.setOnClickListener(this);
        mQrImage = findViewById(R.id.main_qr_image);
        mScanTip = findViewById(R.id.scan_tip);
        mCloseQrBtn = findViewById(R.id.close_qr_btn);
        mCloseQrBtn.setOnClickListener(this);
        mQrProgressBar = findViewById(R.id.qr_load_progress);
        mStatusTitle = findViewById(R.id.main_status_title);
        setStatusTitle(mConnectState);
        mDisconnectBtn = findViewById(R.id.btn_main_disconnect);
        mDisconnectBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (mIsDiscover) {
            mWifiP2pManager.stopPeerDiscovery(mChannel, null);
        }
        if (mMacQr != null) {
            mMacQr.recycle();
        }
        WifiP2pReceiver.getInstance().unRegister(MainActivity.this, MainActivity.this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mMainQrPart.getVisibility() == View.VISIBLE) {
            tearDown();
        } else {
            super.onBackPressed();
        }
    }

    private void tearDown() {
        stopDiscover();
        mPeers = null;
        mDesMac = null;
        mOwnerIp = null;
        mDesDevice = null;
        mIsOwner = false;
        mIsQrMode = false;
        mConnectState = STATE_CONNECT_NONE;
        setStatusTitle(mConnectState);
        mMainQrPart.setVisibility(View.GONE);
        mDisconnectBtn.setVisibility(View.GONE);
        mMainBtnPart.setVisibility(View.VISIBLE);
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
        mScanTip.setVisibility(View.VISIBLE);
        mQrImage.setImageBitmap(mMacQr);
        mCloseQrBtn.setVisibility(View.VISIBLE);
        mIsQrMode = true;
        discoverPeers();
    }

    private void discoverPeers() {
        if (mIsDiscover) {
            return;
        }
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Utils.showToast(MainActivity.this, "Wifi-p2p discovery started.");
            }

            @Override
            public void onFailure(int i) {
                Utils.showToast(MainActivity.this, "Start discover failed.");
            }
        });
    }

    private void stopDiscover() {
        if (!mIsDiscover) {
            return;
        }
        mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Utils.showToast(MainActivity.this, "Wifi-p2p discovery stopped.");
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
        setStatusTitle(mConnectState);
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
                setStatusTitle(mConnectState);
                dismissConnectingDialog();
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

    private void showDisconnectDialog() {
        DialogEntry entry = new DialogEntry(
                getStringRes(R.string.dlg_disconnect_title),
                getStringRes(R.string.dlg_disconnect_message),
                getStringRes(R.string.dlg_btn_yes),
                getStringRes(R.string.dlg_btn_cancel));
        CustomAlertDialog disconnectDialog = CustomAlertDialog.newInstance(entry);
        disconnectDialog.setPositiveListener(new SimpleListener() {
            @Override
            public void call() {
                disConnect();
            }
        });
        disconnectDialog.show(getFragmentManager(), "disconnect");
    }

    private ProgressDialog showProgressDialog(String message) {
        DialogEntry entry = new DialogEntry(
                null,
                message,
                null,
                null);
        ProgressDialog progressDialog = ProgressDialog.newInstance(entry);
        progressDialog.show(getFragmentManager(), message);
        return progressDialog;
    }

    private void dismissP2pDisableDialog() {
        if (mP2pDisableDialog == null || !mP2pDisableDialog.getDialog().isShowing()) {
            return;
        }
        mP2pDisableDialog.dismiss();
    }

    private void dismissConnectingDialog() {
        if (mConnectingDialog == null || !mConnectingDialog.getDialog().isShowing()) {
            return;
        }
        mConnectingDialog.dismiss();
    }

    private void setStatusTitle(String status) {
        String result = getResources().getString(R.string.txt_status, status);
        mStatusTitle.setText(Html.fromHtml(result));
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
                    mConnectingDialog = showProgressDialog(STATE_CONNECTING);
                    setStatusTitle(STATE_CONNECTING);
                } else {
                    Utils.showToast(MainActivity.this, "Invalid qr-code for Transmission.");
                }
                break;
            default:
                break;
        }
    }

    private int getQrSize() {
        return Math.min(Utils.getScreenWidth(this), Utils.getScreenHeight(this)) * 2 / 3;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_qr:
                if (mThisDevice == null) {
                    Utils.showToast(MainActivity.this,
                            "This device is empty, create qr code failed");
                    break;
                }
                mMainBtnPart.setVisibility(View.GONE);
                mMainQrPart.setVisibility(View.VISIBLE);
                if (mMacQr == null) {
                    new Thread() {
                        @Override
                        public void run() {
                            mMacQr = Utils.createQrBitmap(mThisDevice.deviceAddress, getQrSize());
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
                mIsQrMode = false;
                mMainQrPart.setVisibility(View.GONE);
                mMainBtnPart.setVisibility(View.VISIBLE);
                stopDiscover();
                setStatusTitle(mConnectState);
                break;
            case R.id.btn_main_disconnect:
                showDisconnectDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onP2pStateChanged(boolean enabled) {
        if (enabled) {
            initWifiP2pManager();
            dismissP2pDisableDialog();
        } else {
            showP2pDisableDialog();
        }
    }

    @Override
    public void onP2pPeersChanged(WifiP2pDeviceList peers) {
        mPeers = peers;
        if (TextUtils.isEmpty(mDesMac) || mConnectState.equals(STATE_CONNECTING) || mIsQrMode) {
            return;
        }
        for (WifiP2pDevice peer : mPeers.getDeviceList()) {
            String address = peer.deviceAddress;
            if (address.equals(mDesMac)) {
                mDesDevice = peer;
                connect(mDesDevice);
                break;
            }
        }
    }

    @Override
    public void onP2pConnectionChanged(NetworkInfo networkInfo, WifiP2pInfo wifip2pinfo, WifiP2pGroup wifiP2pGroup) {
        if (networkInfo.isConnected()) {
            mIsOwner = wifip2pinfo.isGroupOwner;
            mOwnerIp = wifip2pinfo.groupOwnerAddress.getHostAddress();
            WifiP2pDevice connectPeer = null;
            if (mIsOwner) {
                if (wifiP2pGroup.getClientList() != null
                        && wifiP2pGroup.getClientList().iterator().hasNext()) {
                    connectPeer = wifiP2pGroup.getClientList().iterator().next();
                }
            } else {
                connectPeer = wifiP2pGroup.getOwner();
            }
            if (mDesDevice == null) {
                mDesDevice = connectPeer;
            } else {
                if (connectPeer == null
                        || !mDesDevice.deviceAddress.equals(connectPeer.deviceAddress)) {
                    Utils.showToast(MainActivity.this, "Has connected with invalid device.");
                    return;
                }
                mDesDevice = connectPeer;
            }
            String desName = (mDesDevice == null) ? null : mDesDevice.deviceName;
            setStatusTitle(desName + " connected");
            mDisconnectBtn.setVisibility(View.VISIBLE);
            dismissConnectingDialog();
            mConnectState = STATE_CONNECTED;
        } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            tearDown();
        }
    }

    @Override
    public void onThisDeviceChanged(WifiP2pDevice thisDevice) {
        mThisDevice = thisDevice;
        mThisName = thisDevice.deviceName;
    }

    @Override
    public void onP2pDiscoveryChanged(int discoveryState) {
        mIsDiscover = (discoveryState == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED);
    }
}
