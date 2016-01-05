package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;

public class CameraAutoFocusOnCafCallback implements AutoFocusCallback {
    private CameraAutoFocusOnCafCallbackFunction mGet;

    public interface CameraAutoFocusOnCafCallbackFunction {
        void callbackAutoFocusOnCaf(boolean z, Camera camera);
    }

    public CameraAutoFocusOnCafCallback(CameraAutoFocusOnCafCallbackFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onAutoFocus(boolean success, Camera camera) {
        if (this.mGet != null) {
            this.mGet.callbackAutoFocusOnCaf(success, camera);
        }
    }
}
