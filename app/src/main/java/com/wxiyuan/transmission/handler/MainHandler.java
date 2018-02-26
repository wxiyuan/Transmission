package com.wxiyuan.transmission.handler;

import android.os.Handler;
import android.os.Message;

import com.wxiyuan.transmission.MainActivity;

import java.lang.ref.WeakReference;

public class MainHandler extends Handler {

    public static final int MSG_QR_CODE_READY = 0x10;

    private WeakReference<MainActivity> activity;

    public MainHandler(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_QR_CODE_READY:
                if (activity == null || activity.get() == null) {
                    return;
                }
                activity.get().displayQrCode();
                break;
            default:
                break;
        }
    }

}
