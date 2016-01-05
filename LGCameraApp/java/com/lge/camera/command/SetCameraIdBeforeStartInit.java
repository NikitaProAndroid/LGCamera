package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class SetCameraIdBeforeStartInit extends Command {
    public SetCameraIdBeforeStartInit(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "SetCameraIdBeforeStartInit-start");
        this.mGet.setCameraIdBeforeStartInit();
    }
}
