package com.lge.camera.listeners;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;

public class CamcorderContinuousFocusCallback implements AutoFocusCallback {
    private CamcorderCAFCallbackFunction mGet;

    public interface CamcorderCAFCallbackFunction {
        void doCamcorderContinuousFocusCallback(boolean z);
    }

    public CamcorderContinuousFocusCallback(CamcorderCAFCallbackFunction function) {
        this.mGet = function;
    }

    public void onAutoFocus(boolean focusedState, Camera camera) {
        this.mGet.doCamcorderContinuousFocusCallback(focusedState);
    }
}
