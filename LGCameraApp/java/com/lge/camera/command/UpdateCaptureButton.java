package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class UpdateCaptureButton extends Command {
    public UpdateCaptureButton(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "UpdateCaptureButton");
        CamLog.d(FaceDetector.TAG, "video state: " + this.mGet.getVideoState());
        this.mGet.setShutterButtonImage(true, this.mGet.getOrientationDegree());
    }
}
