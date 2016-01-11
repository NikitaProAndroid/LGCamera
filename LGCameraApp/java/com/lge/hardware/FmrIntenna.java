package com.lge.hardware;

import android.util.Log;

public class FmrIntenna {
    static final int FM_INTENNA_JNI_FAILURE = -1;
    static final int FM_INTENNA_JNI_SUCCESS = 0;
    static final String TAG = "FmrIntenna";

    public static native int setIntennaNative(int i);

    static {
        Log.d(TAG, "[FMR_INTENNA]");
        System.loadLibrary("hook_jni");
    }
}
