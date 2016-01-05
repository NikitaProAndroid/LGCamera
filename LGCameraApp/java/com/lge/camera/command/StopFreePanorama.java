package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class StopFreePanorama extends Command {
    public StopFreePanorama(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "StopFreePanorama executed");
        this.mGet.stopFreePanorama();
    }
}
