package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusMoveCallback;

public class CameraAutoFocusMoveCallback implements AutoFocusMoveCallback {
    private CameraAutoFocusMoveCallbackFunction mGet;

    public interface CameraAutoFocusMoveCallbackFunction {
        void callbackOnAutoFocusMove(boolean z, Camera camera);
    }

    public CameraAutoFocusMoveCallback(CameraAutoFocusMoveCallbackFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onAutoFocusMoving(boolean start, Camera camera) {
        if (this.mGet != null) {
            this.mGet.callbackOnAutoFocusMove(start, camera);
        }
    }
}
