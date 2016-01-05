package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class PauseRecording extends Command {
    public PauseRecording(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        execute(null);
    }

    public void execute(Object obj) {
        CamLog.d(FaceDetector.TAG, "PauseRecording");
        this.mGet.pauseRecording();
    }
}
