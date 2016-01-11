package com.lge.media;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import java.io.FileDescriptor;
import java.lang.reflect.Field;

public class MediaRecorderEx extends MediaRecorder {
    public static final int MEDIA_RECORDER_TARS_STATE_INFO = 999;
    public static final int OUTPUTFORMAT_AAC_TARS = 99;
    private static final String TAG = "LGMediaRecorder";

    public static class LGAudioSource {
        public static int FM_RX;

        static {
            try {
                Field f = Class.forName("android.media.MediaRecorder$AudioSource").getField("FM_RX");
                f.setAccessible(true);
                FM_RX = Integer.parseInt(f.get(null).toString());
                if (SystemProperties.get("ro.lge.chip.vendor").equals("mtk")) {
                    FM_RX = MediaRecorderEx.OUTPUTFORMAT_AAC_TARS;
                }
                Log.d(MediaRecorderEx.TAG, "FM_RX : " + FM_RX);
            } catch (Exception e) {
                Log.d(MediaRecorderEx.TAG, "FM_RX Exception : " + e.toString());
            }
        }
    }

    private native void native_audiozoom() throws IllegalStateException;

    private native void native_changeMaxFileSize(long j) throws IllegalStateException;

    private native void native_pause() throws IllegalStateException;

    private native void native_resume() throws IllegalStateException;

    private native void native_setAudioZoomExceptionCase() throws IllegalStateException;

    private native void native_setOutputFileFD(FileDescriptor fileDescriptor, long j, long j2) throws IllegalStateException;

    private native void native_setParameter(String str) throws IllegalStateException;

    private native void native_setRecordAngle(int i) throws IllegalStateException;

    private native void native_setRecordZoomEnable(int i, int i2) throws IllegalStateException;

    static {
        System.loadLibrary("hook_jni");
    }

    public MediaRecorderEx() {
        Log.e(TAG, "MediaRecorder constructor");
    }

    protected void finalize() {
        super.finalize();
    }

    public void setAudioZooming() {
        native_audiozoom();
        Log.v(TAG, "MediaRecorder setAudioZooming");
    }

    public void setAudioZoomExceptionCase() {
        native_setAudioZoomExceptionCase();
        Log.v(TAG, "MediaRecorder setAudioZoomExceptionCase");
    }

    public void setRecordAngle(int angle) {
        native_setRecordAngle(angle);
        Log.v(TAG, "MediaRecorder setRecordAngle");
    }

    public void setRecordZoomEnable(int angle, int zoommode) {
        native_setRecordZoomEnable(angle, zoommode);
        Log.v(TAG, "MediaRecorder setRecordZoomEnable");
    }

    public void pause() throws IllegalStateException {
        native_pause();
        Log.w(TAG, "mediarecorder pause");
    }

    public void resume() throws IllegalStateException {
        native_resume();
        Log.w(TAG, "mediarecorder resume");
    }

    public void setParameter(String nameValuePair) throws IllegalStateException {
        if (Build.IS_DEBUGGABLE) {
            Log.d(TAG, "mediarecorder setParameter:" + nameValuePair);
        }
        native_setParameter(nameValuePair);
    }

    public void setOutputFileFD(FileDescriptor fd) throws IllegalStateException {
        if (Build.IS_DEBUGGABLE) {
            Log.d(TAG, "mediarecorder setOutputFileFD:" + fd);
        }
        native_setOutputFileFD(fd, 0, 0);
    }

    public void setProfile(CamcorderProfileEx profile) {
        setOutputFormat(profile.fileFormat);
        setVideoFrameRate(profile.videoFrameRate);
        setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        setVideoEncodingBitRate(profile.videoBitRate);
        setVideoEncoder(profile.videoCodec);
        if ((profile.quality < PPPOEServiceExtension.UPDATECONFIG_DELAY_MS || profile.quality > LGDisconnectCause.FACILITY_REJECTED) && profile.audioCodec >= 0) {
            setAudioEncodingBitRate(profile.audioBitRate);
            setAudioChannels(profile.audioChannels);
            setAudioSamplingRate(profile.audioSampleRate);
            setAudioEncoder(profile.audioCodec);
        }
    }

    public void changeMaxFileSize(long subsize) throws IllegalStateException {
        Log.d(TAG, "changeMaxFileSize : " + subsize);
        native_changeMaxFileSize(subsize);
    }
}
