package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class JpegPictureCallback implements PictureCallback {
    private JpegCallbackFunction mGet;

    public interface JpegCallbackFunction {
        void jpegCallbackOnPictureTaken(byte[] bArr, Camera camera);
    }

    public JpegPictureCallback(JpegCallbackFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onPictureTaken(byte[] jpegData, Camera camera) {
        if (this.mGet != null) {
            this.mGet.jpegCallbackOnPictureTaken(jpegData, camera);
        }
    }
}
