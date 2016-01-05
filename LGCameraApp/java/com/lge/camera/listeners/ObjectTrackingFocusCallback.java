package com.lge.camera.listeners;

import android.hardware.Camera;
import com.lge.camera.properties.ModelProperties;
import com.lge.hardware.LGCamera.CameraDataCallback;

public class ObjectTrackingFocusCallback implements CameraDataCallback {
    private ObjectTrackingFunction mGet;
    private int[] statsdata;

    public interface ObjectTrackingFunction {
        void doObjectTrackingFocusCallback(int[] iArr);
    }

    public ObjectTrackingFocusCallback(ObjectTrackingFunction function) {
        this.mGet = null;
        this.statsdata = new int[5];
        this.mGet = function;
    }

    public void onCameraData(int[] data, Camera camera) {
        synchronized (this.statsdata) {
            System.arraycopy(data, 0, this.statsdata, 0, ModelProperties.isRenesasISP() ? 4 : 5);
            this.mGet.doObjectTrackingFocusCallback(this.statsdata);
        }
    }
}
