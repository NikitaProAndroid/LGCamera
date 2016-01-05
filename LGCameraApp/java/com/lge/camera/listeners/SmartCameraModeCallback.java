package com.lge.camera.listeners;

import android.hardware.Camera;
import com.lge.camera.util.CamLog;
import com.lge.hardware.LGCamera.CameraDataCallback;

public class SmartCameraModeCallback implements CameraDataCallback {
    private static final String TAG = "CameraApp";
    private SmartCameraModeFunction mGet;
    private int[] statsdata;

    public interface SmartCameraModeFunction {
        void doSmartCameraModeCallback(int[] iArr);
    }

    public SmartCameraModeCallback(SmartCameraModeFunction function) {
        this.mGet = null;
        this.statsdata = new int[10];
        this.mGet = function;
    }

    public void onCameraData(int[] data, Camera camera) {
        CamLog.d(TAG, "SmartCameraModeCallback, get Object callback");
        System.arraycopy(data, 0, this.statsdata, 0, 10);
        this.mGet.doSmartCameraModeCallback(this.statsdata);
    }
}
