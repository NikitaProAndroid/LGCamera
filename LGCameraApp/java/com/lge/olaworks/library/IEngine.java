package com.lge.olaworks.library;

import android.graphics.Bitmap;
import com.lge.olaworks.datastruct.JOlaBitmap;

/* compiled from: BaseEngine */
interface IEngine {
    int create();

    int destroy();

    void enable(boolean z);

    String getTag();

    boolean needRenderMode();

    int processImage(Bitmap bitmap, int i);

    int processPreview(JOlaBitmap jOlaBitmap);
}
