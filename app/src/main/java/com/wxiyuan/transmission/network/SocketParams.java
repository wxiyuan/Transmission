package com.wxiyuan.transmission.network;

import android.support.annotation.NonNull;

public class SocketParams {

    public String address;
    public boolean isOwner;

    public SocketParams(@NonNull String address, boolean isOwner) {
        this.address = address;
        this.isOwner = isOwner;
    }

}
