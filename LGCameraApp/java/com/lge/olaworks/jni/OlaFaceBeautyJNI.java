package com.lge.olaworks.jni;

import android.graphics.Bitmap;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.define.Ola_Genernal.Ola_JniLabrary;

public class OlaFaceBeautyJNI {
    private static final String TAG = "OlaFaceBeautyJNI";

    public static native int processImage(Bitmap bitmap, int i, int i2, int i3);

    public static native int processImage(JOlaBitmap jOlaBitmap, int i, int i2, int i3);

    static {
        try {
            System.loadLibrary(Ola_JniLabrary.OLA_JNI_LIB);
        } catch (SecurityException se) {
            CamLog.i(TAG, "SecurityException", se);
        } catch (UnsatisfiedLinkError ule) {
            CamLog.i(TAG, "UnsatisfiedLinkError", ule);
        } finally {
            CamLog.i(TAG, "OlaFaceBeautyJNI-end");
        }
    }
}
