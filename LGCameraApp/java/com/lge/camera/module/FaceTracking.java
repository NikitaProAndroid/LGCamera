package com.lge.camera.module;

import com.lge.camera.ControllerFunction;

public class FaceTracking extends Module {
    public FaceTracking(ControllerFunction function) {
        super(function);
    }

    public boolean checkCurrentShotMode() {
        return true;
    }

    public boolean takePicture() {
        return true;
    }

    public void doAfterCapture() {
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }
}
