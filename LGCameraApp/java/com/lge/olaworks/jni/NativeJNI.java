package com.lge.olaworks.jni;

import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.Ola_Genernal.Ola_JniLabrary;

public class NativeJNI {
    private static final String TAG = "NativeJNI";

    public static native int calloc(int i);

    public static native void free(int i);

    public static native int getPeakMem();

    public static native int getUsedMem();

    public static native void initPeakMem();

    public static native int malloc(int i);

    static {
        try {
            System.loadLibrary(Ola_JniLabrary.OLA_JNI_LIB);
        } catch (SecurityException se) {
            CamLog.i(TAG, "SecurityException", se);
        } catch (UnsatisfiedLinkError ule) {
            CamLog.i(TAG, "UnsatisfiedLinkError", ule);
        } finally {
            CamLog.i(TAG, "NativeJNI-end");
        }
    }
}
