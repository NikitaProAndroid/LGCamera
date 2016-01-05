package com.lge.olaworks.datastruct;

import android.graphics.Bitmap;
import android.util.Log;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ReturnType;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.jni.NativeJNI;
import com.lge.olaworks.library.OlaBitmapUtil;

public class JOlaBitmap {
    private static final int NULL = 0;
    private static final String TAG = "JOlaBitmap";
    public int height;
    public byte[] imageData;
    public int imageFormat;
    public int nativeImageData;
    public int orientation;
    public int width;

    public JOlaBitmap() {
        this.width = NULL;
        this.height = NULL;
        this.imageFormat = Ola_ImageFormat.YUVPLANAR_NV21;
        this.orientation = NULL;
        this.imageData = null;
        this.nativeImageData = NULL;
    }

    public JOlaBitmap(int w, int h, int fmt, int ori, byte[] data) {
        this.width = NULL;
        this.height = NULL;
        this.imageFormat = Ola_ImageFormat.YUVPLANAR_NV21;
        this.orientation = NULL;
        this.imageData = null;
        this.nativeImageData = NULL;
        this.width = w;
        this.height = h;
        this.imageFormat = fmt;
        this.orientation = ori;
        this.imageData = data;
    }

    public JOlaBitmap(int w, int h, int fmt, int ori, byte[] data, int dataLength) {
        this.width = NULL;
        this.height = NULL;
        this.imageFormat = Ola_ImageFormat.YUVPLANAR_NV21;
        this.orientation = NULL;
        this.imageData = null;
        this.nativeImageData = NULL;
        this.width = w;
        this.height = h;
        this.imageFormat = fmt;
        this.orientation = ori;
        this.imageData = new byte[dataLength];
        System.arraycopy(data, NULL, this.imageData, NULL, dataLength);
    }

    public JOlaBitmap(int w, int h, int fmt, int ori) {
        int size;
        this.width = NULL;
        this.height = NULL;
        this.imageFormat = Ola_ImageFormat.YUVPLANAR_NV21;
        this.orientation = NULL;
        this.imageData = null;
        this.nativeImageData = NULL;
        this.width = w;
        this.height = h;
        this.imageFormat = fmt;
        this.orientation = ori;
        switch (fmt) {
            case Ola_ShotParam.FaceEffect_Sepia /*257*/:
                size = (w * h) * 3;
                break;
            case Ola_ImageFormat.YUVPLANAR_NV21 /*1026*/:
                size = ((w * h) * 3) / 2;
                break;
            default:
                size = NULL;
                break;
        }
        if (size != 0) {
            this.nativeImageData = NativeJNI.malloc(size);
        }
    }

    public void recycle() {
        this.width = NULL;
        this.height = NULL;
        this.imageFormat = NULL;
        this.orientation = NULL;
        this.imageData = null;
        if (this.nativeImageData != 0) {
            NativeJNI.free(this.nativeImageData);
            this.nativeImageData = NULL;
        }
    }

    public void getBitmap(Bitmap dst) {
        OlaBitmapUtil.convert(dst, this);
    }

    public Bitmap getBitmap() {
        return null;
    }

    public static JOlaBitmap getNative(int w, int h, int fmt, int ori) {
        JOlaBitmap olaBitmap = new JOlaBitmap(w, h, fmt, ori);
        if (olaBitmap.nativeImageData == 0) {
            return null;
        }
        return olaBitmap;
    }

    public static JOlaBitmap getNative(Bitmap bitmap, int fmt, int ori) {
        JOlaBitmap olaBitmap = new JOlaBitmap(bitmap.getWidth(), bitmap.getHeight(), fmt, ori);
        if (olaBitmap.nativeImageData == 0) {
            return null;
        }
        if (!Ola_ReturnType.error(OlaBitmapUtil.convert(olaBitmap, bitmap))) {
            return olaBitmap;
        }
        olaBitmap.recycle();
        Log.e(TAG, String.format("CBitmap.convert ret(%d)", new Object[]{Integer.valueOf(dRet)}));
        return null;
    }
}
