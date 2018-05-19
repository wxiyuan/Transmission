package com.wxiyuan.transmission.network;

import android.net.wifi.p2p.WifiP2pInfo;

import com.wxiyuan.transmission.Const;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerSocketManager implements Runnable {

    private static ServerSocketManager instance;

    private ServerSocket mServerSocket;
    private LinkedBlockingQueue mQueue;
    private ThreadPoolExecutor mExecutorService;
    private WifiP2pInfo wifip2pinfo;
    private boolean isStop = false;

    public ServerSocketManager(WifiP2pInfo wifip2pinfo) {
        try {
            mServerSocket = new ServerSocket(Const.SOCKET_PORT);
            this.wifip2pinfo = wifip2pinfo;
            mExecutorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
            mQueue = (LinkedBlockingQueue) mExecutorService.getQueue();
            mExecutorService.execute(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ServerSocketManager startServer(WifiP2pInfo wifip2pinfo) {
        if (instance == null) {
            instance = new ServerSocketManager(wifip2pinfo);
        }
        return instance;
    }

    @Override
    public void run() {
        while (!isStop && !Thread.currentThread().isInterrupted()) {
            Socket socket;
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            CommunicateTask communicateTask = CommunicateTask.createInstance(socket);
            mExecutorService.execute(communicateTask);
            communicateTask.sendMsg("==========This msg send from Server========");
        }
    }

    public static void stop() {
        if (instance != null) {
            if (instance.mQueue != null) {
                instance.isStop = true;
                for (Runnable task : (Iterable<Runnable>) instance.mQueue) {
                    if (task instanceof BaseTask) {
                        ((BaseTask) task).stop();
                    }
                }
                try {
                    instance.mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                instance = null;
            }
        }
    }

}
