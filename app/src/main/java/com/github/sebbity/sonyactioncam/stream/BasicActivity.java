package com.github.sebbity.sonyactioncam.stream;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarInput;
import com.sony.scalar.sysutil.ScalarProperties;

import java.util.List;

//

public class BasicActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;

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

        listAdapter.add("fooz");

//        TextView textView = new TextView(this);

        try {

            MediaRecorder mediaRecorder = new MediaRecorder();
            MediaRecorder.Parameters sonyParams = mediaRecorder.getSupportedParameters();


//            for (int i = 0; i < numCodecs; i++) {
//                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
//
//                listAdapter.add(codecInfo.getName());
//                listAdapter.add(String.format("isEncoder: %b", codecInfo.isEncoder()));
//
//                String[] types = codecInfo.getSupportedTypes();
//                for (int j = 0; j < types.length; j++) {
//                    listAdapter.add("type: " + types[j]);
//                }
//            }

//            listAdapter.add(sonyParams.getOutputFormat());
//            listAdapter.add(sonyParams.getVideoAspectRatio());
//            listAdapter.add(sonyParams.getVideoFrameRate());
//            listAdapter.add(sonyParams.getVideoSize());
            listAdapter.add("Camcorder profiles");

            List<MediaRecorder.CamcorderProfile> supportedCamcoderProfiles = sonyParams.getSupportedCamcoderProfiles();

            if (supportedCamcoderProfiles != null) {
                for (MediaRecorder.CamcorderProfile c : supportedCamcoderProfiles) {
                    listAdapter.add(c.outputFormat);
                    listAdapter.add(c.videoAspectRatio);
                    listAdapter.add(c.videoEncodingBitRate);
                    listAdapter.add(c.videoFrameRate);
                    listAdapter.add(c.videoSize);
                    listAdapter.add("---");
                }
            }
        } catch(Error e) {
            listAdapter.add("EXCEPTION:");
            listAdapter.add(e.toString());
        }

//        String output = "af"; //String.format("pic: %s\nprev: %s\n vid: %s", supportedPictureSizes, supportedPreviewSizes, supportedVideoSizes);

//        output += sonyParams;

//        if (supportedVideoSizes != null) {
//            output += String.format("vid: %d\n", supportedVideoSizes.size());
//
//            for (Camera.Size s: supportedVideoSizes) {
//                output += String.format("%dx%d, ", s.width, s.height);
//            }
//
//            output += "\n";
//        }

//        textView.setText(output);
//        setContentView(textView);

//        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
//        listView.setAdapter(listAdapter);
//
//        listView.setOnItemClickListener((adapterView, view, pos, id) -> {
//            startActivity(new Intent().setComponent(listAdapter.getItem(pos).getComponentName()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//        });

        if (isCamera() && getSubLcdType() == 1) {
//            // old action cam
//            scroller = new Scroller(SubLCD.getStringLength(SubLCD.Sub.LID_INFOMATION)) {
//                @Override
//                public void display(String text) {
//                    SubLCD.setString(SubLCD.Sub.LID_INFOMATION, !"".equals(text), SubLCD.PTN_ON, text);
//                }
//            };
//
//            listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
//                    scroller.setText(listAdapter.getItem(pos).toString());
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {
//                    scroller.setText("");
//                }
//            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        listAdapter.clear();
//        Intent[] intents = {
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
//        };
//        for (Intent intent : intents) {
//            for (ResolveInfo info : getPackageManager().queryIntentActivities(intent, 0)) {
//                if (!getComponentName().getClassName().equals(info.activityInfo.name)) {
//                    String label = info.activityInfo.loadLabel(getPackageManager()).toString();
//                    label = label.replaceAll("\\s+", " ");
//                    listAdapter.add(new AppInfo(info.activityInfo, label));
//                }
//            }
//        }

//        if (scroller != null) {
//            int pos = listView.getSelectedItemPosition();
//            scroller.setText(pos >= 0 ? listAdapter.getItem(pos).toString() : "");
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (scroller != null)
//            scroller.setText("");
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
