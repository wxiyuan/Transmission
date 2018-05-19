package com.wxiyuan.transmission.network;

import java.net.Socket;

public abstract class BaseTask implements Runnable {

    protected boolean isStop = false;
    protected Socket mSocket;

    public BaseTask(Socket socket) {
        mSocket = socket;
    }

    public abstract void stop();

}
