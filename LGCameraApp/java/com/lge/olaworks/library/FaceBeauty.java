package com.lge.olaworks.library;

import android.graphics.Bitmap;
import com.lge.olaworks.datastruct.JOlaBitmap;
import com.lge.olaworks.jni.OlaFaceBeautyJNI;

public class FaceBeauty extends BaseEngine {
    public static final String ENGINE_TAG = "FaceBeauty";
    public static final String TAG = "CameraApp";
    private int mStrength;
    private int mWhitening;

    public FaceBeauty() {
        this.mWhitening = -1;
        this.mStrength = -1;
    }

    public String getTag() {
        return ENGINE_TAG;
    }

    public int create() {
        return 0;
    }

    public int destroy() {
        return 0;
    }

    public int processPreview(JOlaBitmap olaBitmap) {
        return OlaFaceBeautyJNI.processImage(olaBitmap, this.mWhitening, this.mStrength, 0);
    }

    public int processImage(Bitmap bitmap, int orientation) {
        return OlaFaceBeautyJNI.processImage(bitmap, this.mWhitening, this.mStrength, 0);
    }

    public boolean needRenderMode() {
        return true;
    }

    public void setParameter(int whitening, int strength) {
        this.mWhitening = whitening;
        this.mStrength = strength;
    }
}
