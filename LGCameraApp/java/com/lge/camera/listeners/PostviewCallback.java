package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class PostviewCallback implements PictureCallback {

    public interface PostviewCallbackFunction {
        void PostviewCallbackOnPictureTaken(byte[] bArr, Camera camera);
    }

    public void onPictureTaken(byte[] rawData, Camera camera) {
    }
}
