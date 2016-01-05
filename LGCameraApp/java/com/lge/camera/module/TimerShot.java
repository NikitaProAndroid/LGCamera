package com.lge.camera.module;

import com.lge.camera.ControllerFunction;

public class TimerShot extends Module {
    public TimerShot(ControllerFunction function) {
        super(function);
    }

    public boolean takePicture() {
        return true;
    }

    public void doAfterCapture() {
    }

    public boolean checkCurrentShotMode() {
        return true;
    }

    public boolean isRunning() {
        return false;
    }

    public void stopByUserAction() {
    }
}
