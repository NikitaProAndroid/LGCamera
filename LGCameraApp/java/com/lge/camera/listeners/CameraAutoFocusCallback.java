package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;

public class CameraAutoFocusCallback implements AutoFocusCallback {
    private CameraAutoFocusCallbackFunction mGet;

    public interface CameraAutoFocusCallbackFunction {
        void callbackOnAutoFocus(boolean z, Camera camera);
    }

    public CameraAutoFocusCallback(CameraAutoFocusCallbackFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onAutoFocus(boolean focused, Camera camera) {
        if (this.mGet != null) {
            this.mGet.callbackOnAutoFocus(focused, camera);
        }
    }
}
