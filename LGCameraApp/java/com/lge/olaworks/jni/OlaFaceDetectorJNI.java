package com.lge.olaworks.jni;

import android.graphics.Bitmap;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.datastruct.Ola_FaceDetectorInfo;
import com.lge.olaworks.define.Ola_Genernal.Ola_JniLabrary;

public class OlaFaceDetectorJNI {
    private static final String TAG = "CameraApp";

    public static native int create();

    public static native int destroy();

    public static native int getProcessInfo(Ola_FaceDetectorInfo ola_FaceDetectorInfo);

    public static native int initialize();

    public static native int processImageBitmap(Bitmap bitmap, int i);

    public static native int processImageRaw(JOlaBitmap jOlaBitmap);

    public static native int processPreviewBitmap(Bitmap bitmap, int i);

    public static native int processPreviewRaw(JOlaBitmap jOlaBitmap);

    static {
        try {
            System.loadLibrary(Ola_JniLabrary.OLA_JNI_LIB);
        } catch (SecurityException se) {
            CamLog.i(TAG, "SecurityException", se);
        } catch (UnsatisfiedLinkError ule) {
            CamLog.i(TAG, "UnsatisfiedLinkError", ule);
        } finally {
            CamLog.i(TAG, "OlaFaceDetectorJNI-end");
        }
    }
}
