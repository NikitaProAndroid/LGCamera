package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;

public class ResetFreePanorama extends Command {
    public ResetFreePanorama(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ResetFreePanorama executed");
        this.mGet.runOnUiThread(new Runnable() {
            public void run() {
                ResetFreePanorama.this.mGet.removePostRunnable(this);
                ResetFreePanorama.this.mGet.restartFreePanorama();
            }
        });
    }
}
