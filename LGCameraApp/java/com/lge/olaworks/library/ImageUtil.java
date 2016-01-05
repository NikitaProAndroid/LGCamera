package com.lge.olaworks.library;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.lge.camera.util.CamLog;

public class ImageUtil {
    private static final String TAG = "CameraApp";

    public static Bitmap makeBitmap(byte[] jpegData) {
        return makeBitmap(jpegData, 1);
    }

    public static Bitmap makeBitmap(byte[] jpegData, int sampleSize) {
        Bitmap bitmap = null;
        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
            if (!(options.mCancel || options.outWidth == -1 || options.outHeight == -1)) {
                options.inSampleSize = sampleSize;
                options.inJustDecodeBounds = false;
                options.inDither = true;
                options.inPreferredConfig = Config.ARGB_8888;
                bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
            }
        } catch (OutOfMemoryError ex) {
            CamLog.e(TAG, "Got oom exception ", ex);
        }
        return bitmap;
    }

    public static void Assert(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }
}
