package com.github.sebbity.sonyactioncam.stream;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.ma1co.openmemories.tweak.NativeException;
import com.github.ma1co.openmemories.tweak.Shell;
import com.github.sebbity.sonyactioncam.stream.UstreamMod.Encoder;
import com.github.sebbity.sonyactioncam.stream.UstreamMod.Wifi;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

import com.sony.imaging.app.livestreaming.shooting.WiFiStatusChangeReceiver;



public class UstreamModActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;

    public void Log(String tag, String msg) {
        listAdapter.add(String.format("%s: %s", tag, msg));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isCamera() && getPanelAspect() == 0) {
            // new action cam
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            layoutParams.width = 100;
            layoutParams.height = 80;
            getWindow().setAttributes(layoutParams);

            setTheme(R.style.ActionCamTheme);
        }

        listView = new ListView(this) {
            @Override
            public int getMaxScrollAmount() {
                return Integer.MAX_VALUE;
            }
        };
        setContentView(listView);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(listAdapter);

        listAdapter.add("UstreamModActivity");

        try {

            Wifi wifi = new Wifi(this);
            wifi.connectToWiFi();

        } catch(Error e) {
            listAdapter.add("EXCEPTION:");
            listAdapter.add(e.toString());
        }
    }

    public void onWifiConnected() {
        enableAdb();
        startEncoding();
    }

    private void startEncoding() {
        Encoder encoder = new Encoder(this);
        encoder.start();
    }

    private void enableAdb() {
        String adbStartCommand = getApplicationInfo().nativeLibraryDir + "/libadbd.so &";

        try {
            Shell.execAndroid(adbStartCommand);
        } catch (NativeException e) {
            Log("enableAdb", "EXCEPTION: " + e.toString());
        }

        String ipAddress = Formatter.formatIpAddress(((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress());
        Log("enableAdb", "ADB Enabled at " + ipAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int code = convertKeyCode(event.getScanCode());
        if (code == KeyEvent.KEYCODE_UNKNOWN)
            code = event.getKeyCode();
        return super.dispatchKeyEvent(new KeyEvent(event.getAction(), code));
    }

    public boolean isCamera() {
        return "sony".equals(Build.BRAND) && "ScalarA".equals(Build.MODEL) && "dslr-diadem".equals(Build.DEVICE);
    }

    public int getPanelAspect() {
        return ScalarProperties.getInt(ScalarProperties.PROP_DEVICE_PANEL_ASPECT, -1);
    }

    public int getSubLcdType() {
        return ScalarProperties.getInt(ScalarProperties.PROP_DVICE_SUBLCD_TYPE, -1);
    }

    public int convertKeyCode(int scanCode) {
        switch (scanCode) {
            case ScalarInput.ISV_KEY_UP:
            case ScalarInput.ISV_KEY_IR_UP:
            case ScalarInput.ISV_KEY_LEFT:// action cam
                return KeyEvent.KEYCODE_DPAD_UP;
            case ScalarInput.ISV_KEY_DOWN:
            case ScalarInput.ISV_KEY_IR_DOWN:
            case ScalarInput.ISV_KEY_RIGHT:// action cam
                return KeyEvent.KEYCODE_DPAD_DOWN;
            case ScalarInput.ISV_KEY_ENTER:
            case ScalarInput.ISV_KEY_IR_ENTER:
            case ScalarInput.ISV_KEY_STASTOP:
            case ScalarInput.ISV_KEY_IR_STASTOP:
            case ScalarInput.ISV_KEY_IR_RIGHT:// camcorder
                return KeyEvent.KEYCODE_DPAD_CENTER;
            case ScalarInput.ISV_KEY_MENU:
            case ScalarInput.ISV_KEY_IR_MENU:
            case ScalarInput.ISV_KEY_IR_LEFT:// camcorder
                return KeyEvent.KEYCODE_BACK;
            default:
                return KeyEvent.KEYCODE_UNKNOWN;
        }
    }
}
