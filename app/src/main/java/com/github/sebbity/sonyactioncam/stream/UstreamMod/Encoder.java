package com.github.sebbity.sonyactioncam.stream.UstreamMod;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;

import com.github.sebbity.sonyactioncam.stream.UstreamModActivity;

import com.sony.imaging.lib.ustream.StreamBuffer;
import com.sony.imaging.lib.ustream.Ustream;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.hardware.DeviceBuffer;
import com.sony.scalar.media.MediaRecorder;
import com.sony.scalar.sysutil.ScalarProperties;


// import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexFile;

public class Encoder {

    static String TAG = "Encoder";

    private Ustream us;
    private StreamBuffer mBuffer;
    private MediaRecorder.StreamBuffer streamBuffer;
    private DeviceBuffer dBuffer;
    private MediaRecorder mMediaRecorder2;
    private CameraEx mCameraEx;

    private UstreamModActivity activity;

    public Encoder(UstreamModActivity a) {
        this.activity = a;
    }

    private void Log(String tag, String msg) {
        activity.Log(tag, msg);
    }

    private void Log(String msg) {
        activity.Log(TAG, msg);
    }

    // From https://stackoverflow.com/questions/7757379/dynamically-load-classes-from-other-apks
//    private Object loadClass(String packageName, String className){
//        Object plugin = null;
//        try {
//            PackageManager packageManager = activity.getPackageManager();
//            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
//            DexFile df = new DexFile(appInfo.sourceDir);
//            ClassLoader cl = activity.getClassLoader();
//            Class classToInvestigate = df.loadClass(className, cl);
//            plugin = classToInvestigate.newInstance();
//
//
//        } catch (Exception e) {
//            System.out.println("EXCEPTION");
//        }
//        finally{
//            return plugin;
//        }
//    }

    public void start() {
        // Set parameters for a local recording MediaRecorder?
        setParams();

        Log.d(TAG, "new StreamBuffer");
        SetupMediaRecorder2();

        mMediaRecorder2.setStreamBuffer(createStreamBuffer());
        mMediaRecorder2.prepare();

        SetupUstream();

        // Set parameters for the stream recording MediaRecorder?
        setParams2();

        activity.Log("Enc", "All set up!");

        mMediaRecorder2.start();

        this.us.start();
    }

    private void SetupMediaRecorder2() {
        mMediaRecorder2 = new MediaRecorder();
        mMediaRecorder2.setVideoSource(1);
        mMediaRecorder2.setVideoSource(1);

        CameraEx.OpenOptions opts = new CameraEx.OpenOptions();

        opts.setPreview(true);
        opts.setInheritSetting(true);

        opts.setRecordingMode(1);
        opts.setMovieMode(1);

        // Close the camera reference if we already have one or open() freaks out
        if (mCameraEx != null) {
            mCameraEx.release();
        }

        try {
            mCameraEx = CameraEx.open(0, opts);
        } catch (RuntimeException e) {
            Log("Exception in CameraEx.open: " + e.toString());
        }

        mMediaRecorder2.setCamera(mCameraEx);

        // MovieMultiShootingExecutor.prepareForMovieRec(mMediaRecorder2, ExecutorCreator.this.mAudioManager);
    }

    private void SetupUstream() {
        this.us = new Ustream(activity);

        //this.us.setCallback(this.ustreamCallback);

        this.us.init();
        this.us.setWorkBuffer(this.mBuffer);
        this.us.setChannel(/* channelId */ "23649863");
        this.us.setUserInfo(/* macId */ "75826bba9a2efd1b82ede34d381a2c83cd297c63", /* macSecret */ "df114176c8373075f739141a1ffd95539d8e0779", /* macIssueTime */ 1553654235);

        // Changing these doesn't seem to actually change anything in the video stream?
        int bitRate = 3145728;  // Also seen: 1048576

        int videoWidth = 1280;
        int videoHeight = 720;

        int fps = 30 * 1000;

        this.us.setVideoSize(videoWidth, videoHeight, bitRate, fps, 1001);

        this.us.setAudioSize(131072);

        boolean enableRecordMode = false;
//        if (ReadSetting.getInstance().hs.containsKey(ReadSetting.enableRecordMode)) {
//            enableRecordMode = Boolean.parseBoolean((String) ((ArrayList) ReadSetting.getInstance().hs.get(ReadSetting.enableRecordMode)).get(0));
//        }

        String videoTitle = null;
//        if (ReadSetting.getInstance().hs.containsKey(ReadSetting.videoTitle)) {
//            videoTitle = (String) ((ArrayList) ReadSetting.getInstance().hs.get(ReadSetting.videoTitle)).get(0);
//        }

        String videoDescr = null;
//        if (ReadSetting.getInstance().hs.containsKey(ReadSetting.videoDescription)) {
//            videoDescr = (String) ((ArrayList) ReadSetting.getInstance().hs.get(ReadSetting.videoDescription)).get(0);
//        }

        List<String> tag = null;
//        if (ReadSetting.getInstance().hs.containsKey(ReadSetting.videoTag)) {
//            ArrayList<String> tagList = (ArrayList) ReadSetting.getInstance().hs.get(ReadSetting.videoTag);
//            tag = Arrays.asList(tagList.toArray(new String[tagList.size()]));
//        }

        this.us.setSaveVideo(enableRecordMode, videoTitle, videoDescr, tag);

        String macAddress = ((WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        String modelName = ScalarProperties.getString("model.name");

        Log("mac: " + macAddress);
        Log("model: " + modelName);

        this.us.setCameraInfo(modelName, macAddress);
    }

    private void setParams() {
        /*
        MediaRecorder mediaRecorder = ExecutorCreator.getInstance().getMediaRecorder();
        MediaRecorder.Parameters params = mediaRecorder.getParameters();
        if (((EachExecutorCreator) ExecutorCreator.getInstance()).isRemoteCam()) {
            params.setOutputFormat("MPEG4");
            params.setVideoAspectRatio(PictureSizeController.ASPECT_16_9);
            if (ScalarProperties.getInt("signal.frequency") == 0) {
                params.setVideoFrameRate("25p");
            } else {
                params.setVideoFrameRate("30p");
            }
            params.setVideoSize("FHD_1080");
            params.setVideoEncodingBitRate("FH");
            mediaRecorder.setParameters(params);
            try {
                Log.i(TAG, "setParams(isRemoteCam) : " + params.flatten());
            } catch (Exception e) {
                Log.e(TAG, "flatten Error : " + e.getMessage());
            }
        } else if (mediaRecorder != null) {
            try {
                Log.d(TAG, "Media side parameters : " + params.flatten());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            if (MovieFormatController.AVCHD.equals(params.getOutputFormat())) {
                Log.d(TAG, "Output format is AVCHD");
                if ("25p".equals(params.getVideoFrameRate())) {
                    params.setVideoFrameRate("50i");
                    Log.d(TAG, "FrameRate Change:  25P->50I");
                } else if ("24p".equals(params.getVideoFrameRate())) {
                    params.setVideoFrameRate("60i");
                    Log.d(TAG, "FrameRate Change:  24P->60I");
                } else if ("60p".equals(params.getVideoFrameRate())) {
                    params.setVideoFrameRate("60i");
                    Log.d(TAG, "FrameRate Change:  60P->60I");
                    if ("PS".equals(params.getVideoEncodingBitRate())) {
                        params.setVideoEncodingBitRate("FX");
                        Log.d(TAG, "EncodingBitRate Change:  PS->FX");
                    }
                } else if ("50p".equals(params.getVideoFrameRate())) {
                    Log.d(TAG, "FrameRate Change:  50P->50I");
                    params.setVideoFrameRate("50i");
                    if ("PS".equals(params.getVideoEncodingBitRate())) {
                        params.setVideoEncodingBitRate("FX");
                        Log.d(TAG, "EncodingBitRate Change:  PS->FX");
                    }
                }
            } else if ("XAVCS".equals(params.getOutputFormat())) {
                Log.d(TAG, "Output format is XAVC_S");
                if ("24p".equals(params.getVideoFrameRate())) {
                    params.setVideoFrameRate("30p");
                    Log.d(TAG, "FrameRate Change:  24P->30P");
                } else if ("60p".equals(params.getVideoFrameRate())) {
                    params.setVideoFrameRate("30p");
                    Log.d(TAG, "FrameRate Change:  60P->30P");
                } else if ("50p".equals(params.getVideoFrameRate())) {
                    params.setVideoFrameRate("25p");
                    Log.d(TAG, "FrameRate Change:  50P->25P");
                }
            }
            mediaRecorder.setParameters(params);
            try {
                Log.i(TAG, "setParams : " + params.flatten());
            } catch (Exception e22) {
                Log.e(TAG, "flatten Error : " + e22.getMessage());
            }
        }
        */
    }

    private void setParams2() {
        MediaRecorder mediaRecorder2 = mMediaRecorder2;
        MediaRecorder.Parameters params2 = mediaRecorder2.getParameters();

//        params2.setVideoSize("HVGAW");
//        params2.setVideoEncodingBitRate("LP");

        params2.setVideoSize(MediaRecorder.Parameters.VIDEO_SIZE_HD_720);
        params2.setVideoEncodingBitRate(MediaRecorder.Parameters.VIDEO_ENCODING_BIT_RATE_SP);

        params2.setVideoFrameRate(MediaRecorder.Parameters.VIDEO_FRAME_RATE_30P);
        params2.setOutputFormat(MediaRecorder.Parameters.OUTPUT_FORMAT_MPEG_4);
        params2.setVideoAspectRatio(MediaRecorder.Parameters.VIDEO_ASPECT_RATIO_16_9);

        mediaRecorder2.setParameters(params2);
    }

    public MediaRecorder.StreamBuffer createStreamBuffer() {
        this.mBuffer = new StreamBuffer(((ScalarProperties.getInt("mem.rawimage.size.in.mega.pixel") * 1024) * 1024) * 2);
        this.mBuffer.StatusAreaMCodecSize = 2048;
        this.mBuffer.StatusAreaScalarSize = 2048;
        this.mBuffer.VideoInfoSize = 1048576;
        this.mBuffer.AudioInfoSize = 131072;
        this.mBuffer.VideoESBufferSize = 16777216;
        this.mBuffer.AudioESBufferSize = 4194304;
        this.mBuffer.calculate();

        this.streamBuffer = new MediaRecorder.StreamBuffer();

        this.dBuffer = this.mBuffer.getBuffer();

        this.streamBuffer.readStatusArea = new MediaRecorder.StreamBuffer.StreamMemoryArea(this.dBuffer, this.mBuffer.StatusAreaScalarOffset, this.mBuffer.StatusAreaScalarSize);
        this.streamBuffer.writeStatusArea = new MediaRecorder.StreamBuffer.StreamMemoryArea(this.dBuffer, this.mBuffer.StatusAreaMCodecOffset, this.mBuffer.StatusAreaMCodecSize);
        this.streamBuffer.videoInfo = new MediaRecorder.StreamBuffer.StreamMemoryArea(this.dBuffer, this.mBuffer.VideoInfoOffset, this.mBuffer.VideoInfoSize);
        this.streamBuffer.videoES = new MediaRecorder.StreamBuffer.StreamMemoryArea(this.dBuffer, this.mBuffer.VideoESBufferOffset, this.mBuffer.VideoESBufferSize);
        this.streamBuffer.audioInfo = new MediaRecorder.StreamBuffer.StreamMemoryArea(this.dBuffer, this.mBuffer.AudioInfoOffset, this.mBuffer.AudioInfoSize);
        this.streamBuffer.audioES = new MediaRecorder.StreamBuffer.StreamMemoryArea(this.dBuffer, this.mBuffer.AudioESBufferOffset, this.mBuffer.AudioESBufferSize);

        return this.streamBuffer;
    }
}
