package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ExitInteraction extends Command {
    public ExitInteraction(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ExitInteraction");
        if (checkMediator()) {
            this.mGet.resetScreenTimeout();
        }
    }
}
