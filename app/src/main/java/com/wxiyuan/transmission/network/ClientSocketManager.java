package com.wxiyuan.transmission.network;

import android.net.wifi.p2p.WifiP2pInfo;

import com.wxiyuan.transmission.Const;

import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientSocketManager implements Runnable {

    private static ClientSocketManager instance;

    private WifiP2pInfo mWifiP2pInfo;
    private ThreadPoolExecutor mExecutorService;
    private SynchronousQueue mQueue;
    private String mServerIp = "";

    public ClientSocketManager(WifiP2pInfo wifiP2pInfo) {
        mWifiP2pInfo = wifiP2pInfo;
        mExecutorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        mQueue = (SynchronousQueue) mExecutorService.getQueue();
        mExecutorService.execute(this);
    }

    public static synchronized ClientSocketManager startClient(WifiP2pInfo wifiP2pInfo) {
        if (instance == null) {
            instance = new ClientSocketManager(wifiP2pInfo);
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            mServerIp = mWifiP2pInfo.groupOwnerAddress.getHostAddress();
            TimeUnit.SECONDS.sleep(1);
            Socket socket = new Socket(mServerIp, Const.SOCKET_PORT);
            CommunicateTask communicateTask = CommunicateTask.createInstance(socket);
            mExecutorService.execute(communicateTask);
            communicateTask.sendMsg("==========This msg send from Client========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (instance != null) {
            if (instance.mQueue != null) {
                for (Runnable task : (Iterable<Runnable>) instance.mQueue) {
                    ((BaseTask) task).stop();
                }
                instance = null;
            }
        }
    }

}
