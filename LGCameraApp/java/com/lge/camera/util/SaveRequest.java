package com.lge.camera.util;

import android.graphics.Bitmap;

public class SaveRequest {
    public Bitmap bitmap;
    public byte[] data;
    public long dateTaken;
    public int degree;
    public byte[] exifData;
    public boolean isBurstFirst;
    public boolean isSetLastThumb;

    public SaveRequest() {
        this.exifData = null;
        this.data = null;
        this.bitmap = null;
        this.degree = 0;
        this.isSetLastThumb = false;
        this.isBurstFirst = false;
    }
}
