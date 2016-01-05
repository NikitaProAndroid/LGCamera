package com.lge.olaworks.jni;

import com.lge.camera.util.CamLog;
import com.lge.olaworks.define.Ola_Genernal.Ola_JniLabrary;

public class OlaEngineJNI {
    private static final String TAG = "CameraApp";

    public static native String getProjectDate();

    public static native String getProjectName();

    public static native String getProjectVersion();

    static {
        try {
            System.loadLibrary(Ola_JniLabrary.OLA_JNI_LIB);
        } catch (SecurityException se) {
            CamLog.i(TAG, "SecurityException", se);
        } catch (UnsatisfiedLinkError ule) {
            CamLog.i(TAG, "UnsatisfiedLinkError", ule);
        } finally {
            CamLog.i(TAG, "OlaEngineJNI-end");
        }
    }
}
