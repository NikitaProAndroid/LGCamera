package com.lge.media;

import java.io.FileDescriptor;

public class LGAudioSystem {
    public static final int DEVICE_IN_ALL = -1065877505;
    public static final int DEVICE_IN_BLUETOOTH_A2DP = -2147352576;
    public static final int HEADSET_TYPE_ADVANCED = 2;
    public static final int HEADSET_TYPE_AUX = 3;
    public static final int HEADSET_TYPE_NONE = 0;
    public static final int HEADSET_TYPE_NORMAL = 1;
    public static final int NUM_STREAM_TYPES = 10;
    public static final int STREAM_INCALL_MUSIC = 10;

    public static native String getParameters(String str);

    public static native int setMABLControl(int i, int i2);

    public static native int setMABLEnable(int i);

    public static native int setParameters(String str);

    public static native FileDescriptor setRecordHookingEnabled(int i, int i2, int i3);

    public static native int setRingerMode(int i);

    static {
        System.loadLibrary("hook_jni");
    }
}
