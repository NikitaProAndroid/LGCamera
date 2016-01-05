package com.lge.olaworks.jni;

import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.datastruct.Ola_AutoPanoramaInfo;
import com.lge.olaworks.datastruct.Ola_AutoPanoramaThumbInfo;
import com.lge.olaworks.define.Ola_Genernal.Ola_JniLabrary;

public class OlaAutoPanoramaJNI {
    private static final String TAG = "OlaPanoramaJNI";

    public static native int cancel();

    public static native int create();

    public static native int destroy();

    public static native int getParam(int i);

    public static native int getStatus(Ola_AutoPanoramaInfo ola_AutoPanoramaInfo);

    public static native int initialize();

    public static native int makeThumbnail(JOlaBitmap jOlaBitmap);

    public static native int processFrame(JOlaBitmap jOlaBitmap, Ola_AutoPanoramaInfo ola_AutoPanoramaInfo);

    public static native int processSynthesis(JOlaBitmap jOlaBitmap, int i);

    public static native int setFrame(JOlaBitmap jOlaBitmap, int i);

    public static native int setParam(int i, int i2);

    public static native int setThumbnailSize(Ola_AutoPanoramaThumbInfo ola_AutoPanoramaThumbInfo, int i, int i2);

    public static native int stopProcess();

    static {
        try {
            System.loadLibrary(Ola_JniLabrary.OLA_JNI_LIB);
        } catch (SecurityException se) {
            CamLog.i(TAG, "SecurityException", se);
        } catch (UnsatisfiedLinkError ule) {
            CamLog.i(TAG, "UnsatisfiedLinkError", ule);
        } finally {
            CamLog.i(TAG, "OlaAutoPanoramaJNI-end");
        }
    }
}
