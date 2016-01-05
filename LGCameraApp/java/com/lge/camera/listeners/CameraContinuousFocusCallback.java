package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;

public class CameraContinuousFocusCallback implements AutoFocusCallback {
    private CameraCAFCallbackFunction mGet;

    public interface CameraCAFCallbackFunction {
        void callbackOnCAFocus(boolean z, Camera camera);
    }

    public CameraContinuousFocusCallback(CameraCAFCallbackFunction function) {
        this.mGet = null;
        this.mGet = function;
    }

    public void onAutoFocus(boolean focusedState, Camera camera) {
        if (this.mGet != null) {
            this.mGet.callbackOnCAFocus(focusedState, camera);
        }
    }
}
