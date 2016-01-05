package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class RawPictureCallback implements PictureCallback {

    public interface RawCallbackFunction {
        void RawCallbackOnPictureTaken(byte[] bArr, Camera camera);
    }

    public void onPictureTaken(byte[] rawData, Camera camera) {
    }
}
