package com.sony.imaging.app.livestreaming.shooting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class WiFiStatusChangeReceiver extends BroadcastReceiver {
    static WiFiStatusChangeReceiver receiver;
    private Callback mCallback;
    private boolean running = false;

    public interface Callback {
        void onCallback(Intent intent);
    }

    public static WiFiStatusChangeReceiver getInstance() {
        if (receiver == null) {
            receiver = new WiFiStatusChangeReceiver();
        }
        return receiver;
    }

    public void regist(Context c, Callback callback) {
        this.mCallback = callback;
        c.registerReceiver(this, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        c.registerReceiver(this, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
        c.registerReceiver(this, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
        c.registerReceiver(this, new IntentFilter("android.net.wifi.supplicant.CONNECTION_CHANGE"));
        c.registerReceiver(this, new IntentFilter("android.net.wifi.RSSI_CHANGED"));
        c.registerReceiver(this, new IntentFilter("android.net.wifi.supplicant.STATE_CHANGE"));
        c.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.running = true;
    }

    public void unregist(Context c) {
        if (this.running) {
            c.unregisterReceiver(this);
            this.running = false;
            this.mCallback = null;
        }
        receiver = null;
    }

    public void onReceive(Context c, Intent intent) {
        if (this.running && this.mCallback != null) {
            this.mCallback.onCallback(intent);
        }
    }
}
