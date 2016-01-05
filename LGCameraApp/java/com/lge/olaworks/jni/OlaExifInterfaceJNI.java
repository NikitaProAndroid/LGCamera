package com.lge.olaworks.jni;

import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.Ola_ExifGpsUrational;
import com.lge.olaworks.datastruct.Ola_ExifInfo.Ifd;
import com.lge.olaworks.datastruct.Ola_ExifInfo.Img;
import com.lge.olaworks.datastruct.Ola_ExifInfo.Private;
import com.lge.olaworks.datastruct.Ola_ExifUrational;
import com.lge.olaworks.define.Ola_Genernal.Ola_JniLabrary;

public class OlaExifInterfaceJNI {
    private static final String TAG = "CameraApp";

    public static native int create();

    public static native int destroy();

    public static native byte[] generate();

    public static native int get(int i, int i2, Ifd ifd);

    public static native int initialize(Img img, Img img2, Private privateR);

    public static native int load(Img img);

    public static native int setByteArray(int i, int i2, int i3, int i4, byte[] bArr);

    public static native int setGpsUrational(int i, int i2, int i3, int i4, Ola_ExifGpsUrational ola_ExifGpsUrational);

    public static native int setInt(int i, int i2, int i3, int i4, int i5);

    public static native int setPrimaryImg(byte[] bArr);

    public static native int setThumbNail(int i, byte[] bArr);

    public static native int setUrational(int i, int i2, int i3, int i4, Ola_ExifUrational ola_ExifUrational);

    public static native byte[] stripJpegHeader(byte[] bArr);

    static {
        try {
            System.loadLibrary(Ola_JniLabrary.OLA_JNI_LIB);
        } catch (SecurityException se) {
            CamLog.i(TAG, "SecurityException", se);
        } catch (UnsatisfiedLinkError ule) {
            CamLog.i(TAG, "UnsatisfiedLinkError", ule);
        } finally {
            CamLog.i(TAG, "OlaExifInterfaceJNI-end");
        }
    }
}
