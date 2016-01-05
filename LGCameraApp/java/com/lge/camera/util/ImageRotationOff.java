package com.lge.camera.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.net.Uri;

public class ImageRotationOff extends ImageHandler {
    public void resetRotation() {
    }

    public boolean setRotation(Parameters param, int rotation) {
        return false;
    }

    public void startOlaPanorama(Parameters param, int rotation) {
    }

    public Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, int degree, boolean isBurst) {
        return ImageManager.addImage(cr, title, dateTaken, location, directory, filename, degree, isBurst);
    }

    public Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, Bitmap source, byte[] jpegData, int degree, boolean isBurst) {
        return ImageManager.addImage(cr, title, dateTaken, location, directory, filename, source, jpegData, degree, isBurst);
    }

    public boolean saveTempFileForTimeMachineShot(byte[] jpegData, String directory, String filename, String ext) {
        return ImageManager.saveTempFileForTimeMachineShot(jpegData, directory, filename, ext);
    }

    public int saveContiShotImage(byte[] data, String filename, int rotation, int width, int height) {
        return -1;
    }

    public Bitmap getImage(Bitmap bmp, int rotation, boolean mirror) {
        return bmp;
    }

    public Uri addJpegImage(ContentResolver cr, String title, long dateTaken, byte[] jpegData, Location location, String directory, String filename, int degree, boolean isBurst) {
        return ImageManager.addJpegImage(cr, title, dateTaken, jpegData, location, directory, filename, degree, isBurst);
    }

    public byte[] convertYuvToJpeg(byte[] data, int width, int height, int rotation, int quality) {
        return null;
    }
}
