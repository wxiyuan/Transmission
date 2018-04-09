package com.wxiyuan.transmission.network;

import com.wxiyuan.transmission.handler.MainHandler;

public class SocketManager {

    private static SocketManager instance;

    private MainHandler mainHandler;
    private SocketParams socketParams;

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void init(MainHandler handler) {
        mainHandler = handler;
    }

    public void startSocket(SocketParams params) {
        socketParams = params;
    }

    public void release() {
        socketParams = null;
        mainHandler = null;
        instance = null;
    }

}
