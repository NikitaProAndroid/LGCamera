package com.lge.camera.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.net.Uri;
import com.lge.camera.components.RotateView;

public abstract class ImageHandler {
    protected static final String TAG = "CameraApp";

    public abstract Uri addImage(ContentResolver contentResolver, String str, long j, Location location, String str2, String str3, int i, boolean z);

    public abstract Uri addImage(ContentResolver contentResolver, String str, long j, Location location, String str2, String str3, Bitmap bitmap, byte[] bArr, int i, boolean z);

    public abstract Uri addJpegImage(ContentResolver contentResolver, String str, long j, byte[] bArr, Location location, String str2, String str3, int i, boolean z);

    public abstract byte[] convertYuvToJpeg(byte[] bArr, int i, int i2, int i3, int i4);

    public abstract Bitmap getImage(Bitmap bitmap, int i, boolean z);

    public abstract void resetRotation();

    public abstract int saveContiShotImage(byte[] bArr, String str, int i, int i2, int i3);

    public abstract boolean saveTempFileForTimeMachineShot(byte[] bArr, String str, String str2, String str3);

    public abstract boolean setRotation(Parameters parameters, int i);

    public abstract void startOlaPanorama(Parameters parameters, int i);

    protected Bitmap getRotated(Bitmap bmp, int degree) {
        return getRotated(bmp, degree, false);
    }

    protected Bitmap getRotated(Bitmap bmp, int degree, boolean mirror) {
        if (bmp == null) {
            return null;
        }
        if (degree == 0 && !mirror) {
            return bmp;
        }
        Matrix matrix = new Matrix();
        if (mirror) {
            matrix.setScale(-1.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        }
        if (degree != 0) {
            matrix.postRotate((float) degree);
        }
        Bitmap rotated = null;
        try {
            rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        } catch (OutOfMemoryError err) {
            Bitmap tmp = Bitmap.createScaledBitmap(bmp, bmp.getHeight() / 4, bmp.getWidth() / 4, false);
            rotated = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, false);
            tmp.recycle();
            CamLog.e(TAG, "error occurred rotating image because of OutOfMemory", err);
        } finally {
            if (bmp != null) {
                bmp.recycle();
            }
        }
        return matrix != null ? rotated : rotated;
    }

    public Bitmap getRotatedNotRecycle(Bitmap bmp, int rotation, boolean mirror) {
        if (rotation == 0 && !mirror) {
            return bmp;
        }
        Bitmap rotated;
        Matrix matrix = new Matrix();
        if (mirror) {
            matrix.setScale(-1.0f, RotateView.DEFAULT_TEXT_SCALE_X);
        }
        if (rotation != 0) {
            matrix.postRotate((float) rotation);
        }
        try {
            rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        } catch (OutOfMemoryError err) {
            Bitmap tmp = Bitmap.createScaledBitmap(bmp, bmp.getHeight() / 4, bmp.getWidth() / 4, false);
            rotated = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, false);
            CamLog.e(TAG, "error occurred rotating image because of OutOfMemory", err);
        }
        if (matrix != null) {
        }
        return rotated;
    }
}
