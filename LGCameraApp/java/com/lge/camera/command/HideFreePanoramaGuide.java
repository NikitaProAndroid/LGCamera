package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class HideFreePanoramaGuide extends Command {
    public HideFreePanoramaGuide(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "HideFreePanoramaGuide executed");
        this.mGet.hideFreePanoramaTakingGuide();
    }

    public void execute(Object obj) {
        CamLog.d(FaceDetector.TAG, "TestCommand obj executed");
    }
}
