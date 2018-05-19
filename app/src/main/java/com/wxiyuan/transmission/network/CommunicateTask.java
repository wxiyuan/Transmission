package com.wxiyuan.transmission.network;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicateTask extends BaseTask {

    private static CommunicateTask instance;

    private BufferedReader mSocketReader;
    private PrintWriter mSocketWriter;

    private CommunicateTask(Socket socket) {
        super(socket);
        try {
            mSocketReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mSocketWriter = new PrintWriter(mSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isStop && !Thread.currentThread().isInterrupted()) {
            try {
                String line;
                while (!isStop && (line = mSocketReader.readLine()) != null && !TextUtils.isEmpty(line)) {
                    Log.d("communicate", "Get message = " + line);
                }
            } catch (IOException e) {
                stop();
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(final String msg) {
        new Thread() {
            public void run() {
                mSocketWriter.println(msg);
                mSocketWriter.flush();
            }
        }.start();
    }

    @Override
    public void stop() {
        isStop = false;
        Thread.currentThread().interrupt();
        close();
        instance = null;
    }

    private void close() {
        if (mSocketReader != null) {
            try {
                mSocketReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSocketWriter != null) {
            mSocketWriter.close();
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized CommunicateTask createInstance(Socket socket) {
        if (instance == null) {
            instance = new CommunicateTask(socket);
        }
        return instance;
    }

    public static synchronized CommunicateTask getInstance() {
        return instance;
    }

}
