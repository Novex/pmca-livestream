package com.github.sebbity.sonyactioncam.stream.UstreamMod;

import android.app.Activity;
import android.app.WifiSettingManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.github.sebbity.sonyactioncam.stream.UstreamModActivity;
import com.sony.imaging.app.livestreaming.shooting.WiFiStatusChangeReceiver;

import java.net.UnknownServiceException;
import java.util.Iterator;
import java.util.List;

public class Wifi {

    private static String TAG = "Wifi";
    private boolean isWiFiWorking;
    private static Boolean isWss = null;
    boolean wifi_connected;

    private UstreamModActivity activity;

    private void Log(String tag, String msg) {
        activity.Log(tag, msg);
    }

    public class CallbackWifi implements WiFiStatusChangeReceiver.Callback {
        public void onCallback(Intent intent) {
            if (!isWiFiWorking) {
                Log(TAG, "intent " + intent.getAction() + ": Not handled.");

            } else if ("android.net.wifi.supplicant.CONNECTION_CHANGE".equals(intent.getAction())) {
                Log(TAG, "SUPPLICANT_CONNECTION_CHANGE_ACTION");

            } else if ("android.net.wifi.WIFI_STATE_CHANGED".equals(intent.getAction())) {
                Log(TAG, "WIFI_STATE_CHANGED_ACTION");
                Wifi.this.handleWifiStateChanged(intent.getIntExtra("wifi_state", 4));

            } else if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
                Log(TAG, "NETWORK_STATE_CHANGED_ACTION");
                Wifi.this.handleNetworkStateChanged(intent);

            } else if ("android.net.wifi.RSSI_CHANGED".equals(intent.getAction())) {
                Log(TAG, "RSSI_CHANGED_ACTION");

            } else if ("android.net.wifi.SCAN_RESULTS".equals(intent.getAction())) {
                Log(TAG, "SCAN_RESULTS_AVAILABLE_ACTION");
                Wifi.this.handleScanResultAvailable();

            } else if ("android.net.wifi.supplicant.STATE_CHANGE".equals(intent.getAction())) {
                Log(TAG, "SUPPLICANT_STATE_CHANGED_ACTION");

            } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                Log(TAG, "CONNECTIVITY_ACTION");
            }
        }
    }

    public Wifi(UstreamModActivity a) {
        this.activity = a;
    }

    public void connectToWiFi() {
        this.isWiFiWorking = true;
        WiFiStatusChangeReceiver.getInstance().regist(activity.getApplicationContext(), new CallbackWifi());
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
             Log(TAG, "WiFi is enabled onResume");
            return;
        }

         Log(TAG, "WiFi enable start");
        if (!isEnableWss()) {
            wifiManager.setWifiEnabled(true);
        }

        // Report an error if wifi hasn't enabled within 30 seconds
        //getHandler().postDelayed(this.wifiEnableTimerRunnable, 30000);
    }

    public boolean isEnableWss() {
        if (isWss == null) {
            isWss = Boolean.FALSE;
            Object wss = activity.getSystemService("wifiSetting");
            if (wss != null && true == ((WifiSettingManager) wss).isActive()) {
                isWss = Boolean.TRUE;
            }
        }
        return isWss.booleanValue();
    }

    private void handleWifiStateChanged(int state) {
        switch (state) {
            case 0:
                Log(TAG, "WifiState DISABLING");
                return;
            case 1:
//                SubLCD.setState("LID_SUBLCD_WIFI_STATE", "LKID_SUBLCD_WIFI_STATE", true, "PTN_OFF");
                Log(TAG, "WifiState DISABLED");
                return;
            case 2:
                Log(TAG, "WifiState ENABLING");
                return;
            case 3:
                // Disable the timeout we had to check for error
//                if (this.wifiEnableTimerRunnable != null) {
//                    getHandler().removeCallbacks(this.wifiEnableTimerRunnable);
//                }

//                UstreamNotificationManager.getInstance().requestNotify(UstreamNotificationManager.WIFI_ON);
//                SubLCD.setState("LID_SUBLCD_WIFI_STATE", "LKID_SUBLCD_WIFI_STATE", true, "PTN_ON");
                Log(TAG, "WifiState ENABLED");
                connectToAccessPoint();
                return;
            case 4:
//                SubLCD.setState("LID_SUBLCD_WIFI_STATE", "LKID_SUBLCD_WIFI_STATE", true, "PTN_OFF");
                Log(TAG, "WifiState UNKNOWN");
                return;
            default:
                return;
        }
    }

    private void handleScanResultAvailable() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService("wifi");
        List<ScanResult> results = wifiManager.getScanResults();
        if (results == null) {
            Log(TAG, "Scan Result is empty");
        } else if (results.size() == 0) {
            Log(TAG, "Scan Result size is empty");
        } else {
            Log(TAG, "Size of Scan Results: " + results.size());
            Iterator<WifiConfiguration> it = wifiManager.getConfiguredNetworks().iterator();
            if (!it.hasNext()) {
                Log(TAG, "No AP setting");
            }
            boolean APinScanResult = false;
            while (it.hasNext()) {
                WifiConfiguration c = (WifiConfiguration) it.next();
                Log(TAG, "WiFiConfig: " + c.toString());
                String configuredSsid = c.SSID.substring(1, c.SSID.length() - 1);
                for (ScanResult result : results) {
                    if (configuredSsid.equals(result.SSID)) {
                        APinScanResult = true;
//                        if (this.wifiScanTimerRunnable != null) {
//                            getHandler().removeCallbacks(this.wifiScanTimerRunnable);
//                        }

//                        UstreamNotificationManager.getInstance().requestNotify(UstreamNotificationManager.SSID_KNOWN, configuredSsid);

                        Log(TAG, configuredSsid + " is available. Trying to connect.");
                        if (!wifiManager.enableNetwork(c.networkId, true)) {
                            Log(TAG, "Failed to connect to: " + configuredSsid);
                        } else {
                            return;
                        }
                    }
                }
            }
            if (!APinScanResult) {
                Log(TAG, "NO AP");
            }
            wifiManager.startScan();
        }
    }

    private void handleNetworkStateChanged(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
            if (info == null) {
                return;
            }
            if (!info.isConnectedOrConnecting()) {
                Log(TAG, "Disconnected ");
                this.wifi_connected = false;

            } else if (info.isConnected()) {
                Log(TAG, "Connected");
                this.wifi_connected = true;

                // This might need network connectivity first?
                activity.onWifiConnected();

//                UstreamNotificationManager.getInstance().requestNotify(UstreamNotificationManager.WIFI_CONNECTED);

                // Sends the modelName and macAddress to libustream via setCameraInfo
//                UstWrapper.getInstance().ustSetCameraInfo();

                // Cancel the error timeout for the connection
//                if (this.wifiConnectionTimerRunnable != null) {
//                    getHandler().removeCallbacks(this.wifiConnectionTimerRunnable);
//                }
//                if (onWiFiConnected != null) {
//                    onWiFiConnected.wifiConnected();
//                }
            } else {
                Log(TAG, "Connecting");
                this.wifi_connected = false;
            }
        }
    }

    private void connectToAccessPoint() {
        Log(TAG, "connectToAccessPoint");
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            if (wifiManager.getConfiguredNetworks().size() == 0) {
                Log(TAG, "Wi-Fi Configuration is empty");
            }

            NetworkInfo nInfo = connManager.getActiveNetworkInfo();
            if (nInfo == null) {
                Log(TAG, "Active network info is null.");
            } else if (nInfo.isConnected()) {
                Log(TAG, "Before scan network is connected");
                return;
            } else if (nInfo.isConnectedOrConnecting()) {
                Log(TAG, "Before scan, network is Connecting... ");
                return;
            } else {
                Log(TAG, "Not connected or connecting.");
            }
            if (wifiManager.pingSupplicant()) {
                Log(TAG, "Supplicant Respond. Start scanning.");
                wifiManager.startScan();
                //getHandler().postDelayed(this.wifiScanTimerRunnable, 10000);
                //getHandler().postDelayed(this.wifiConnectionTimerRunnable, 30000);
                return;
            }
            Log(TAG, "no supplicant respond");
            return;
        }
        Log(TAG, "Wi-Fi is not enabled. wait for enabled action");
    }
}
