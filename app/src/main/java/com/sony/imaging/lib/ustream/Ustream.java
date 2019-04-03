package com.sony.imaging.lib.ustream;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.github.sebbity.sonyactioncam.stream.UstreamModActivity;

import java.util.List;

public class Ustream {
    public static final int AllViewerCountListener = 600;
    public static final int AuthenticationError = 100;
    public static final int BroadcastStarted = 1;
    public static final int BroadcastStateCallback = 1000;
    public static final int BroadcastStopped = 0;
    public static final int CannotDropVideo = 311;
    public static final int CannotSaveVideoDetails = 310;
    public static final int ConnectionError = 300;
    public static final int ErrorCallback = 100;
    public static final int InvalidChannelError = 101;
    public static final int InvalidMACAgeError = 102;
    public static final int LowBandwidthError = 301;
    public static final int OverrideStateCallback = 200;
    public static final int RecordLimitExceeded = 201;
    public static final int RecordTimerWarningCallback = 300;
    public static final int RecordingError = 200;
    private static final int STOP_WAIT_TIME = 40000;
    public static final int SessionStateCallback = 400;
    public static final int VideoWasTooShort = 312;
    public static final int ViewerCountListener = 500;

    private int mBroadcastState;
    private Object mLockStop;
    private boolean mStopEnd;
    private Object mWaitStoped;

    private UstreamModActivity activity;

    private native void _start();

    private native void _stop();

    private native void addTag(String tag);

    private native void clearTags();

    private native void setSaveVideo(boolean enableRecordMode, String videoTitle, String videoDescription);

    private native void setWorkBuffer(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    public native void init();

    public native void readStart(int i);

    public native void readStop();

    public native void setAudioSize(int i);

    public native void setCameraInfo(String modelName, String macAddress);

    public native void setChannel(String channelId);

    public native void setUserInfo(String macId, String macSecret, long macIssueTime);

    public native void setVideoSize(int videoWidth, int videoHeight, int bitrate, int fpsInMilliseconds, int unknown_Always1001);

    public native void term();

    static {
        LibraryLoader.init();
    }

    public Ustream(UstreamModActivity a) {
        this.mBroadcastState = 0;
        this.mWaitStoped = new Object();
        this.mLockStop = new Object();
        this.activity = a;
    }

    private void Log(String tag, String msg) {
        activity.Log(tag, msg);
    }

    public void start() {
        this.mBroadcastState = 0;
        _start();
    }

    public void stop() {
        Log("UstreamLib", "stop call.");
        synchronized (this.mLockStop) {
            Log("UstreamLib", "stop start.");
            this.mStopEnd = false;
            synchronized (this.mWaitStoped) {
                new Thread() {
                    public void run() {
                        Ustream.this._stop();
                        Ustream.this.mStopEnd = true;
                        synchronized (Ustream.this.mWaitStoped) {
                            Ustream.this.mWaitStoped.notifyAll();
                        }
                    }
                }.start();
                Log("UstreamLib", "waiting 40000msec");
                try {
                    this.mWaitStoped.wait(40000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log("UstreamLib", "mStopEnd=" + this.mStopEnd);
            if (this.mStopEnd) {
            } else {
                throw new RuntimeException("_stop() timeout!!");
            }
        }
    }

    public void setSaveVideo(boolean save, String title, String description, List<String> tags) {
        clearTags();
        if (title == null) {
            title = "";
        }
        if (description == null) {
            description = "";
        }
        setSaveVideo(save, title, description);
        if (tags != null) {
            for (String tag : tags) {
                if (!(tag == null || tag.isEmpty())) {
                    addTag(tag);
                }
            }
        }
    }

    public void setWorkBuffer(StreamBuffer buffer) {
        if (buffer != null) {
            setWorkBuffer(buffer.getAddr(), buffer.getSize(), buffer.StatusAreaScalarSize, buffer.StatusAreaMCodecSize, buffer.VideoInfoSize, buffer.AudioInfoSize, buffer.VideoESBufferSize, buffer.AudioESBufferSize);
            return;
        }
        setWorkBuffer(0, 0, 0, 0, 0, 0, 0, 0);
    }

    /* Access modifiers changed, original: protected */
    public void onEvent(int what, int code) {
        Log("UstreamLib", "onEvent what=" + what + ", code=" + code);

        switch (what) {
            case 100:
                Log("UstreamLib", "ErrorCallback: " + code);
                break;
            case 200:
                Log("UstreamLib", "OverrideStateCallback: " + code);
                break;
            case 300:
                Log("UstreamLib", "RecordTimerWarningCallback: " + code);
                break;
            case SessionStateCallback /*400*/:
                Log("UstreamLib", "SessionStateCallback: " + code);
                break;
            case ViewerCountListener /*500*/:
                Log("UstreamLib", "ViewerCountListener: " + code);
                break;
            case 600:
                Log("UstreamLib", "AllViewerCountListener: " + code);
                break;
            case BroadcastStateCallback /*1000*/:
                if (this.mBroadcastState != code) {
                    this.mBroadcastState = code;
                    Log("UstreamLib", "BroadcastStateCallback: " + code);
                    break;
                }
                return;
        }
    }
}
